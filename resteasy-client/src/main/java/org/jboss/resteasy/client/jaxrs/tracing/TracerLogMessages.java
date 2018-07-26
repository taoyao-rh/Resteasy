package org.jboss.resteasy.client.jaxrs.tracing;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
/**
 * Client tracer logger. This logger can be configured with an async-handler 
 * to get better throughput and fewer latency
 * 
 * @author <a href="mailto:ema@redhat.com">Jim Ma</a>
 *
 */
@MessageLogger(projectCode = "RESTEASY")
public interface TracerLogMessages extends BasicLogger
{
   TracerLogMessages LOGGER = Logger.getMessageLogger(TracerLogMessages.class, TracerLogMessages.class.getPackage().getName());
   int TRACINGBASE = 200;
   @Message(id = TRACINGBASE + 0, value = "Operation %s : Duration: %s")
   String logSpan(String operation, long duration);
}
