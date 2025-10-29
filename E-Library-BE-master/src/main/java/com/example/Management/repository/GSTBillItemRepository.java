package com.example.Management.repository;

import com.example.Management.entity.GSTBill;
import com.example.Management.entity.GSTBillItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GSTBillItemRepository extends JpaRepository<GSTBillItem, Long> {


}