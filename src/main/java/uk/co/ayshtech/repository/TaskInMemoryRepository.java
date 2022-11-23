package uk.co.ayshtech.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import uk.co.ayshtech.api.model.TaskDto;

@Repository
public class TaskInMemoryRepository {

  private Map<String, TaskDto> tasksMap = new HashMap<>();

  public String saveTask(TaskDto taskDto) {
        String uuid = UUID.randomUUID().toString();
        tasksMap.put(uuid, taskDto);
        return uuid;
  }

  public TaskDto getTask(String uuid) {
    return tasksMap.get(uuid);
  }

}
