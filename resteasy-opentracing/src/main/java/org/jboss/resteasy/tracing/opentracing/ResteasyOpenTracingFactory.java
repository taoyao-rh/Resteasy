package org.jboss.resteasy.tracing.opentracing;

import io.jaegertracing.internal.JaegerTracer.Builder;
import io.opentracing.util.GlobalTracer;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.tracing.ResponseHeaderTracerReporter;
import org.jboss.resteasy.spi.tracing.ResteasyLoggerTracerReporter;
import org.jboss.resteasy.spi.tracing.ResteasyNoopTracerImpl;
import org.jboss.resteasy.spi.tracing.ResteasyTracer;
import org.jboss.resteasy.spi.tracing.ResteasyTracerReporter;
import org.jboss.resteasy.spi.tracing.TracerFactory;

public class ResteasyOpenTracingFactory implements TracerFactory
{
   public ResteasyOpenTracingFactory()
   {
      if (!GlobalTracer.isRegistered())
      {
         Builder builder = new Builder("Resteasy")
               .withSampler(new io.jaegertracing.internal.samplers.ConstSampler(true))
               .withMetricsFactory(new io.jaegertracing.internal.metrics.NoopMetricsFactory());
         GlobalTracer.register(builder.build());
      }
   }
   private final ResteasyTracerReporter loggerReporter = new ResteasyLoggerTracerReporter();

   private final ResteasyNoopTracerImpl noopTracer = new ResteasyNoopTracerImpl();

   @Override
   public ResteasyTracer createTracer(HttpRequest request, HttpResponse response)
   {
      
      ResteasyOpenTracer openTracer;
      if (ResteasyProviderFactory.getInstance().isTracerEnabled())
      {
         openTracer = new ResteasyOpenTracer(GlobalTracer.get());
         openTracer.addReporter(loggerReporter);
         openTracer.addReporter(new ResponseHeaderTracerReporter(request, response));
         return openTracer;
      }
      if (!request.getHttpHeaders().getRequestHeader("x-resteasy-trace").isEmpty()
            && !ResteasyProviderFactory.getInstance().isTracerEnabled())
      {
          openTracer = new ResteasyOpenTracer(GlobalTracer.get());
          openTracer.addReporter(new ResponseHeaderTracerReporter(request, response));
          return openTracer;
      }

      if (!request.getHttpHeaders().getRequestHeader("x-resteasy-trace-logger").isEmpty()
            && ResteasyProviderFactory.getInstance().isTracerEnabled())
      {
         
         openTracer = new ResteasyOpenTracer(GlobalTracer.get());
         openTracer.addReporter(loggerReporter);
         return openTracer;
      }

      return noopTracer;
   }

   @Override
   public ResteasyTracer createTracer()
   {
      ResteasyOpenTracer openTracer = new ResteasyOpenTracer(GlobalTracer.get());
      return openTracer;
   }
}
