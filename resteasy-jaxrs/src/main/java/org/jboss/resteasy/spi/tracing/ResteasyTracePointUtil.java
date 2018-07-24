package org.jboss.resteasy.spi.tracing;

import static org.jboss.resteasy.spi.ResteasyProviderFactory.getContextData;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

public class ResteasyTracePointUtil
{

   public static ResteasyTracePoint createPoint(ResteasyTracePoint parent, String operationName)
   {
      ResteasyTracer tracer = getContextData(ResteasyTracer.class);
      ResteasyTracePoint point = tracer.createPoint(parent, operationName);
      return point;
   }
   
   public static ResteasyTracePoint createChildPoint(String operationName)
   {
      ResteasyTracer tracer = getContextData(ResteasyTracer.class);
      if (tracer == null) {
          return new ResteasyNoopTracePointImpl(operationName);
      }
      ResteasyTracePoint parentPoint = getContextData(ResteasyTracePoint.class);
      ResteasyTracePoint point = tracer.createPoint(parentPoint, operationName);
      return point;
   }
   
   public static ResteasyTracePoint createPoint(String name)
   {
      ResteasyTracer tracer = getContextData(ResteasyTracer.class);
      if (tracer == null) {
         return new ResteasyNoopTracePointImpl(name);
      }
      HttpRequest request = getContextData(HttpRequest.class);
      HttpResponse response = getContextData(HttpResponse.class);
      ResteasyTracePoint point  = null;
      if (request != null && response != null) {
         point = tracer.createPoint(request, response, name);
      } else {
         point = tracer.createPoint(null,  name);
      }
      ResteasyProviderFactory.getContextDataMap().put(ResteasyTracePoint.class, point);
      return point;
   }
   public static void report(ResteasyTracePoint span)
   {
      ResteasyTracer tracer = getContextData(ResteasyTracer.class);
      tracer.report(span);
   }
}
