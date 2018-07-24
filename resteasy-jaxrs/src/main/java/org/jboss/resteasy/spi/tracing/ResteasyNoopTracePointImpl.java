package org.jboss.resteasy.spi.tracing;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResteasyNoopTracePointImpl implements ResteasyTracePoint
{

   public ResteasyNoopTracePointImpl(ResteasyTracePoint parent, String operationName)
   {
   }

   public ResteasyNoopTracePointImpl(String operationName)
   {

   }
   @Override
   public int size()
   {
      return 0;
   }

   @Override
   public boolean isEmpty()
   {
      return false;
   }

   @Override
   public boolean containsKey(Object key)
   {
      return false;
   }

   @Override
   public boolean containsValue(Object value)
   {
      return false;
   }

   @Override
   public Object get(Object key)
   {
      return null;
   }

   @Override
   public Object put(Object key, Object value)
   {
      return null;
   }

   @Override
   public Object remove(Object key)
   {
      return null;
   }

   @Override
   public void putAll(Map m)
   {
      
   }

   @Override
   public void clear()
   {
   }

   @Override
   public Set keySet()
   {
      return null;
   }

   @Override
   public Collection values()
   {
      return null;
   }

   @Override
   public Set entrySet()
   {
      return null;
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

}
