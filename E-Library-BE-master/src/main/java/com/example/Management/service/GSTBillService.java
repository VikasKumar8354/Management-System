package com.example.Management.service;

import com.example.Management.dto.GSTBillItemDTO;
import com.example.Management.dto.GSTBillRequestDTO;
import com.example.Management.dto.GSTBillResponseDTO;
import com.example.Management.entity.Customer;
import com.example.Management.entity.GSTBillItem;
import com.example.Management.entity.Vendor;
import com.example.Management.repository.CustomerRepo;
import com.example.Management.repository.GSTBillItemRepository;
import com.example.Management.repository.GSTBillRepository;
import com.example.Management.entity.GSTBill;
import com.example.Management.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GSTBillService {

    @Autowired
    private GSTBillRepository gSTBillRepo;

    @Autowired
    private GSTBillItemRepository GSTBillItemRepository;
    @Autowired
    private VendorRepository vendorRepository;
    @Autowired
    private CustomerRepo customerRepository;

    // ✅ Get All Bills with Pagination
    public Page<GSTBill> getAllBills(Pageable pageable) {
        return gSTBillRepo.findAll(pageable);
    }

    public Optional<GSTBill> getBillById(Long id) {
        return gSTBillRepo.findById(id);
    }

    public GSTBill createBill(GSTBill gSTBill) {
        try {
            System.out.println(gSTBill);

            // Set payment status
            gSTBill.setPaymentStatus("PENDING");

            // ✅ First, save the bill to get the billNumber (ID)
            GSTBill savedBill = gSTBillRepo.save(gSTBill);

            // ✅ Now, save the items with correct bill reference
            if (gSTBill.getGSTBillItems() != null && !gSTBill.getGSTBillItems().isEmpty()) {
                gSTBill.getGSTBillItems().forEach(item -> {
                    item.setBill(savedBill); // set the saved bill
                    GSTBillItemRepository.save(item); // save the item
                });
            }

            System.err.println("***************** " + savedBill);
            return savedBill;
        } catch (Exception e) {
            throw new RuntimeException("Error creating bill", e);
        }
    }


    public GSTBill createGSTBill(GSTBill gSTBill) {
        try {
            System.out.println("Bill to save: " + gSTBill);

            // Set payment status
            gSTBill.setPaymentStatus("PENDING");

            // First, save the bill to get the generated ID
            GSTBill savedBill = gSTBillRepo.save(gSTBill);

            // Now, save the items with the correct bill reference
            if (gSTBill.getGSTBillItems() != null && !gSTBill.getGSTBillItems().isEmpty()) {
                gSTBill.getGSTBillItems().forEach(item -> {
                    item.setBill(savedBill); // set the saved bill reference
                    GSTBillItemRepository.save(item); // save the item
                });
            }

            System.err.println("Saved Bill: " + null);
            return null;

        } catch (Exception e) {
            throw new RuntimeException("Error creating bill", e);
        }
    }




    public GSTBill paidStatusMark(Long id) {
        return gSTBillRepo.findById(id).map(GSTBill -> {
            GSTBill.setPaymentStatus("PAID");
            GSTBill.setPaidDate(java.time.LocalDate.now());
            return gSTBillRepo.save(GSTBill);
        }).orElseThrow(() -> new RuntimeException("Bill not found with id " + id));
    }

    public GSTBill updateBill(Long id, GSTBill GSTBillDetails) {
        return gSTBillRepo.findById(id).map(GSTBill -> {
            GSTBill.setBillDate(GSTBillDetails.getBillDate());
            GSTBill.setPaidDate(GSTBillDetails.getPaidDate());
            GSTBill.setGSTBillItems(GSTBillDetails.getGSTBillItems());
            GSTBill.setBillType(GSTBillDetails.getBillType());
            GSTBill.setDescription(GSTBillDetails.getDescription());
            GSTBill.setTotalAmount(GSTBillDetails.getTotalAmount());
            GSTBill.setGstAmount(GSTBillDetails.getGstAmount());
            GSTBill.setPaymentStatus(GSTBillDetails.getPaymentStatus());
            GSTBill.setVendor(GSTBillDetails.getVendor());
            GSTBill.setVendorName(GSTBillDetails.getVendorName());
            return gSTBillRepo.save(GSTBill);
        }).orElseThrow(() -> new RuntimeException("Bill not found with id " + id));
    }

    public void deleteBill(Long id) {
        try {
            gSTBillRepo.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting bill", e);
        }
    }

    public GSTBillResponseDTO getBillDetails(Long billId) {
        GSTBill bill = gSTBillRepo.findByIdWithItems(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        Vendor vendor = vendorRepository.findById(bill.getVendor())
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        Customer customer = customerRepository.findById(bill.getCustomer())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        List<GSTBillResponseDTO.BillItemDTO> itemDTOs = bill.getGSTBillItems().stream()
                .map(item -> new GSTBillResponseDTO.BillItemDTO(
                        item.getId(),
                        item.getDescription(),
                        item.getHsnSac(),
                        item.getQuantity(),
                        item.getRate(),
                        item.getSgstRate(),
                        item.getCgstRate(),
                        item.getIgstRate(),
                        item.getSgstAmount(),
                        item.getCgstAmount(),
                        item.getIgstAmount(),
                        item.getTotalAmount()
                ))
                .toList();

        return new GSTBillResponseDTO(
                bill.getBillNumber(),
                bill.getBillDate(),
                bill.getPaidDate(),
                bill.getBillType(),
                bill.getDescription(),
                bill.getTotalAmount(),
                bill.getGstAmount(),
                bill.getPaymentStatus(),

                vendor.getId(),
                vendor.getName(),
                vendor.getGstNumber(),
                vendor.getAddress(),
                vendor.getState(),
                vendor.getContactNumber(),
                vendor.getEmail(),
                vendor.getPanNumber(),
                vendor.getBankName(),
                vendor.getAccountNumber(),
                vendor.getIfscCode(),
                vendor.getImage(),

                customer.getId(),
                customer.getName(),
                customer.getGstNumber(),
                customer.getAddress(),
                customer.getState(),
                customer.getContactNumber(),
                customer.getEmail(),

                itemDTOs
        );
    }

}