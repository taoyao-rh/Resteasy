package org.jboss.resteasy.tracing.opentracing;

import io.jaegertracing.internal.JaegerSpan;
import io.opentracing.Span;
import io.opentracing.Tracer.SpanBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.jboss.resteasy.spi.tracing.ResteasyTracePoint;

public class ResteayOpenSpan extends HashMap implements ResteasyTracePoint
{
   private SpanBuilder spanBuilder;

   private JaegerSpan span;

   private ResteasyTracePoint parent;

   protected List<ResteasyTracePoint> childs = new LinkedList<ResteasyTracePoint>();

   public ResteayOpenSpan(SpanBuilder spanBuilder)
   {
      this.spanBuilder = spanBuilder;

   }

   public ResteayOpenSpan(ResteayOpenSpan parent, SpanBuilder spanBuilder)
   {
      this.spanBuilder = spanBuilder.asChildOf(parent.getSpan());
      this.parent = parent;
   }

   public void close() throws IOException
   {
      finish();

   }

   @Override
   public ResteasyTracePoint finish()
   {
      this.span.finish();
      return this;
   }

   public Span getSpan()
   {
      return this.span;
   }

   @Override
   public String getName()
   {
      return span.getOperationName();
   }

   @Override
   public long getDuration()
   {
      return span.getDuration();
   }

   @Override
   public ResteasyTracePoint start()
   {
      span = (JaegerSpan) spanBuilder.start();
      if (parent != null)
      {
         parent.getChildren().add(this);
      }
      return this;
   }

   @Override
   public List<ResteasyTracePoint> getChildren()
   {
      return this.childs;
   }

   @Override
   public String getContextString()
   {
       return span.context().contextAsString();
   }
}
