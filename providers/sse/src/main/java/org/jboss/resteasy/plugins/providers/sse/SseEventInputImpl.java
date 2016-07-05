package org.jboss.resteasy.plugins.providers.sse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.nio.charset.Charset;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.sse.InboundSseEvent;
import javax.ws.rs.sse.SseEventInput;

import org.jboss.resteasy.plugins.providers.sse.i18n.LogMessages;
import org.jboss.resteasy.plugins.providers.sse.i18n.Messages;

public class SseEventInputImpl implements SseEventInput
{

   private Annotation[] annotations;

   private MediaType mediaType;

   private MultivaluedMap<String, String> httpHeaders;

   private InputStream inputStream;
   
   private final byte[] EventEND = "\r\n\r\n".getBytes();
   
   private boolean isClosed = false;

   public SseEventInputImpl(Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
         InputStream inputStream)
   {
      this.annotations = annotations;
      this.mediaType = mediaType;
      this.httpHeaders = httpHeaders;
      this.inputStream = inputStream;
   }

   @Override
   public void close() throws IOException
   {
      this.inputStream.close();
      isClosed = true;

   }

   @Override
   public boolean isClosed()
   {
      return isClosed;
   }

   @Override
   public InboundSseEvent read() throws IllegalStateException
   {
      byte[] chunk = null;
      try
      {
         chunk = readEvent(inputStream);
      }
      catch (IOException e1)
      {
         throw new RuntimeException(Messages.MESSAGES.readEventException(), e1);
      }
      if (chunk == null)
      {
         return null;
      }
      

      final ByteArrayInputStream entityStream = new ByteArrayInputStream(chunk);
      final ByteArrayOutputStream tokenData = new ByteArrayOutputStream();
      Charset charset = SseConstants.UTF8;
      if (mediaType != null && mediaType.getParameters().get(MediaType.CHARSET_PARAMETER) != null)
      {
         charset = Charset.forName(mediaType.getParameters().get(MediaType.CHARSET_PARAMETER));
      }
      final InboundSseEventImpl.Builder eventBuilder = new InboundSseEventImpl.Builder(annotations, mediaType,
            httpHeaders);
      int b = -1;
      SseConstants.State currentState = SseConstants.State.NEW_LINE;
      try
      {
         loop : do
         {
            switch (currentState)
            {
               case NEW_LINE :
                  if (b == '\r')
                  {
                     b = entityStream.read();

                     b = b == '\n' ? entityStream.read() : b;
                  }
                  else
                  {
                     b = entityStream.read();
                  }

                  if (b == '\n' || b == '\r' || b == -1)
                  {
                     break loop;
                  }

                  if (b == ':')
                  {
                     currentState = SseConstants.State.COMMENT;
                  }
                  else
                  {
                     tokenData.write(b);
                     currentState = SseConstants.State.FIELD;
                  }
                  break;
               case COMMENT :
                  b = readLine(entityStream, '\n', tokenData);
                  final String commentLine = tokenData.toString(charset.toString());
                  tokenData.reset();
                  eventBuilder.commentLine(commentLine);
                  currentState = SseConstants.State.NEW_LINE;
                  break;
               case FIELD :
                  b = readLine(entityStream, ':', tokenData);
                  final String fieldName = tokenData.toString(charset.toString());
                  tokenData.reset();

                  if (b == ':')
                  {
                     do
                     {
                        b = entityStream.read();
                     }
                     while (b == ' ');

                     if (b != '\n' && b != '\r' && b != -1)
                     {
                        tokenData.write(b);
                        b = readLine(entityStream, '\n', tokenData);
                     }
                  }

                  processField(eventBuilder, fieldName, mediaType, tokenData.toByteArray());
                  tokenData.reset();

                  currentState = SseConstants.State.NEW_LINE;
                  break;
            }
         }

         while (b != -1);
      }
      catch (IOException e)
      {
         throw new RuntimeException(Messages.MESSAGES.readEventException(), e);
      }
      return eventBuilder.build();

   }

   private int readLine(final InputStream in, final int delimiter, final OutputStream out) throws IOException
   {
      int b;
      while ((b = in.read()) != -1)
      {
         if (b == delimiter || b == '\n' || b == '\r')
         {
            break;
         }
         else if (out != null)
         {
            out.write(b);
         }
      }

      return b;
   }

   private void processField(final InboundSseEventImpl.Builder inboundEventBuilder, final String name,
         final MediaType mediaType, final byte[] value)
   {
      Charset charset = SseConstants.UTF8;
      if (mediaType != null && mediaType.getParameters().get(MediaType.CHARSET_PARAMETER) != null)
      {
         charset = Charset.forName(mediaType.getParameters().get(MediaType.CHARSET_PARAMETER));
      }
      String valueString = new String(value, charset);
      if (name.startsWith("    "))
      {
         inboundEventBuilder.name(name.substring(4));
      }
      else if ("data".equals(name))
      {
         inboundEventBuilder.write(value);
         inboundEventBuilder.write(SseConstants.EOL);
      }
      else if ("id".equals(name))
      {
         inboundEventBuilder.id(valueString);
      }
      else if ("retry".equals(name))
      {
         try
         {
            inboundEventBuilder.reconnectDelay(Long.parseLong(valueString));
         }
         catch (final NumberFormatException ex)
         {
            LogMessages.LOGGER.skipIllegalField("retry", valueString);
         }
      }
      else
      {
         LogMessages.LOGGER.skipUnkownFiled(name);
      }
   }
   public byte[] readEvent(final InputStream in) throws IOException {
      final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      int data;
      int pos = 0;
      while ((data = in.read()) != -1) {
           byte b = (byte)data;
           if (b == EventEND[pos]) {
               pos++;
           } else {
              pos = 0;
           }
           buffer.write(b);
           if (pos >= EventEND.length && buffer.toByteArray().length > EventEND.length) {
              return buffer.toByteArray();
           }
           if (pos >= EventEND.length && buffer.toByteArray().length == EventEND.length) {
              pos = 0;
              buffer.reset();
              continue;
           }
      }
      return null;
  } 
}
