package com.example.Management.Library.repository;


import com.example.Management.Library.model.DocumentBook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;


public interface DocumentBookRepository extends JpaRepository<DocumentBook, Long>, JpaSpecificationExecutor<DocumentBook> {
    // ✅ Search in safe fields only (title + authoringAgency)
    Page<DocumentBook> findByTitleContainingIgnoreCaseOrDepartmentContainingIgnoreCase(
            String title, String department, Pageable pageable
    );

    Page<DocumentBook> findByTagsIn(List<String> tags, Pageable pageable);

    // You can also keep simple findAll(Pageable pageable)
    Page<DocumentBook> findAll(Pageable pageable);

}


