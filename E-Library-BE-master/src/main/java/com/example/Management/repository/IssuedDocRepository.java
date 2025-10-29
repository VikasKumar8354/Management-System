package com.example.Management.repository;

import com.example.Management.entity.IssuedDoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IssuedDocRepository extends JpaRepository<IssuedDoc, Long> {
    Optional<IssuedDoc> findByEmployeeId(Long employeeId);

    Page<IssuedDoc> findAll(Pageable pageable);

    Page<IssuedDoc> findByEmployeeId(Long employeeId, Pageable pageable);

    Page<IssuedDoc> findByTypeOfDoc(String typeOfDoc, Pageable pageable);

    Page<IssuedDoc> findByIssuedBy(String issuedBy, Pageable pageable);

    Page<IssuedDoc> findByDateOfIssue(LocalDate dateOfIssue, Pageable pageable);

    Page<IssuedDoc> findByEmpEmail(String empEmail, Pageable pageable);

    void deleteById(String id);

    Optional<IssuedDoc> findById(String id);
}