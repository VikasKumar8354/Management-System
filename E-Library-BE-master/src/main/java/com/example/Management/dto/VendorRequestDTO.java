package com.example.Management.dto;
import lombok.Data;

@Data
public class VendorRequestDTO {
    private String name;
    private String gstNumber;
    private String address;
    private String state;
    private String contactNumber;
    private String email;
    private String panNumber;
    private String bankName;
    private String accountNumber;
    private String ifscCode;
    private String base64Image; // Optional base64-encoded image string
}
