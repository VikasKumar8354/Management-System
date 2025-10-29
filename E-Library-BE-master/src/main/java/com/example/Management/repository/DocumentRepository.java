package com.example.Management.repository;

import com.example.Management.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByEmployeeId(Long employeeId);

    // Updated method with pagination support
    Page<Document> findByTypeOfDoc(String typeOfDoc, Pageable pageable);

    Page<Document> findByEmployeeId(Long employeeId, Pageable pageable);

    Page<Document> findAll(Pageable pageable);
}