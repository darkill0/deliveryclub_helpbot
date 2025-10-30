package com.deliveryclub.helpbot.service;

import com.deliveryclub.helpbot.models.Task;
import com.deliveryclub.helpbot.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    // Сохранение задачи
    public void saveTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        if (task.getStatus() == null) {
            task.setStatus("pending");
        }
        taskRepository.save(task);
    }

    // Получение задач пользователя по chatId
    public List<Task> getTasksByUser(String chatId) {
        return taskRepository.findByAssignedTo(chatId);
    }

    // Получение задачи по ID
    public Task getTaskById(String taskId) {
        return taskRepository.findById(taskId).orElse(null);
    }
}