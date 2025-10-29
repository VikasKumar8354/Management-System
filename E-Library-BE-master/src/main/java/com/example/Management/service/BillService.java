package com.example.Management.service;

import com.example.Management.entity.Bill;
import com.example.Management.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class BillService {

    @Autowired
    private BillRepository billRepository;

    private long generateBillId() {
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        long sequenceNumber = billRepository.count() + 1;
        String generatedId = datePrefix + "01S"+String.format("%04d", sequenceNumber);
        return Long.parseLong(generatedId);
    }
    // Create a new Bill
    public Bill createBill(Bill bill) {
        long generatedBillId = generateBillId();
        bill.setBillId(generatedBillId);
        bill.setDate(LocalDate.now());
        return billRepository.save(bill);
    }
    public List<Bill> getBillsByDate(LocalDate date) {
        return billRepository.findByDate(date);
    }
    // Retrieve all Bills
    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }
    // Retrieve a Bill by ID
    public Bill getBillById(Long id) {
        return billRepository.findById(id).orElse(null);
    }
    public List<Bill> getBillsByVendorName(String vendorName) {
        return billRepository.findByVendorName(vendorName);
    }
    public List<Bill> getPendingStatusBills() {
        return billRepository.findByStatusIgnoreCase("pending");
    }
    // Get all bills where payment is pending
    public List<Bill> getPendingPayments() {
        return billRepository.findByPaymentIgnoreCase("pending");
    }
    // Update a Bill
    public Bill updateBill(Long id, Bill billDetails) {
        Bill bill = billRepository.findById(id).orElse(null);
        if (bill != null) {
            bill.setDate(LocalDate.now());
            bill.setVendorName(billDetails.getVendorName());
            bill.setStatus(billDetails.getStatus());
            bill.setPayment(billDetails.getPayment());
            return billRepository.save(bill);
        }
        return null;
    }
    // Delete a Bill
    public boolean deleteBill(Long id) {
        Bill bill = billRepository.findById(id).orElse(null);
        if (bill != null) {
            billRepository.delete(bill);
            return true;
        }
        return false;
    }
}
