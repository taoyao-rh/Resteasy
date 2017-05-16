package org.jboss.resteasy.client.jaxrs.internal;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;

import javax.ws.rs.client.CompletionStageRxInvoker;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.SyncInvoker;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

/**
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @author <a href="mailto:ema@redhat.com">Jim Ma</a>
 * @date March 9, 2016
 */
public class CompletionStageRxInvokerImpl implements CompletionStageRxInvoker
{
   private SyncInvoker invoker;

   private ExecutorService executor;

   public CompletionStageRxInvokerImpl()
   {
   }

   public CompletionStageRxInvokerImpl(SyncInvoker invoker)
   {
      this.invoker = invoker;
   }

   public CompletionStageRxInvokerImpl(SyncInvoker invoker, ExecutorService executor)
   {
      this.invoker = invoker;
      this.executor = executor;
   }

   @Override
   public CompletionStage<Response> get()
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> invoker.get());
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> invoker.get(), executor);
      }

   }

   @Override
   public <T> CompletionStage<T> get(Class<T> responseType)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> invoker.get(responseType));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> invoker.get(responseType), executor);
      }
   }

   @Override
   public <T> CompletionStage<T> get(GenericType<T> responseType)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> invoker.get(responseType));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> invoker.get(responseType), executor);
      }
   }

   @Override
   public CompletionStage<Response> put(Entity<?> entity)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> invoker.put(entity));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> invoker.put(entity), executor);
      }
   }

   @Override
   public <T> CompletionStage<T> put(Entity<?> entity, Class<T> clazz)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> invoker.put(entity, clazz));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> invoker.put(entity, clazz), executor);
      }
   }

   @Override
   public <T> CompletionStage<T> put(Entity<?> entity, GenericType<T> type)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> invoker.put(entity, type));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> invoker.put(entity, type), executor);
      }
   }

   @Override
   public CompletionStage<Response> post(Entity<?> entity)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> invoker.post(entity));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> invoker.post(entity), executor);
      }
   }

   @Override
   public <T> CompletionStage<T> post(Entity<?> entity, Class<T> clazz)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> invoker.post(entity, clazz));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> invoker.post(entity, clazz), executor);
      }
   }

   @Override
   public <T> CompletionStage<T> post(Entity<?> entity, GenericType<T> type)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> invoker.post(entity, type));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> invoker.post(entity, type), executor);
      }
   }

   @Override
   public CompletionStage<Response> delete()
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> invoker.delete());
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> invoker.delete(), executor);
      }
   }

   @Override
   public <T> CompletionStage<T> delete(Class<T> responseType)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> invoker.delete(responseType));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> invoker.delete(responseType), executor);
      }
   }

   @Override
   public <T> CompletionStage<T> delete(GenericType<T> responseType)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> invoker.delete(responseType));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> invoker.delete(responseType), executor);
      }
   }

   @Override
   public CompletionStage<Response> head()
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> invoker.head());
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> invoker.head(), executor);
      }
   }

   @Override
   public CompletionStage<Response> options()
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> invoker.options());
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> invoker.options(), executor);
      }
   }

   @Override
   public <T> CompletionStage<T> options(Class<T> responseType)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> invoker.options(responseType));
      }
      {
         return CompletableFuture.supplyAsync(() -> invoker.options(responseType), executor);
      }
   }

   @Override
   public <T> CompletionStage<T> options(GenericType<T> responseType)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> invoker.options(responseType));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> invoker.options(responseType), executor);
      }
   }

   @Override
   public CompletionStage<Response> trace()
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> invoker.trace());
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> invoker.trace(), executor);
      }
   }

   @Override
   public <T> CompletionStage<T> trace(Class<T> responseType)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> invoker.trace(responseType));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> invoker.trace(responseType), executor);
      }
   }

   @Override
   public <T> CompletionStage<T> trace(GenericType<T> responseType)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> invoker.trace(responseType));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> invoker.trace(responseType), executor);
      }
   }

   @Override
   public CompletionStage<Response> method(String name)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> invoker.method(name));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> invoker.method(name), executor);
      }
   }

   @Override
   public <T> CompletionStage<T> method(String name, Class<T> responseType)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> invoker.method(name, responseType));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> invoker.method(name, responseType), executor);
      }
   }

   @Override
   public <T> CompletionStage<T> method(String name, GenericType<T> responseType)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> invoker.method(name, responseType));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> invoker.method(name, responseType), executor);
      }
   }

   @Override
   public CompletionStage<Response> method(String name, Entity<?> entity)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> invoker.method(name, entity));
      }
      {
         return CompletableFuture.supplyAsync(() -> invoker.method(name, entity), executor);
      }
   }

   @Override
   public <T> CompletionStage<T> method(String name, Entity<?> entity, Class<T> responseType)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> invoker.method(name, entity, responseType));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> invoker.method(name, entity, responseType), executor);
      }
   }

   @Override
   public <T> CompletionStage<T> method(String name, Entity<?> entity, GenericType<T> responseType)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> invoker.method(name, entity, responseType));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> invoker.method(name, entity, responseType), executor);
      }
   }

   public SyncInvoker getinvoker()
   {
      return invoker;
   }

   public CompletionStageRxInvoker invoker(SyncInvoker invoker)
   {
      this.invoker = invoker;
      return this;
   }

   public ExecutorService getExecutor()
   {
      return executor;
   }

   public CompletionStageRxInvoker executor(ExecutorService executor)
   {
      this.executor = executor;
      return this;
   }

   @Override
   public CompletionStage<Response> patch(Entity<?> entity)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> invoker.patch(entity));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> invoker.patch(entity), executor);
      }
   }

   @Override
   public <T> CompletionStage<T> patch(Entity<?> entity, Class<T> responseType)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> invoker.patch(entity, responseType));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> invoker.patch(entity, responseType), executor);
      }
   }

   @Override
   public <T> CompletionStage<T> patch(Entity<?> entity, GenericType<T> responseType)
   {
      if (executor == null)
      {
         return CompletableFuture.supplyAsync(() -> invoker.patch(entity, responseType));
      }
      else
      {
         return CompletableFuture.supplyAsync(() -> invoker.patch(entity, responseType), executor);
      }
   }
}
