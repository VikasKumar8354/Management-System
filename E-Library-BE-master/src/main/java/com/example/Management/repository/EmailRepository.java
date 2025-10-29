package com.example.Management.repository;

import com.example.Management.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmailRepository extends JpaRepository<Email, String> {

//    List<Email> findByEmpName(String empName);
//


    String findTopByIdStartingWithOrderByIdDesc(String prefix);
}