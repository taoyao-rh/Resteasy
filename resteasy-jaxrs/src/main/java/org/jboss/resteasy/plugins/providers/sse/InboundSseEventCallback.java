package org.jboss.resteasy.plugins.providers.sse;

import javax.ws.rs.sse.InboundSseEvent;

public interface InboundSseEventCallback
{
   public void receive(InboundSseEvent event);
   public void error(Throwable e);
   public void close();

}
