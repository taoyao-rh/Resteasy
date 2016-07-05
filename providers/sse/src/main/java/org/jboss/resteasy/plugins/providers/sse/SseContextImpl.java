package org.jboss.resteasy.plugins.providers.sse;

import javax.ws.rs.sse.OutboundSseEvent.Builder;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseContext;
import javax.ws.rs.sse.SseEventOutput;

public class SseContextImpl implements SseContext
{
   private SseEventProvider writer = new SseEventProvider();
   private SseEventOutputProvider eventWriter = new SseEventOutputProvider();

   @Override
   public SseEventOutput newOutput()
   {
      SseEventOutput output = new SseEventOutputImpl(writer);
      
      return output;
   }

   @Override
   public Builder newEvent()
   {
      return new OutboundSseEventImpl.BuilderImpl();
   }

   @Override
   public SseBroadcaster newBroadcaster()
   {
      return new SseBroadcasterImpl();
   }
}
