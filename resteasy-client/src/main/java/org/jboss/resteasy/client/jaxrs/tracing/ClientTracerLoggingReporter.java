package org.jboss.resteasy.client.jaxrs.tracing;

import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.jboss.resteasy.spi.tracing.ResteasyLoggerTracerReporter;
import org.jboss.resteasy.spi.tracing.ResteasyTracePoint;
import org.jboss.resteasy.spi.tracing.TracerLogMessages;

public class ClientTracerLoggingReporter extends ResteasyLoggerTracerReporter
{

   private ClientResponse response;

   private final TracerLogMessages logger = TracerLogMessages.LOGGER;

   public ClientTracerLoggingReporter(ClientResponse response)
   {
      this.response = response;
   }

   @Override
   public void report(ResteasyTracePoint point)
   {
      int counter = 0;
      long parentDuration = point.getDuration();
      
      logger.info("RESTEASY-TRACE-" + Integer.toHexString(counter++) + ":" + getFormatString("START", null, null));

      for (ResteasyTracePoint span : point.getChildren())
      {
         long spanDuration = span.getDuration();
         float ratio = 0;
         if (parentDuration != 0)
         {
            ratio = spanDuration * 100 / parentDuration;
         }
         StringBuffer prefix = new StringBuffer();
         prefix.append("RESTEASY-TRACE-").append(Integer.toHexString(counter++));
         logger.infof("%s:%s", prefix.toString(), getResultString(String.valueOf(ratio), span));
         //TODO: find how to check it's a server invoker span
         if (span.getName().equals("INVOKE_SERVER"))
         {
            for (String key : response.getHeaders().keySet())
            {
               if (key.startsWith("X-RESTEASY-TRACE"))
               {
                  logger.infof("--------INVOKE_SERVER:%s",  response.getStringHeaders().get(key));
               }
            }
         }
      }
      logger.info("RESTEASY-TRACE" + Integer.toHexString(counter++) + ":"+  getFormatString("FINISED", null, null));

   }

}
