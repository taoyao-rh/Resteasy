package org.jboss.resteasy.test;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.plugins.server.netty.NettyContainer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class NettyTraceTest
{
   @Path("/")
   public static class Resource
   {
      @GET
      @Path("/test")
      @Produces("text/plain")
      public String hello()
      {
         return "hello world";
      }

      @GET
      @Path("empty")
      public void empty()
      {

      }

      @GET
      @Path("query")
      public String query(@QueryParam("param") String value)
      {
         return value;

      }

      @GET
      @Path("/exception")
      @Produces("text/plain")
      public String exception()
      {
         throw new RuntimeException();
      }

      @GET
      @Path("large")
      @Produces("text/plain")
      public String large()
      {
         StringBuffer buf = new StringBuffer();
         for (int i = 0; i < 1000; i++)
         {
            buf.append(i);
         }
         return buf.toString();
      }

      @GET
      @Path("/context")
      @Produces("text/plain")
      public String context(@Context ChannelHandlerContext context)
      {
         return context.channel().toString();
      }

      @POST
      @Path("/post")
      @Produces("text/plain")
      public String post(String postBody)
      {
         return postBody;
      }

      @PUT
      @Path("/leak")
      public String put(String contents)
      {
         return contents;
      }

      @GET
      @Path("/test/absolute")
      @Produces("text/plain")
      public String absolute(@Context UriInfo info)
      {
         return "uri: " + info.getRequestUri().toString();
      }
   }

   static Client client;

   @BeforeClass
   public static void setup() throws Exception
   {
      ResteasyDeployment deployment = new ResteasyDeployment();
      //deployment.getRegistry().addPerRequestResource(Resource.class);
      deployment.setTracerFactoryClass("org.jboss.resteasy.tracing.opentracing.ResteasyOpenTracingFactory");
      NettyContainer.start("", null, deployment).getRegistry().addPerRequestResource(Resource.class);
      // NettyContainer.start().getDefaultContextObjects()
      ClientBuilder builder = ClientBuilder.newBuilder();
      builder.property("resteasy.tracer.factory", "org.jboss.resteasy.tracing.opentracing.ResteasyOpenTracingFactory");
      builder.property("resteasy.tracing", true);
      client = builder.build();
      
      
   }

   @AfterClass
   public static void end() throws Exception
   {
      try
      {
         client.close();
      }
      catch (Exception e)
      {

      }
      NettyContainer.stop();
   }
   
   @Test
   public void testBasicTracing() throws Exception
   {
      //Thread.sleep(999999999);
      WebTarget target = client.target(generateURL("/test/absolute"));
      for (int i = 0; i < 5; i++) {
      Response response = target.request().header("x-resteasy-trace", "true").get();
      for (Map.Entry<String, List<Object>> entry: response.getHeaders().entrySet())
      {
         System.out.println(entry.getKey() + ":" + entry.getValue().toString());
      }
      }
      Thread.sleep(10000000);
   }
}
