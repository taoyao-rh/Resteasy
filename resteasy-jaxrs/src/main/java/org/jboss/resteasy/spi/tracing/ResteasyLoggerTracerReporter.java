package org.jboss.resteasy.spi.tracing;

import java.util.Arrays;

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
      logger.info("RESTEASY-TRACE-" + Integer.toHexString(counter++) + getResultString("START", null, null));
      for (ResteasyTracePoint span : parent.getChildren())
      {
         System.out.println(span);
         long spanDuration = span.getDuration();
         float ratio = 0;
         if (parentDuration != 0)
         {
            ratio = spanDuration * 100 / parentDuration;
         }
         logger.info("RESTEASY-TRACE-" + Integer.toHexString(counter++)
               + getResultString(String.valueOf(ratio), parent));
      }
      logger.info("RESTEASY-TRACE" + Integer.toHexString(counter++) + getResultString("FINISED", null, null));
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
      result.append(String.format("[ %6s ms | %6s %%]:%s",  duration, ratio, Arrays.asList(point)));
      return result.toString();
   }
   

}
