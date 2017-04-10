package org.jboss.resteasy.plugins.providers.sse;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.ws.rs.Flow;
import javax.ws.rs.Flow.Subscriber;
import javax.ws.rs.Flow.Subscription;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.SseBroadcaster;


public class SseBroadcasterImpl implements SseBroadcaster
{
   private final int THREADS = 5;

   private final Map<Subscriber<? super OutboundSseEvent>, Subscription> subscribers = new ConcurrentHashMap<>();

   private final Set<Consumer<Subscriber<? super OutboundSseEvent>>> onCloseConsumers = new CopyOnWriteArraySet<>();

   private final Set<BiConsumer<Subscriber<? super OutboundSseEvent>, Throwable>> onErrorConsumers = new CopyOnWriteArraySet<>();

   private BlockingQueue<OutboundSseEvent> eventQueue = new ArrayBlockingQueue<OutboundSseEvent>(1024);

   private AtomicInteger threadCounter = new AtomicInteger();
   
   private OutboundSseEventCallback eventHandler;
   
   private Flowable<OutboundSseEvent> flowable;

   //TODO : Look at use a concainter provided thread pool ?
   private ExecutorService service = Executors.newCachedThreadPool(runnable -> {
      Thread thread = new Thread(runnable);
      thread.setName("SseEvent Emmiter thread  " + threadCounter.getAndIncrement());
      return thread;
   });

   public SseBroadcasterImpl () {
         flowable = Flowable.<OutboundSseEvent>create(emitter ->  {
         OutboundSseEventCallback callback = new OutboundSseEventCallback() {
            @Override
            public void send(OutboundSseEvent event) {
              emitter.onNext(event);
              //TODO: give a flag to trigger onCompleted ?
              //emitter.onCompleted();
            }

            @Override
            public void error(Throwable e) {
              emitter.onError(e);
            }
          };
          this.eventHandler = callback;
        }, BackpressureStrategy.BUFFER).share();
     
   }

   @Override
   public void broadcast(OutboundSseEvent event)
   {
      if (eventQueue.size() == 0) {
         for (int i = 0 ; i < THREADS; i++) {
            service.submit(() -> {
               try
               {
                  this.eventHandler.send(eventQueue.take());
               }
               catch (Exception e)
               {
                  this.eventHandler.error(e);
               }
            });
         }
      }
      eventQueue.add(event);
   }

   @Override
   public void close()
   {
      for (final Subscriber<? super OutboundSseEvent> output : subscribers.keySet())
      {
         try
         {
            output.onComplete();
            for (Consumer<Subscriber<? super OutboundSseEvent>> consumer : onCloseConsumers)
            {
               consumer.accept(output);
            }
         }
         catch (final Exception ex)
         {
            output.onError(ex); //TODO is this required?
            onCloseConsumers.forEach(occ -> occ.accept(output));
         }
      }
   }

   @Override
   public void onError(BiConsumer<Flow.Subscriber<? super OutboundSseEvent>, Throwable> onError)
   {
      onErrorConsumers.add(onError);
   }



   @Override
   public void subscribe(Flow.Subscriber<? super OutboundSseEvent> output)
   {
      flowable.subscribe(new RxStreamSubscriberAdaptor(output));
   }

   @Override
   public void onClose(Consumer<javax.ws.rs.Flow.Subscriber<? super OutboundSseEvent>> onClose)
   {
      onCloseConsumers.add(onClose);
      
   }
   
   public class RxStreamSubscriberAdaptor implements org.reactivestreams.Subscriber<OutboundSseEvent> {
      private javax.ws.rs.Flow.Subscriber<? super OutboundSseEvent> subscriber;
      public  RxStreamSubscriberAdaptor(javax.ws.rs.Flow.Subscriber<? super OutboundSseEvent> output) {
         this.subscriber = output;
      }
      @Override
      public void onSubscribe(org.reactivestreams.Subscription s)
      {
         subscriber.onSubscribe(new Subscription (){

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
      public void onNext(OutboundSseEvent t)
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


