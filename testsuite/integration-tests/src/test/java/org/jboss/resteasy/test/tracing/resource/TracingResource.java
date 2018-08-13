package org.jboss.resteasy.test.tracing.resource;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

@Path("/")
public class TracingResource
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
    public String context(@Context Context context)
    {
        return context.toString();
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