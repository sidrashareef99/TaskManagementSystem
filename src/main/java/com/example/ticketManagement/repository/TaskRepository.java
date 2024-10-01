package com.example.ticketManagement.repository;

import com.example.ticketManagement.model.Employee;
import com.example.ticketManagement.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByEmployee(Employee employee);
}
