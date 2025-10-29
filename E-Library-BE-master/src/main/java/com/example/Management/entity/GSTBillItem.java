package com.example.Management.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Data


public class GSTBillItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;    // Item Description (e.g., Brochure Design)
    private String hsnSac;         // HSN/SAC Code (for GST classification)

    private int quantity;          // Quantity of the item
    private BigDecimal rate;       // Rate per item

    private BigDecimal sgstRate;   // SGST rate (in %)
    private BigDecimal cgstRate;   // CGST rate (in %)
    private BigDecimal igstRate;   // Cess rate (if s, in %)

    private BigDecimal sgstAmount; // Calculated SGST amount
    private BigDecimal cgstAmount; // Calculated CGST amount
    private BigDecimal igstAmount; // Calculated Cess amount

    private BigDecimal totalAmount; // Total amount for this item


    @ManyToOne
    @JoinColumn(name = "bill_id")
    private GSTBill bill;
}