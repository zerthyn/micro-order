package org.rhydo.microorder.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ResourceNotFoundException extends RuntimeException {
  String resourceName;
  String fieldName;
  Long fieldId;

  public ResourceNotFoundException(String resourceName, String fieldName, Long fieldId) {
    super(String.format("%s not found with %s: %s", resourceName, fieldName, fieldId));
    this.resourceName = resourceName;
    this.fieldName = fieldName;
    this.fieldId = fieldId;
  }

}
