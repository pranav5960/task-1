package com.example.taskapi.controller;
import com.example.taskapi.model.Task;
import com.example.taskapi.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService service;

    public TaskController(TaskService service) { this.service = service; }

    @GetMapping
    public ResponseEntity<?> getTasks(@RequestParam(required = false) String id) {
        if (id != null)
            return service.getTaskById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        return ResponseEntity.ok(service.getAllTasks());
    }
    @GetMapping("/")
    public String home() {
        return "✅ Task API is running!";
}

    @PutMapping
    public ResponseEntity<?> createTask(@RequestBody Task task) {
        return ResponseEntity.ok(service.saveTask(task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable String id) {
        service.deleteTask(id);
        return ResponseEntity.ok("Deleted task " + id);
    }

    @GetMapping("/search")
    public ResponseEntity<?> findByName(@RequestParam String name) {
        List<Task> found = service.findByName(name);
        if (found.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(found);
    }

    @PutMapping("/{id}/execute")
    public ResponseEntity<?> executeTask(@PathVariable String id) {
        try {
            return ResponseEntity.ok(service.executeTask(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
