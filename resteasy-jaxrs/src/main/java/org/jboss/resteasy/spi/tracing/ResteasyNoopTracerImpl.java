package org.jboss.resteasy.spi.tracing;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;

public class ResteasyNoopTracerImpl implements ResteasyTracer
{
   private final static String CONTEXT_KEY = "x-resteasy-trace-context";
   public ResteasyNoopTracerImpl()
   {
   }
   @Override
   public ResteasyTracePoint createPoint(ResteasyTracePoint parent, String operationName)
   {
      return new ResteasyNoopTracePointImpl(parent, operationName);
   }

   @Override
   public void report(ResteasyTracePoint span)
   {
      //do nothing
      
   }
   @Override
   public ResteasyTracePoint createPoint(HttpRequest request, HttpResponse response, String operationName)
   {
      return new ResteasyNoopTracePointImpl(operationName);
   }
   @Override
   public String getContextKey()
   {
      return CONTEXT_KEY;
   }
   @Override
   public void addReporter(ResteasyTracerReporter reporter)
   {
       //do nothing
      
   }
}
