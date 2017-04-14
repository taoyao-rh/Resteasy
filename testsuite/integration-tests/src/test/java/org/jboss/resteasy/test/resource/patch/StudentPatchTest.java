package org.jboss.resteasy.test.resource.patch;

import javax.json.Json;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@RunAsClient
public class StudentPatchTest
{

   static Client client;

   @BeforeClass
   public static void setup()
   {
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void close()
   {
      client.close();
   }

   @Deployment
   public static Archive<?> deploy()
   {
      WebArchive war = TestUtil.prepareArchive(StudentPatchTest.class.getSimpleName());
      return TestUtil.finishContainerPrepare(war, null, StudentResource.class, Student.class);
   }

   private String generateURL(String path)
   {
      return PortProviderUtil.generateURL(path, StudentPatchTest.class.getSimpleName());
   }

   @Test
   @Ignore
   public void testPatchStudent() throws Exception
   {
      ResteasyClient client = new ResteasyClientBuilder().connectionPoolSize(10).build();
      WebTarget base = client.target(generateURL("/students"));
      //add a student
      Student newStudent = new Student().setId(1L).setName("John").setSchool("SchoolName1");
      Response response = base.request().post(Entity.<Student> entity(newStudent, MediaType.APPLICATION_JSON));
      Student s = response.readEntity(Student.class);
      //udpate with patch

      WebTarget patchTarget = client.target(generateURL("/students/1"));
      javax.json.JsonArray patchRequest = Json.createArrayBuilder()
            .add(Json.createObjectBuilder().add("op", "replace").add("path", "/name").add("value", "Mike").build())
            .build();
      patchTarget.request().patch(Entity.entity(patchRequest, MediaType.APPLICATION_JSON), Student.class);

      //verify the patch update result;
      Student student = patchTarget.request().get().readEntity(Student.class);
      //Change expceted value to Mike after patch is implemented
      Assert.assertEquals("Patch student doesn't work, expected student name is udpated to Mike", "Mike",
            student.getName());

   }
}
