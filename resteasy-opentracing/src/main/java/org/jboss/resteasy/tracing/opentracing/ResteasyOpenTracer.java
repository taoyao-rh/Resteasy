package org.jboss.resteasy.tracing.opentracing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.Tracer.SpanBuilder;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapExtractAdapter;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.tracing.ResteasyTracePoint;
import org.jboss.resteasy.spi.tracing.ResteasyTracer;
import org.jboss.resteasy.spi.tracing.ResteasyTracerReporter;

public class ResteasyOpenTracer implements ResteasyTracer
{

   private Tracer tracer;
   private final  List<ResteasyTracerReporter> reporters = new ArrayList<ResteasyTracerReporter>(4);
   
   public ResteasyOpenTracer(Tracer tracer)
   {
      this.tracer = tracer;
   }

   @Override
   public ResteasyTracePoint createPoint(ResteasyTracePoint parent, String operationName)
   {
      SpanBuilder spanBuilder = tracer.buildSpan(operationName);
      if (parent == null) {
         return  new ResteayOpenSpan(spanBuilder);
      }
      return new ResteayOpenSpan((ResteayOpenSpan) parent, spanBuilder);
   }

   @Override
   public ResteasyTracePoint createPoint(HttpRequest request, HttpResponse httpResponse, String operationName)
   {
      MultivaluedMap<String, String> headers = request.getMutableHeaders();
      Map<String, String> headersMap = new HashMap<String, String>(8);
      for (Map.Entry<String, List<String>> entry : headers.entrySet())
      {
         if (entry.getValue().size() == 1)
         {
            headersMap.put(entry.getKey(), entry.getValue().get(0));
         }
      }
      SpanContext spanContext = tracer.extract(Format.Builtin.HTTP_HEADERS, new TextMapExtractAdapter(headersMap));
      SpanBuilder spanBuilder = null;
      if (spanContext != null)
      {
         spanBuilder = tracer.buildSpan(operationName).asChildOf(spanContext);
      }
      else
      {
         spanBuilder = tracer.buildSpan(operationName);
      }

      return new ResteayOpenSpan(spanBuilder);
   }

   @Override
   public String getContextKey()
   {
      
      return "uber-trace-id";
   }
   
   @Override
   public void report(ResteasyTracePoint point)
   {
      reporters.forEach(reporter -> {
         reporter.report(point);
      });
      
   }

   @Override
   public void addReporter(ResteasyTracerReporter reporter)
   {
      reporters.add(reporter);
      
   }

}
