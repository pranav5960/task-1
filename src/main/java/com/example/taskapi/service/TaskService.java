package com.example.taskapi.service;

import com.example.taskapi.model.Task;
import com.example.taskapi.model.TaskExecution;
import com.example.taskapi.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class TaskService {
    private final TaskRepository repo;

    public TaskService(TaskRepository repo) { this.repo = repo; }

    public List<Task> getAllTasks() { return repo.findAll(); }

    public Optional<Task> getTaskById(String id) { return repo.findById(id); }

    public Task saveTask(Task task) {
        if (task.getCommand().contains("rm") || task.getCommand().contains("shutdown")) {
            throw new IllegalArgumentException("Unsafe command detected!");
        }
        return repo.save(task);
    }

    public void deleteTask(String id) { repo.deleteById(id); }

    public List<Task> findByName(String name) { return repo.findByNameContainingIgnoreCase(name); }

    public Task executeTask(String id) throws Exception {
        Task task = repo.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        String command = task.getCommand();

        TaskExecution exec = new TaskExecution();
        exec.setStartTime(new Date());

        ProcessBuilder builder = new ProcessBuilder("bash", "-c", command);
        Process process = builder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String output = reader.lines().reduce("", (a, b) -> a + "\n" + b);
        process.waitFor();

        exec.setEndTime(new Date());
        exec.setOutput(output);

        task.getTaskExecutions().add(exec);
        return repo.save(task);
    }
}
