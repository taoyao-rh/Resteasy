package org.jboss.resteasy.spi.tracing;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;

public class ResponseHeaderTracerReporter extends ResteasyLoggerTracerReporter
{
   private HttpRequest request;

   private HttpResponse response;

   public ResponseHeaderTracerReporter(HttpRequest request, HttpResponse response)
   {
      this.request = request;
      this.response = response;
   }

   @Override
   public void report(ResteasyTracePoint parent)
   {
      int counter = 0;
     
      long parentDuration = parent.getDuration();
      System.out.println("Parent:" + parent);
      this.response.getOutputHeaders().putSingle("X-RESTEASY-TRACE-" + Integer.toHexString(counter++),
            getResultString("START", null, null));
      for (ResteasyTracePoint span : parent.getChildren())
      {
         System.out.println(span);
         long spanDuration = span.getDuration();
         float ratio = 0;
         if (parentDuration != 0)
         {
            ratio = spanDuration * 100 / parentDuration;
         }
         this.response.getOutputHeaders().putSingle("X-RESTEASY-TRACE-" + Integer.toHexString(counter++),
               getResultString(span.getName(), String.valueOf(spanDuration), String.valueOf(ratio)));
      }
      this.response.getOutputHeaders().putSingle("X-RESTEASY-TRACE-" + Integer.toHexString(counter++),
            getResultString("FINISED", null, null));
   }
}
