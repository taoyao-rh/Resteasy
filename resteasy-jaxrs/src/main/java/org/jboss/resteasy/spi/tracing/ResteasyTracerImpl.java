package org.jboss.resteasy.spi.tracing;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;

public class ResteasyTracerImpl implements ResteasyTracer
{

   private final static String CONTEXT_KEY = "x-resteasy-trace-context";
  
   @Override
   public ResteasyTracePoint createPoint(ResteasyTracePoint parent, String operationName)
   {
      if (parent == null)
      {
         return new ResteasyTracePointImpl(operationName);
      }
      return new ResteasyTracePointImpl(parent, operationName);
   }

   @Override
   public ResteasyTracePoint createPoint(HttpRequest request, HttpResponse response, String operationName)
   {
      //TODO: look at retrieve context from request headers
      return new ResteasyTracePointImpl(operationName);
   }

   @Override
   public String getContextKey()
   {
      return CONTEXT_KEY;
   }

}
