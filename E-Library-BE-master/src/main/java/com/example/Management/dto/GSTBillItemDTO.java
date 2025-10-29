package com.example.Management.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GSTBillItemDTO {
    private String description;
    private String hsnSac;
    private int quantity;
    private BigDecimal rate;
    private BigDecimal sgstRate;
    private BigDecimal cgstRate;
    private BigDecimal igstRate;   // Cess rate (if s, in %)

    private BigDecimal sgstAmount; // Calculated SGST amount
    private BigDecimal cgstAmount; // Calculated CGST amount
    private BigDecimal igstAmount; // Calculated Cess amount    private GSTBill bill;
    private BigDecimal totalAmount;
}
