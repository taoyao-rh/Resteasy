package org.jboss.resteasy.test.providers.jsonb.basic.resource;

import org.jboss.resteasy.annotations.providers.NoJackson;
import javax.json.bind.annotation.JsonbPropertyOrder;

/**
 * The annotation, NoJackson is required in order for the jackson provider not to be used.
 * Created by rsearls.
 */
//@NoJackson
@JsonbPropertyOrder({"color", "sort", "name", "domesticated"})
public class Cat {

   private String name;
   private String sort;
   private String color;
   private boolean domesticated;

   // json-b needs the default constructor
   public Cat() {
      super();
   }

   public Cat(String name, String sort, String color, boolean domesticated) {
      this.name = name;
      this.sort = sort;
      this.color = color;
      this.domesticated = domesticated;
   }

   public String getName() {
      return name;
   }

   public Cat setName(String name) {
      this.name = name;
      return this;
   }

   public String getSort() {
      return sort;
   }

   public Cat setSort(String sort) {
      this.sort = sort;
      return this;
   }

   public String getColor() {
      return color;
   }

   public Cat setColor(String color) {
      this.color = color;
      return this;
   }

   public boolean isDomesticated() {
      return domesticated;
   }

   public Cat setDomesticated(boolean domesticated) {
      this.domesticated = domesticated;
      return this;
   }
}

