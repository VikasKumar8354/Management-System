package com.example.Management.controller;


import com.example.Management.dto.VendorRequestDTO;
import com.example.Management.entity.Vendor;
import com.example.Management.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    @Autowired
    private VendorRepository vendorRepository;

    // ✅ Get all vendors with Pagination
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping
    public Page<Vendor> getAllVendors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return vendorRepository.findAll(pageable);
    }

    // ✅ Get Vendor by ID with Pagination
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/{id}")
    public ResponseEntity<Page<Vendor>> getVendorsById(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Vendor> vendorPage = vendorRepository.findById(id, pageable);

        if (vendorPage.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(vendorPage);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PostMapping(consumes = "application/json")
    public ResponseEntity<Vendor> createVendor(@RequestBody VendorRequestDTO dto) {
        try {
            Vendor vendor = new Vendor();
            vendor.setName(dto.getName());
            vendor.setGstNumber(dto.getGstNumber());
            vendor.setAddress(dto.getAddress());
            vendor.setState(dto.getState());
            vendor.setContactNumber(dto.getContactNumber());
            vendor.setEmail(dto.getEmail());
            vendor.setPanNumber(dto.getPanNumber());
            vendor.setBankName(dto.getBankName());
            vendor.setAccountNumber(dto.getAccountNumber());
            vendor.setIfscCode(dto.getIfscCode());

            // Decode base64 image if provided
            if (dto.getBase64Image() != null && !dto.getBase64Image().isEmpty()) {
                byte[] imageBytes = java.util.Base64.getDecoder().decode(dto.getBase64Image());
                vendor.setImage(imageBytes);
            }

            Vendor savedVendor = vendorRepository.save(vendor);
            return ResponseEntity.ok(savedVendor);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }


    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Vendor> updateVendor(@PathVariable Long id, @RequestBody Vendor vendorDetails) {
        Optional<Vendor> vendorOptional = vendorRepository.findById(id);
        if (vendorOptional.isPresent()) {
            Vendor vendor = vendorOptional.get();
            vendor.setName(vendorDetails.getName());
            vendor.setGstNumber(vendorDetails.getGstNumber());
            vendor.setAddress(vendorDetails.getAddress());
            vendor.setState(vendorDetails.getState());
            Vendor updatedVendor = vendorRepository.save(vendor);
            return ResponseEntity.ok(updatedVendor);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVendor(@PathVariable Long id) {
        try {
            vendorRepository.deleteById(id);
            return ResponseEntity.ok("Vendor deleted successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting vendor");
        }
    }


    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getVendorImage(@PathVariable Long id) {
        return vendorRepository.findById(id)
                .map(vendor -> ResponseEntity
                        .ok()
                        .header("Content-Type", "image/jpeg")
                        .body(vendor.getImage()))
                .orElse(ResponseEntity.notFound().build());
    }


    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/fetch")
    public List<Vendor> getAllVendors() {
        List<Vendor> vendors = vendorRepository.findAll();
        return vendors.isEmpty() ? Collections.emptyList() : vendors;
    }


    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("fetchbyid/{id}")
    public ResponseEntity<Vendor> getVendorById(@PathVariable Long id) {
        Optional<Vendor> vendor = vendorRepository.findById(id);
        return vendor.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
