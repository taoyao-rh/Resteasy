package org.jboss.resteasy.plugins.server.netty.cdi2;

import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.se.SeContainer;

import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.plugins.server.netty.RequestDispatcher;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.weld.context.bound.BoundRequestContext;

public class SeCdiRequestDispatcher extends RequestDispatcher
{

   private SeContainer container;
   public SeCdiRequestDispatcher(SynchronousDispatcher dispatcher, ResteasyProviderFactory providerFactory,
         SecurityDomain domain, SeContainer container)
   {
      super(dispatcher, providerFactory, domain);
      this.container = container;
   }
   @Override
   public void service(ChannelHandlerContext ctx, HttpRequest request, HttpResponse response, boolean handleNotFound) throws IOException {
       BoundRequestContext context = container.select(BoundRequestContext.class).get();
       Map<String,Object> contextMap = new HashMap<String,Object>();
       context.associate(contextMap);
       context.activate();
       try
       {
           super.service(ctx, request,response,handleNotFound);
       }
       finally
       {
           context.invalidate();
           context.deactivate();
           context.dissociate(contextMap);
       }
   }
}
