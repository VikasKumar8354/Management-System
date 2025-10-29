package com.example.Management.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GSTBillResponseDTO {
    // Bill Details
    private Long billNumber;
    private LocalDate billDate;
    private LocalDate paidDate;
    private String billType;
    private String description;
    private BigDecimal totalAmount;
    private BigDecimal gstAmount;
    private String paymentStatus;

    // Vendor Details
    private Long vendorId;
    private String vendorName;
    private String vendorGstNumber;
    private String vendorAddress;
    private String vendorState;
    private String vendorContactNumber;
    private String vendorEmail;
    private String vendorPanNumber;
    private String vendorBankName;
    private String vendorAccountNumber;
    private String vendorIfscCode;
    private byte[] vendorImage;

    // Customer Details
    private Long customerId;
    private String customerName;
    private String customerGstNumber;
    private String customerAddress;
    private String customerState;
    private String customerContactNumber;
    private String customerEmail;

    // Bill Items
    private List<BillItemDTO> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillItemDTO {
        private Long id;
        private String description;
        private String hsnSac;
        private int quantity;
        private BigDecimal rate;
        private BigDecimal sgstRate;
        private BigDecimal cgstRate;
        private BigDecimal igstRate;
        private BigDecimal sgstAmount;
        private BigDecimal cgstAmount;
        private BigDecimal igstAmount;
        private BigDecimal totalAmount;
    }
}
