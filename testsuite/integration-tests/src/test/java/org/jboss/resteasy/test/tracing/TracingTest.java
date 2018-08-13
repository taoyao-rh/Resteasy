package org.jboss.resteasy.test.tracing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.tracing.resource.TracingResource;
import org.jboss.resteasy.tracing.opentracing.ResteasyOpenTracingFactory;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@RunAsClient
public class TracingTest {
    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(TracingTest.class.getSimpleName());
        war.addClass(org.jboss.resteasy.tracing.opentracing.ResteasyOpenTracingFactory.class);
        war.setManifest(new StringAsset("Manifest-Version: 1.0\n"
                + "Dependencies: org.jboss.resteasy.resteasy-opentracing services\n"));
        Map<String, String> contextParams = new HashMap<>();
        contextParams.put("resteasy.tracer.factory", "org.jboss.resteasy.tracing.opentracing.ResteasyOpenTracingFactory");
        contextParams.put("resteasy.tracing", "true");
        return TestUtil.finishContainerPrepare(war, contextParams, TracingResource.class, ResteasyOpenTracingFactory.class);
    }

    /**
     * @tpTestDetails
     * @tpSince
     */
    @Test
    public void testTracing() throws Exception {
        ClientBuilder builder = ClientBuilder.newBuilder();
        builder.property("resteasy.tracer.factory", "org.jboss.resteasy.tracing.opentracing.ResteasyOpenTracingFactory");
        builder.property("resteasy.tracing", true);
        Client client = builder.build();

        WebTarget base = client.target(PortProviderUtil.generateURL("/test/absolute", TracingTest.class.getSimpleName()));
        for (int i = 0; i < 5; i++) {
            Response response = base.request().header("x-resteasy-trace", "true").get();
            for (Map.Entry<String, List<Object>> entry: response.getHeaders().entrySet())
            {
                System.out.println(entry.getKey() + ":" + entry.getValue().toString());
            }
        }
        Thread.sleep(10000000);
    }
}