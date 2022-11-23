package uk.co.ayshtech.api.model;

import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class TaskDto {
  @NotEmpty
  private String name;
  @NotEmpty
  private String assignedTo;
  private String details;
}
