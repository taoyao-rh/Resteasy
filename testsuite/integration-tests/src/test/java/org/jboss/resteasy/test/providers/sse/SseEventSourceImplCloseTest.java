package org.jboss.resteasy.test.providers.sse;

import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.sse.resource.SseSmokeResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.sse.InboundSseEvent;
import javax.ws.rs.sse.SseEventSource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(Arquillian.class)
@RunAsClient
public class SseEventSourceImplCloseTest {
    private final static Logger logger = Logger.getLogger(SseEventSourceImplCloseTest.class);
    private static final ScheduledExecutorService scheduledExecutor =
            Executors.newScheduledThreadPool(16);
    private static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(SseEventSourceImplCloseTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, SseSmokeResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, SseEventSourceImplCloseTest.class.getSimpleName());
    }

    @Test
    public void testSseEventSourceOnEventCallback() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final List<InboundSseEvent> results = new ArrayList<InboundSseEvent>();
        Client client = ResteasyClientBuilder.newBuilder().scheduledExecutorService(scheduledExecutor).build();
        Callable<Integer> aCallable = () -> {
            Thread.currentThread().sleep(35 * 1000);
            return 10086;
        };
        Future<Integer> future = scheduledExecutor.submit(aCallable);
        try {
            WebTarget target = client.target(generateURL("/sse/eventssimple"));
            SseEventSource msgEventSource = SseEventSource.target(target).build();
            try (SseEventSource eventSource = msgEventSource) {
                eventSource.register(event -> {
                    results.add(event);
                    latch.countDown();
                });
                eventSource.open();

                boolean waitResult = latch.await(30, TimeUnit.SECONDS);
                Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
            }
            Assert.assertEquals("One message was expected.", 1, results.size());
            Assert.assertThat("The message doesn't have expected content.", "data",
                    CoreMatchers.is(CoreMatchers.equalTo(results.get(0).readData(String.class))));
            msgEventSource.close();
            Integer res = future.get();
            // does not shut down executor
            Assert.assertTrue(res == 10086);
        } finally {
            client.close();
        }
    }

    @Test
    public void testCloseFunction() throws ExecutionException, InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final List<InboundSseEvent> results = new ArrayList<InboundSseEvent>();

        final CountDownLatch latchFollowing = new CountDownLatch(1);
        final List<InboundSseEvent> resultsFollowing = new ArrayList<InboundSseEvent>();
        Client client = ResteasyClientBuilder.newBuilder().scheduledExecutorService(scheduledExecutor).build();

        try {
            WebTarget target = client.target(generateURL("/sse/eventssimple"));
            SseEventSource msgEventSource = SseEventSource.target(target).build();
            try (SseEventSource eventSource = msgEventSource) {
                eventSource.register(event -> {
                    results.add(event);
                    latch.countDown();
                });
                eventSource.open();
            }

            // Using the same scheduledExecutor
            // Closing this EventSource does not affect the previous tasks.
            WebTarget targetFollowing = client.target(generateURL("/sse/eventssimple"));
            SseEventSource msgEventSourceFollowing = SseEventSource.target(target).build();
            try (SseEventSource eventSource = msgEventSourceFollowing) {
                eventSource.register(event -> {
                    resultsFollowing.add(event);
                    latchFollowing.countDown();
                });
                eventSource.open();
                msgEventSourceFollowing.close();
            }

            boolean waitResult = latch.await(120, TimeUnit.SECONDS);
            Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
            Assert.assertEquals("One message was expected.", 1, results.size());
            Assert.assertThat("The message doesn't have expected content.", "data",
                    CoreMatchers.is(CoreMatchers.equalTo(results.get(0).readData(String.class))));
        } finally {
            client.close();
        }
    }


}
