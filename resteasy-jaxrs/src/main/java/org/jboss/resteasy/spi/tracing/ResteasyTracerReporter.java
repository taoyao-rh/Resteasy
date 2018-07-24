package org.jboss.resteasy.spi.tracing;
/*
* Interface to report a trace point and its children trace point.
* Implement this interface to report trace point to different place.
* Like report to logger with LoggerTracerReporter; Report to 
* http response with HttpResponseTracerReporter 
* 
* @author  <a href="mailto:ema@redhat.com>Jim Ma</a>
* @see     ResteasyTracePoint
* @since   4.0
* */
public interface ResteasyTracerReporter
{

  /**
  *  Report trace point result and its childern trace point
  *  
  *  @param point trace point to report
  */
  void report(ResteasyTracePoint point);
}
