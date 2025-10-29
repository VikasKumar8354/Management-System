package com.example.Management.service;

import com.example.Management.entity.Employee;
import com.example.Management.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;



    public Employee createEmployee(Employee employee) {
        employee.setId(generateEmployeeId());
        System.out.println(employee);
        return employeeRepository.save(employee);
    }


    Long generateEmployeeId() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        List<Employee> employees = employeeRepository.findAll();
        int maxId = 0;
        for (Employee emp : employees) {
            String empIdStr = emp.getId().toString();
            if (empIdStr.startsWith(datePart)) {
                int currentId = Integer.parseInt(empIdStr.substring(6));
                if (currentId > maxId) {
                    maxId = currentId;
                }
            }
        }
        int newIdNumber = maxId + 1;
        String newIdPart = String.format("%04d", newIdNumber);
        return Long.parseLong(datePart + newIdPart);
    }
//    public List<Employee> getAllEmployees() {
//        return employeeRepository.findAll();
//    }
    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }
    public List<Employee> getEmployeesByRole(String role) {
        return employeeRepository.findByRole(role);
    }
    public Employee updateEmployee(Long id, Employee employeeDetails) {
        Employee employee = employeeRepository.findById(id).orElseThrow();
        employee.setEmployeeName(employeeDetails.getEmployeeName());
        employee.setAddress(employeeDetails.getAddress());
        employee.setPhone(employeeDetails.getPhone());
        employee.setEmail(employeeDetails.getEmail());
        employee.setRole(employeeDetails.getRole());
        return employeeRepository.save(employee);
    }
    public Page<Employee> getAllEmployees(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return employeeRepository.findAll(pageable);
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
}
