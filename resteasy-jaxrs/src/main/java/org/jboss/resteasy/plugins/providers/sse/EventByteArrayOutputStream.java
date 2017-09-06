package org.jboss.resteasy.plugins.providers.sse;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class EventByteArrayOutputStream extends ByteArrayOutputStream
{

   public synchronized byte[] getEventPayLoad()
   {
      if (this.buf[count - 1] == this.buf[count])
      {
         count = count - 1;
      }
      else
      {
         count = count - 2;
      }
      return Arrays.copyOf(buf, count);
   }
}
