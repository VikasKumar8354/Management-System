package com.example.Management.repository;

import com.example.Management.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByRole(String role);
    Optional<Employee> findByEmployeeName(String employeeName);

    Page<Employee> findAll(Pageable pageable);

    Employee findByIdAndRole(Long managerId, String manager);

    Optional<Employee> findByEmail(String recipientEmail);
}


