package org.jboss.resteasy.spi.tracing;

import java.io.Closeable;
import java.util.List;
import java.util.Map;
/*
* An object to trace how long an operation takes. It has child elements contain 
* the sub operation time measurement.
* 
*<p> This interface implements Closeable to allow operation can be execute in a
*try-with-resources to counter the time. This interface extends Map interface to 
*put other additional information like http method, intercetprs name. Any additional 
*information should be carried can put in with a map entry. 
*
* @author  <a href="mailto:ema@redhat.com>Jim Ma</a>
* @see     Map
* @see     Closeable
* @since   4.0
* */
public interface ResteasyTracePoint extends Closeable 
{
   
   /**
    * Returns the opeartion name or meaninful name for this trace point
    * 
    * @return meaningful name for this trace pint
    */
   String getName();
   /**
    * Returns duration of this trace point after finish
    * 
    * @return duraton for this trace point
    */
   long getDuration();
   /**
    * Starts the trace point to counter time
    * 
    * @return this object
    */
   ResteasyTracePoint start();
   /**
    * Finish the trace point
    * 
    * @return this object
    */
   ResteasyTracePoint finish();
   /**
    * Get the child list of this trace point 
    * 
    * @return this trace point's child list
    */
   List<ResteasyTracePoint> getChildren();
   
   /**
    * Get context string which can be propagated cross process
    * 
    * @return context string 
    */
   String  getContextString();
   
   
   /**
    * Add more info to TracePoint with key-value pair 
    * 
    * @param key info key
    * @param value info
    */
   void addDetail(String key, String value);
   
   /**
    * Get all added info  info 
    * 
    * @return all added info
    */
   Map<String, String> getDetails();
}
