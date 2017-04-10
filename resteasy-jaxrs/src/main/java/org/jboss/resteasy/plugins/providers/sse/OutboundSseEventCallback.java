package org.jboss.resteasy.plugins.providers.sse;

import javax.ws.rs.sse.OutboundSseEvent;

public interface OutboundSseEventCallback
{
   public void send(OutboundSseEvent event);
   public void error(Throwable e);

}
