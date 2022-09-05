package com.iot.api.model;

public enum OperationType {
   AVG("AVG"), MEDIAN("MEDIAN"),MAX("MAX"),MIN("MIN");
   private final String type;
   OperationType(String type) {
      this.type = type;
   }
   public String getType() {
      return type;
   }
}
