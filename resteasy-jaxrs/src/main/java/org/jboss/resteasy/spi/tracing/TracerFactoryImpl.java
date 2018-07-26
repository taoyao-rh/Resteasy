package org.jboss.resteasy.spi.tracing;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

public class TracerFactoryImpl implements TracerFactory
{
   private final ResteasyLoggerTracerReporter loggerReporter = new ResteasyLoggerTracerReporter();

   private final ResteasyNoopTracerImpl noopTracer = new ResteasyNoopTracerImpl();

   @Override
   public ResteasyTracer createTracer(HttpRequest request, HttpResponse response)
   {
      ResteasyTracerImpl tracer = null;
      if (ResteasyProviderFactory.getInstance().isTracerEnabled())
      {
         if (tracer == null)
         {
            tracer = new ResteasyTracerImpl();
         }
         tracer.addReporter(new ResponseHeaderTracerReporter(request, response));
         tracer.addReporter(loggerReporter);
      }
      if (!request.getHttpHeaders().getRequestHeader("x-resteasy-trace").isEmpty()
            && !ResteasyProviderFactory.getInstance().isTracerEnabled())
      {
         if (tracer == null)
         {
            tracer = new ResteasyTracerImpl();
         }
         tracer.addReporter(new ResponseHeaderTracerReporter(request, response));
      }

      if (!request.getHttpHeaders().getRequestHeader("x-resteasy-trace-logger").isEmpty()
            && ResteasyProviderFactory.getInstance().isTracerEnabled())
      {
         if (tracer == null)
         {
            tracer = new ResteasyTracerImpl();
         }
         tracer.addReporter(loggerReporter);
      }

      if (tracer != null)
      {
         return tracer;
      }
      return noopTracer;
   }

   @Override
   public ResteasyTracer createTracer()
   {
      ResteasyTracerImpl tracer = new ResteasyTracerImpl();
      return tracer;
   }

}
