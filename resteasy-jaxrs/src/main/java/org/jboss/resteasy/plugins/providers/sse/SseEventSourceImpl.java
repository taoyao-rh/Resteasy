package org.jboss.resteasy.plugins.providers.sse;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;

/*import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;*/
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import javax.ws.rs.Flow.Subscriber;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.InboundSseEvent;
import javax.ws.rs.sse.SseEventSource;
import javax.ws.rs.sse.SseSubscription;

import org.apache.http.HttpHeaders;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;


public class SseEventSourceImpl implements SseEventSource
{
   public static final long RECONNECT_DEFAULT = 500;

   private enum State {
      READY, OPEN, CLOSED
   }

   private WebTarget target = null;
   private final long reconnectDelay;
   private final boolean disableKeepAlive;
   private final ScheduledExecutorService executor;
   private final AtomicReference<State> state = new AtomicReference<>(State.READY);
   //Remove this after reactive api is used and tested
   /* private final List<Consumer<SseSubscription>> onSubscribeConsumers = new CopyOnWriteArrayList<>(); //TODO how to use this?
   private final List<Consumer<InboundSseEvent>> onEventConsumers = new CopyOnWriteArrayList<>();
   private final List<Consumer<Throwable>> onErrorConsumers = new CopyOnWriteArrayList<>();
   private final List<Runnable> onCompleteConsumers = new CopyOnWriteArrayList<>();*/
   private Flowable<InboundSseEvent> flowable;
   private InboundSseEventCallback eventCallback = null;
   
   public static class SourceBuilder extends Builder
   {
      private WebTarget endpoint = null;
      private long reconnect = RECONNECT_DEFAULT;
      private String name = null;
      private boolean disableKeepAlive = false;

      public SourceBuilder(final WebTarget endpoint)
      {
         this.endpoint = endpoint;
      }

      public Builder named(String name)
      {
         this.name = name;
         return this;
      }

      public SseEventSource build()
      {
         return new SseEventSourceImpl(endpoint, name, reconnect, disableKeepAlive, false);
      }

      public SseEventSource open()
      {
         // why this api is required ? build can create SseEventSource and this can be invoked against SseEventSource
         final SseEventSource source = new SseEventSourceImpl(endpoint, name, reconnect, disableKeepAlive, false);
         source.open();
         return source;
      }

      @Override
      public Builder target(WebTarget endpoint)
      {
         return new SourceBuilder(endpoint);
      }

      @Override
      public Builder reconnectingEvery(long delay, TimeUnit unit)
      {
         reconnect = unit.toMillis(delay);
         return this;
      }
   }

   public SseEventSourceImpl(final WebTarget endpoint)
   {
      this(endpoint, true);
   }

   public SseEventSourceImpl(final WebTarget endpoint, final boolean open)
   {
      this(endpoint, null, RECONNECT_DEFAULT, true, open);
   }

   private SseEventSourceImpl(final WebTarget target, String name, long reconnectDelay, final boolean disableKeepAlive,
         final boolean open)
   {
      if (target == null)
      {
         throw new IllegalArgumentException(Messages.MESSAGES.webTargetIsNotSetForEventSource());
      }
      this.target = target;
      this.reconnectDelay = reconnectDelay;
      this.disableKeepAlive = disableKeepAlive;

      if (name == null)
      {
         name = String.format("sse-event-source(%s)", target.getUri());
      }
      this.executor = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory());

