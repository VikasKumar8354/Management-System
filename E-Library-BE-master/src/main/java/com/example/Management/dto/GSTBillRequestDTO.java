package com.example.Management.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data

public class GSTBillRequestDTO {

    private Long vendor;
    private String vendorName;
    private Long customer;
    private String customerName;
    private List<GSTBillItemDTO> gSTBillItems; // ✅ Important

    private LocalDate billDate;
    private String paymentStatus;
    private String billType;
    private LocalDate paidDate;
    private BigDecimal gstAmount;
    private BigDecimal totalAmount;
    private BigDecimal totalQuantity;

    public List<GSTBillItemDTO> getgSTBillItems() {
        return gSTBillItems;
    }

    public void setgSTBillItems(List<GSTBillItemDTO> gSTBillItems) {
        this.gSTBillItems = gSTBillItems;
    }

// other fields
}

