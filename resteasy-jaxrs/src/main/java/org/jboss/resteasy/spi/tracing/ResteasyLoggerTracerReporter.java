package org.jboss.resteasy.spi.tracing;

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
               + getResultString(span.getName(), String.valueOf(spanDuration), String.valueOf(ratio)));
      }
      logger.info("RESTEASY-TRACE" + Integer.toHexString(counter++) + getResultString("FINISED", null, null));
   }

   protected String getResultString(String name, String duration, String ratio)
   {

      if (duration == null)
         duration = "------";
      if (ratio == null)
         ratio = "------";
      StringBuffer result = new StringBuffer(String.format("%-30s", name).replace(" ", "-"));
      result.append(String.format("[ %6s ms | %6s %%]", duration, ratio));
      return result.toString();
   }

}
