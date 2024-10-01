package com.example.ticketManagement.controller;


import com.example.ticketManagement.model.Employee;
import com.example.ticketManagement.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.ticketManagement.service.EmployeeService;
import com.example.ticketManagement.service.TaskService;

import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private EmployeeService employeeService;

    // Admin view for listing all tasks
    @GetMapping
    public String listAllTasks(Model model) {
        if (!isAuthenticatedAdmin()) {
            return "error/403";  // Simulated auth check for admin
        }
        List<Task> tasks = taskService.getAllTasks();
        model.addAttribute("tasks", tasks);
        return "task-list";
    }

    // Admin editing a task
    @GetMapping("/edit/{id}")
    public String editTask(@PathVariable Long id, Model model) {
        if (!isAuthenticatedAdmin()) {
            return "error/403";
        }
        Task task = taskService.getTaskById(id);
        if (task == null) {
            return "error/404";  // Handle case where task is not found
        }
        model.addAttribute("task", task);
        List<Employee> employees = employeeService.getAllEmployees();
        model.addAttribute("employees", employees);
        return "task-form";
    }

    @PostMapping("/save")
    public String saveTask(@ModelAttribute("task") Task task) {
        if (!isAuthenticatedAdmin()) {
            return "error/403";
        }
        taskService.saveTask(task);
        return "redirect:/tasks";
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        if (!isAuthenticatedAdmin()) {
            return "error/403";
        }
        taskService.deleteTask(id);
        return "redirect:/tasks";
    }

    // Employee task hub
    @GetMapping("/employee")
    public String employeeTaskHub(Model model) {
        Employee employee = getAuthenticatedEmployee();  // Simulated employee auth
        List<Task> tasks = taskService.getTasksByEmployee(employee);
        model.addAttribute("tasks", tasks);
        model.addAttribute("employee", employee);
        return "task-hub";
    }

    // Employee updating the task status
    @PostMapping("/update-status")
    public String updateTaskStatus(@RequestParam Long taskId, @RequestParam String status) {
        Employee employee = getAuthenticatedEmployee();
        Task task = taskService.getTaskById(taskId);
        if (task == null || !task.getEmployee().getId().equals(employee.getId())) {
            return "error/403";  // Employees can only update their own tasks
        }
        taskService.updateTaskStatus(taskId, status);
        return "redirect:/tasks/employee";
    }

    // Admin creating a new task
    @GetMapping("/new")
    public String showCreateTaskForm(Model model) {
        Task task = new Task(); // Create an empty task object
        List<Employee> employees = employeeService.getAllEmployees(); // Fetch employees for assignment
        model.addAttribute("task", task); // Pass the empty task to the form
        model.addAttribute("employees", employees); // Pass the employees list
        return "task-form"; // Return the form view
    }

    // Simulate user authentication
    private Employee getAuthenticatedEmployee() {
        return employeeService.getEmployeeById(2L);  // Hardcoded for testing
    }

    private boolean isAuthenticatedAdmin() {
        return false;  // Simulated admin role for testing
    }
}