      flowable = Flowable.<InboundSseEvent>create(emitter ->  {
         InboundSseEventCallback callback = new InboundSseEventCallback() {
         @Override
         public void receive(InboundSseEvent event) {
           emitter.onNext(event);
         }

         @Override
         public void error(Throwable e) {
           emitter.onError(e);
         }
         
         @Override
         public void close() {
            emitter.onComplete();
         }
       };
       this.eventCallback = callback;
     }, BackpressureStrategy.BUFFER).share();
  
      
      if (open)
      {
         open();
      }
   }

   private static class DaemonThreadFactory implements ThreadFactory
   {
      private static final AtomicInteger poolNumber = new AtomicInteger(1);
      private final ThreadGroup group;
      private final AtomicInteger threadNumber = new AtomicInteger(1);
      private final String namePrefix;

      DaemonThreadFactory()
      {
         SecurityManager s = System.getSecurityManager();
         group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
         namePrefix = "resteasy-sse-eventsource" + poolNumber.getAndIncrement() + "-thread-";
      }

      public Thread newThread(Runnable r)
      {
         Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
         t.setDaemon(true);
         return t;
      }
   }

   @Override
   public void open()
   {
      if (!state.compareAndSet(State.READY, State.OPEN))
      {
         throw new IllegalStateException(Messages.MESSAGES.eventSourceIsNotReadyForOpen());
      }
      EventHandler handler = new EventHandler(reconnectDelay, null);
      executor.submit(handler);
      handler.awaitConnected();
   }

   public boolean isOpen()
   {
      return state.get() == State.OPEN;
   }

   @Override
   public void close()
   {
      this.close(5, TimeUnit.SECONDS);
   }

   public void subscribe(Consumer<InboundSseEvent> onEvent) {
      if (onEvent == null) {
         throw new IllegalArgumentException();
      }
      //onEventConsumers.add(onEvent);
      flowable.subscribe(onEvent::accept);
   }

   public void subscribe(Consumer<InboundSseEvent> onEvent,
                  Consumer<Throwable> onError) {
      if (onEvent == null) {
         throw new IllegalArgumentException();
      }
      if (onError == null) {
         throw new IllegalArgumentException();
      }
      //onEventConsumers.add(onEvent);
      //onErrorConsumers.add(onError);
      flowable.subscribe(onEvent::accept, onError::accept);
   }

   public void subscribe(Consumer<InboundSseEvent> onEvent,
                  Consumer<Throwable> onError,
                  Runnable onComplete) {
      if (onEvent == null) {
         throw new IllegalArgumentException();
      }
      if (onError == null) {
         throw new IllegalArgumentException();
      }
      if (onComplete == null) {
         throw new IllegalArgumentException();
      }
      //onEventConsumers.add(onEvent);
      //onErrorConsumers.add(onError);
      //onCompleteConsumers.add(onComplete);
      flowable.subscribe(onEvent::accept, onError::accept, onComplete::run);
   }

   public void subscribe(Consumer<SseSubscription> onSubscribe,
                  Consumer<InboundSseEvent> onEvent,
                  Consumer<Throwable> onError,
                  Runnable onComplete) {
      if (onSubscribe == null) {
         throw new IllegalArgumentException();
      }
      if (onEvent == null) {
         throw new IllegalArgumentException();
      }
      if (onError == null) {
         throw new IllegalArgumentException();
      }
      if (onComplete == null) {
         throw new IllegalArgumentException();
      }
      
      //onSubscribeConsumers.add(onSubscribe);
      //onEventConsumers.add(onEvent);
      //onErrorConsumers.add(onError);
      //onCompleteConsumers.add(onComplete);
      flowable.subscribe(onEvent::accept, onError::accept, onComplete::run, new Consumer<org.reactivestreams.Subscription>() {
         public void accept(org.reactivestreams.Subscription sub)
         {
            onSubscribe.accept(new SseSubscription() {
               @Override
               public void request(long n)
               {
                  sub.request(n);  
               }
               @Override
               public void cancel()
               {
                 sub.cancel();               
               }
               
            });
         }
         
      } :: accept);
     
   }
   
   @Override
   public boolean close(final long timeout, final TimeUnit unit)
   {
      eventCallback.close();
      if (state.getAndSet(State.CLOSED) != State.CLOSED)
      {
         executor.shutdownNow();
      }
      try
      {
         if (!executor.awaitTermination(timeout, unit))
         {
            return false;
         }
      }
      catch (InterruptedException e)
      {
         Thread.currentThread().interrupt();
         return false;
      }
      return true;
   }

   private class EventHandler implements Runnable
   {

      private final CountDownLatch connectedLatch;
      private String lastEventId;
      private long reconnectDelay;

      public EventHandler(final long reconnectDelay, final String lastEventId)
      {
         this.connectedLatch = new CountDownLatch(1);
         this.reconnectDelay = reconnectDelay;
         this.lastEventId = lastEventId;
      }

      private EventHandler(final EventHandler anotherHandler)
      {
         this.connectedLatch = anotherHandler.connectedLatch;
         this.reconnectDelay = anotherHandler.reconnectDelay;
         this.lastEventId = anotherHandler.lastEventId;
      }

      @Override
      public void run()
      {
         SseEventInputImpl eventInput = null;
         try {
            final Invocation.Builder request = buildRequest();
            if (state.get() == State.OPEN)
            {
               eventInput = request.get(SseEventInputImpl.class);
            }
         } catch (Throwable e) {
            e.printStackTrace();
         } finally {
            if (connectedLatch != null) {
               connectedLatch.countDown();
            }
         }
         while (state.get() == State.OPEN)
         {
            if (eventInput.isClosed())
            {
               reconnect(reconnectDelay);
               break;
            }
            else
            {
               InboundSseEvent event = eventInput.read();
               if (event != null)
               {
                  onEvent(event);
               }
            }
         }
      }

      public void awaitConnected()
      {
         if (connectedLatch == null)
         {
            return;
         }
         try
         {
            connectedLatch.await(30, TimeUnit.SECONDS);
         }
         catch (InterruptedException ex)
         {
            Thread.currentThread().interrupt();
         }

      }

      private void onEvent(final InboundSseEvent event)
      {
         if (event == null)
         {
            return;
         }
         if (event.getId() != null)
         {
            lastEventId = event.getId();
         }
         if (event.isReconnectDelaySet())
         {
            reconnectDelay = event.getReconnectDelay();
         }
         eventCallback.receive(event);
      }

      private Invocation.Builder buildRequest()
      {
         final Invocation.Builder request = target.request(MediaType.SERVER_SENT_EVENTS_TYPE);
         if (lastEventId != null && !lastEventId.isEmpty())
         {
            request.header(SseConstants.LAST_EVENT_ID_HEADER, lastEventId);
         }
         if (disableKeepAlive)
         {
            request.header(HttpHeaders.CONNECTION, "close");
         }
         return request;
      }

      private void reconnect(final long delay)
      {
         if (state.get() != State.OPEN)
         {
            return;
         }

         EventHandler processor = new EventHandler(this);
         if (delay > 0)
         {
            executor.schedule(processor, delay, TimeUnit.MILLISECONDS);
         }
         else
         {
            executor.submit(processor);
         }
      }
   }
   @Override
   public void subscribe(Subscriber<? super InboundSseEvent> subscriber)
   {
      flowable.subscribe(new RxStreamSubscriberAdapter(subscriber));
      
   }
   
   
    class RxStreamSubscriberAdapter implements org.reactivestreams.Subscriber<InboundSseEvent> {

      private Subscriber<? super InboundSseEvent> subscriber; 
      public RxStreamSubscriberAdapter(Subscriber<? super InboundSseEvent> subscriber)  {
         this.subscriber = subscriber;  
      }
      @Override
      public void onSubscribe(org.reactivestreams.Subscription s)
      {
         subscriber.onSubscribe(new  javax.ws.rs.Flow.Subscription () {          
            @Override
            public void request(long n)
            {
               s.request(n);              
            }            
            @Override
            public void cancel()
            {
               s.cancel();
               
            }
         });       
      }

      @Override
      public void onNext(InboundSseEvent t)
      {
         subscriber.onNext(t);
         
      }

      @Override
      public void onError(Throwable t)
      {
         subscriber.onError(t);
         
      }

      @Override
      public void onComplete()
      {
         subscriber.onComplete();
         
      }
       
    }
}
