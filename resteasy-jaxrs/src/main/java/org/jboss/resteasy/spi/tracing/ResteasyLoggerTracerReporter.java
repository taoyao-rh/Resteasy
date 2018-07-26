package org.jboss.resteasy.spi.tracing;

import java.util.Arrays;
import java.util.Map;

public class ResteasyLoggerTracerReporter implements ResteasyTracerReporter
{

   private TracerLogMessages logger;

   public ResteasyLoggerTracerReporter()
   {
      logger = TracerLogMessages.LOGGER;
   }

   @Override
   public void report(ResteasyTracePoint parent)
   {
      int counter = 0;

      long parentDuration = parent.getDuration();
      logger.info("RESTEASY-TRACE-" + Integer.toHexString(counter++) + getFormatString("START", null, null));
      for (ResteasyTracePoint span : parent.getChildren())
      {
         long spanDuration = span.getDuration();
         float ratio = 0;
         if (parentDuration != 0)
         {
            ratio = spanDuration * 100 / parentDuration;
         }
         logger.info("RESTEASY-TRACE-" + Integer.toHexString(counter++)
               + getResultString(String.valueOf(ratio), span));
      }
      logger.info("RESTEASY-TRACE" + Integer.toHexString(counter++) + getFormatString("FINISHED", null, null));
   }

   protected String getResultString(String ratio, ResteasyTracePoint point)
   {

      return getResultString( String.valueOf(point.getDuration()), ratio, point);
   }
   
   protected String getResultString(String duration, String ratio,  ResteasyTracePoint point)
   {

      if (duration == null)
         duration = "------";
      if (ratio == null)
         ratio = "------";
      StringBuffer result = new StringBuffer(String.format("%-30s", point.getName()).replace(" ", "-"));
      result.append(String.format("[ %6s ms | %6s %%]",  duration, ratio));
      if (!point.getDetails().isEmpty()) {
         result.append("-(");
      }
      for (Map.Entry<String, String> entry : point.getDetails().entrySet()) {
         result.append(entry.getKey()).append("=").append(entry.getValue()).append(";");
      }
      if (!point.getDetails().isEmpty()) {
         result.append(")");
      }
      return result.toString();
   }
   
   protected String getFormatString(String name, String duration, String ratio)
   {

      if (duration == null)
         duration = "------";
      if (ratio == null)
         ratio = "------";
      StringBuffer result = new StringBuffer(String.format("%-30s", name).replace(" ", "-"));
      result.append(String.format("[ %6s ms | %6s %%]",  duration, ratio));
      return result.toString();
   }
   
   

}
