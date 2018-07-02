package org.jboss.resteasy.plugins.server.netty.cdi2;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.se.SeContainer;

import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.plugins.server.netty.RequestDispatcher;
@Dependent
public class SeCdiNettyJaxrsServer extends NettyJaxrsServer
{

   private SeContainer secontainer;
   public SeCdiNettyJaxrsServer(SeContainer container) {
      this.secontainer = container;
   }
   
   @Override
   protected RequestDispatcher createRequestDispatcher() {
       return new SeCdiRequestDispatcher((SynchronousDispatcher)super.deployment.getDispatcher(),
                               super.deployment.getProviderFactory(), super.domain, this.secontainer);
   }
   
   
}
