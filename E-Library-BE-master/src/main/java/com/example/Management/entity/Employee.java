package com.example.Management.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    @Id
    private Long id;
    private String employeeName;
    private String address;
    private Long phone;
    private String email;
    private String role;
    private String bankName;
    private String ifsc;
    private String bankingEmpName;
    private Long AccountNo;


    public Long getId() {
        return id;
    }

    public void setId(Long employeeId) {
        this.id = employeeId;
    }

}
