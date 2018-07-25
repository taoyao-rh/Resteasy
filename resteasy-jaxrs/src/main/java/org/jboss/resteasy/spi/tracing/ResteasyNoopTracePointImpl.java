package org.jboss.resteasy.spi.tracing;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ResteasyNoopTracePointImpl implements ResteasyTracePoint
{
   public ResteasyNoopTracePointImpl(ResteasyTracePoint parent, String operationName)
   {
   }

   public ResteasyNoopTracePointImpl(String operationName)
   {

   }

   @Override
   public void close() throws IOException
   {

   }

   @Override
   public String getName()
   {
      return "NoopTracePoint";
   }

   @Override
   public long getDuration()
   {
      return 0;
   }
   @Override
   public ResteasyTracePoint start()
   {
      return this;
   }

   @Override
   public ResteasyTracePoint finish()
   {
      return this;
   }

   @SuppressWarnings("unchecked")
   @Override
   public List<ResteasyTracePoint> getChildren()
   {
      return Collections.EMPTY_LIST;
   }

   @Override
   public String getContextString()
   {
      return "";
   }

   @Override
   public void addDetail(String key, String value)
   {
      
   }

   @SuppressWarnings("unchecked")
   @Override
   public Map<String, String> getDetails()
   {
      return Collections.EMPTY_MAP;
   }

}
