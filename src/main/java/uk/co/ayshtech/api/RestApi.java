package uk.co.ayshtech.api;

import java.net.URI;
import javax.validation.Valid;
import javax.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.ayshtech.api.model.TaskDto;
import uk.co.ayshtech.repository.TaskInMemoryRepository;

@RestController
@RequestMapping("/tasks")
@Validated
public class RestApi {

  @Autowired
  private TaskInMemoryRepository taskInMemoryRepository;

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> createTask(@RequestBody @Valid TaskDto taskDto) {
        String taskId = taskInMemoryRepository.saveTask(taskDto);
        return ResponseEntity.created(URI.create("/tasks/"+taskId)).build();
  }

  @GetMapping(value = "/{taskId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<TaskDto> getTask(@PathVariable String taskId) {
    TaskDto taskDto = taskInMemoryRepository.getTask(taskId);
    return ResponseEntity.ok(taskDto);
  }

}
