package org.jboss.resteasy.spi.tracing;

import java.net.URL;

import javax.ws.rs.core.MediaType;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.Message.Format;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.logging.annotations.MessageLogger;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 13, 2015
 */
@MessageLogger(projectCode = "RESTEASY")
public interface TracerLogMessages extends BasicLogger
{
   TracerLogMessages LOGGER = Logger.getMessageLogger(TracerLogMessages.class, TracerLogMessages.class.getPackage().getName());
   int TRACINGBASE = 7000;
   @Message(id = TRACINGBASE + 0, value = "ResteasyViolationException has invalid format: %s %s")
   String logSpan(String operation, long duration);
}
