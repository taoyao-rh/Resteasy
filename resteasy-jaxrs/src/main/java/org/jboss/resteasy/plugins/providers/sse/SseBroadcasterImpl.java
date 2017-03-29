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
import javax.ws.rs.Flow.Sink;
import javax.ws.rs.Flow.Subscription;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.SseBroadcaster;

import org.reactivestreams.Subscriber;

public class SseBroadcasterImpl implements SseBroadcaster
{
   private final int THREADS = 5;

   private final Map<Sink<? super OutboundSseEvent>, Subscription> subscribers = new ConcurrentHashMap<>();

   private final Set<Consumer<Sink<? super OutboundSseEvent>>> onCloseConsumers = new CopyOnWriteArraySet<>();

   private final Set<BiConsumer<Sink<? super OutboundSseEvent>, Throwable>> onErrorConsumers = new CopyOnWriteArraySet<>();

   private BlockingQueue<OutboundSseEvent> eventQueue = new ArrayBlockingQueue<OutboundSseEvent>(1024);

   private AtomicInteger threadCounter = new AtomicInteger();
   
   private EventCallback eventHandler;
   
   private Flowable<OutboundSseEvent> flowable;

   //TODO : Look at use a concainter provided thread pool ?
   private ExecutorService service = Executors.newCachedThreadPool(runnable -> {
      Thread thread = new Thread(runnable);
      thread.setName("SseEvent Emmiter thread  " + threadCounter.getAndIncrement());
      return thread;
   });

   public SseBroadcasterImpl () {
         flowable = Flowable.create(emitter ->  {
         EventCallback callback = new EventCallback() {
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
        }, BackpressureStrategy.BUFFER);
     
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
      for (final Sink<? super OutboundSseEvent> output : subscribers.keySet())
      {
         try
         {
            output.onComplete();
            for (Consumer<Sink<? super OutboundSseEvent>> consumer : onCloseConsumers)
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
   public void onError(BiConsumer<Flow.Sink<? super OutboundSseEvent>, Throwable> onError)
   {
      onErrorConsumers.add(onError);
   }

   @Override
   public void onClose(Consumer<Sink<? super OutboundSseEvent>> onClose)
   {
      onCloseConsumers.add(onClose);
   }

   @Override
   public void subscribe(Sink<? super OutboundSseEvent> subscriber)
   {
      flowable.subscribe(new Subscriber<OutboundSseEvent>() {

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
         
      });
   }
}
