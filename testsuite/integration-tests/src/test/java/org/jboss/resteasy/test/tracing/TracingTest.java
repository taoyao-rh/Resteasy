package org.jboss.resteasy.test.tracing;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.test.tracing.resource.TracingResource;
import org.jboss.resteasy.tracing.opentracing.ResteasyOpenTracingFactory;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(Arquillian.class)
@RunAsClient
public class TracingTest {
    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(TracingTest.class.getSimpleName());
        war.addClass(org.jboss.resteasy.tracing.opentracing.ResteasyOpenTracingFactory.class);
        war.setManifest(new StringAsset("Manifest-Version: 1.0\n"
                + "Dependencies: io.opentracing, org.jboss.resteasy.resteasy-opentracing\n"));
        Map<String, String> contextParams = new HashMap<>();
        contextParams.put("resteasy.tracer.factory", "org.jboss.resteasy.tracing.opentracing.ResteasyOpenTracingFactory");
        contextParams.put(ResteasyContextParameters.RESTEASY_TRACING, "true");
        //return TestUtil.finishContainerPrepare(war, contextParams, TracingResource.class, ResteasyOpenTracingFactory.class);
        return TestUtil.finishContainerPrepare(war, contextParams, TracingResource.class);
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
