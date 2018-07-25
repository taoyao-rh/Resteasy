package org.jboss.resteasy.spi.tracing;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
/*
* Factory object to create a ResteasyTracer with the configuraiton from 
* http request or response. 
* 
* The ResteasyTracer can be created with a no operation tracer if request 
* or deployment configuration disables tracer. 
* 
* @author  <a href="mailto:ema@redhat.com>Jim Ma</a>
* @since   4.0
* */
public interface TracerFactory
{
   /**
    * Factory method to create ResteasyTracer
    * 
    * @param request resteasy http request
    * @param response resteasy http response
    * @return created tracer object
    */
   ResteasyTracer createTracer(HttpRequest request, HttpResponse response);
   
   /**
    * Create ResteasyTracer
    * 
    * @return created tracer object
    */
   ResteasyTracer createTracer();
  
}
