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
      this.response.getOutputHeaders().putSingle("X-RESTEASY-TRACE-" + Integer.toHexString(counter++),
            getFormatString("START", null, null));
      for (ResteasyTracePoint span : parent.getChildren())
      {
         long spanDuration = span.getDuration();
         float ratio = 0;
         if (parentDuration != 0)
         {
            ratio = spanDuration * 100 / parentDuration;
         }
         this.response.getOutputHeaders().putSingle("X-RESTEASY-TRACE-" + Integer.toHexString(counter++),
               getResultString(String.valueOf(ratio), span));
      }
      this.response.getOutputHeaders().putSingle("X-RESTEASY-TRACE-" + Integer.toHexString(counter++),
            getFormatString("FINISED", null, null));
   }
}
