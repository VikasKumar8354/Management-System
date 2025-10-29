package com.example.Management.controller;

import com.example.Management.entity.Bill;
import com.example.Management.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    @Autowired
    private BillService billService;

    // Create a new Bill
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PostMapping
    public Bill createBill(@RequestBody Bill bill) {
        return billService.createBill(bill);
    }

    // Get all Bills
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping
    public List<Bill> getAllBills() {
        return billService.getAllBills();
    }

    // Get a Bill by ID
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping("/{id}")
    public Bill getBillById(@PathVariable Long id) {
        return billService.getBillById(id);
    }
    //Get bills by vendor name
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping("/vendor/{vendorName}")
    public List<Bill> getBillsByVendorName(@PathVariable String vendorName) {
        return billService.getBillsByVendorName(vendorName);
    }
    //get bills by date
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/date/{date}")
    public List<Bill> getBillsByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return billService.getBillsByDate(date);
    }
    // Get all pending bills
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping("/pending")
    public List<Bill> getPendingStatusBills() {
        return billService.getPendingStatusBills();
    }
    // Get all bills with payment "pending"
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping("/payment/pending")
    public List<Bill> getPendingPayments() {
        return billService.getPendingPayments();
    }
    // Update a Bill
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PutMapping("/{id}")
    public Bill updateBill(@PathVariable Long id, @RequestBody Bill billDetails) {
        return billService.updateBill(id, billDetails);
    }
    // Delete a Bill
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @DeleteMapping("/{id}")
    public String deleteBill(@PathVariable Long id) {
        boolean isDeleted = billService.deleteBill(id);
        return isDeleted ? "Bill deleted successfully." : "Bill not found.";
    }
}
