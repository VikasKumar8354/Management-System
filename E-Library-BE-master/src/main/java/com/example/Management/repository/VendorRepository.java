package com.example.Management.repository;


import com.example.Management.entity.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Page<Vendor> findAll(Pageable pageable);

    Page<Vendor> findById(Long id, Pageable pageable);
}