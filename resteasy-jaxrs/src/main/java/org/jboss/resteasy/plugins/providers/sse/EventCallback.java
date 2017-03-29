package org.jboss.resteasy.plugins.providers.sse;

import javax.ws.rs.sse.OutboundSseEvent;

public interface EventCallback
{
   public void send(OutboundSseEvent event);
   public void error(Throwable e);

}
