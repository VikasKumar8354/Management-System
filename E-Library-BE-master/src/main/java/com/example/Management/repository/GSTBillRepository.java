package com.example.Management.repository;


import com.example.Management.entity.GSTBill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GSTBillRepository extends JpaRepository<GSTBill, Long> {
    Page<GSTBill> findAll(Pageable pageable);

    @Query("SELECT b FROM GSTBill b LEFT JOIN FETCH b.gSTBillItems WHERE b.id = :billId")
    Optional<GSTBill> findByIdWithItems(@Param("billId") Long billId);
}