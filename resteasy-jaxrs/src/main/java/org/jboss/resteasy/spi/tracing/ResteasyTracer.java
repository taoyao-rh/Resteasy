package org.jboss.resteasy.spi.tracing;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
/*
* ResteasyTracer is the entry point to create trace point. It has the reporters
* list to report the ResteasyTracePoint result to different place like console, 
* http response , database or memory. 

* @author  <a href="mailto:ema@redhat.com>Jim Ma</a>
* @see     ResteasyTracePoint
* @see     ResteasyTracerReporter
* @since   4.0
* */

public interface ResteasyTracer
{

   /**
    * Create Trace point with parent
    * 
    * @param parent parent trace point this object will be added to
    * @param operationName the meaningful name of this trace point
    * @return this object
    */
   ResteasyTracePoint createPoint(ResteasyTracePoint parent, String operationName);
   /**
    * Create trace point with the context information from http request and response
    * 
    * @param request http request
    * @param response http response
    * @param operationName the meaninful name for this trace point
    * @return this object
    */
   ResteasyTracePoint createPoint(HttpRequest request, HttpResponse response, String operationName);
   /**
    * Invoke all reporters to report the trace point  
    * 
    * @param point trace point to report
    */
   
   /**
    * Get context key for propagation cross process
    * 
    * @return conext key
    */
   String getContextKey();
  
   /**
    * Default method to call all reporters
    * 
    * @param point trace point
    */
   void report(ResteasyTracePoint point);

   /**
    * Add reporters 
    * 
    * @param reporter reporter to add to list
    */
   void addReporter(ResteasyTracerReporter reporter);
   
}
