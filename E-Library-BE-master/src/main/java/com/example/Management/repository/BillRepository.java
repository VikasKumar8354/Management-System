package com.example.Management.repository;

import com.example.Management.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByVendorName(String vendorName);
    List<Bill> findByDate(LocalDate date);
    List<Bill> findByStatusIgnoreCase(String status);
    List<Bill> findByPaymentIgnoreCase(String payment);

}

