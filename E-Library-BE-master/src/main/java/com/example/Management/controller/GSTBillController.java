package com.example.Management.controller;

import com.example.Management.dto.GSTBillRequestDTO;
import com.example.Management.dto.GSTBillResponseDTO;
import com.example.Management.entity.GSTBillItem;
import com.example.Management.repository.GSTBillItemRepository;
import com.example.Management.repository.GSTBillRepository;
import com.example.Management.service.GSTBillService;
import com.example.Management.entity.GSTBill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/GSTbills")
public class GSTBillController {

    @Autowired
    private GSTBillService GSTBillService;


    @Autowired
    private GSTBillItemRepository gstBillItemRepository;
    @Autowired
    private GSTBillRepository gstBillRepository;

    // ✅ Get All Bills with Pagination
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping
    public Page<GSTBill> getAllBills(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return GSTBillService.getAllBills(pageable);
    }

    // ✅ Get Bill by ID
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/{id}")
    public ResponseEntity<GSTBill> getBillById(@PathVariable Long id) {
        Optional<GSTBill> bill = GSTBillService.getBillById(id);
        return bill.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Create Bill
    // ✅ Update Bill

//    @PostMapping("/gst-bill")
//    public ResponseEntity<GSTBill> createGSTBill(@RequestBody GSTBillRequestDTO dto) {
//        GSTBill savedBill = GSTBillService.createBill(dto);
//        return ResponseEntity.ok(savedBill);
//    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PostMapping("/gst")
    public ResponseEntity<GSTBill> createGSTBill(@RequestBody GSTBillRequestDTO gSTBillDto) {
        try {
            System.out.println(gSTBillDto);
            System.out.println(gSTBillDto.getgSTBillItems());
            // ✅ Manual mapping from DTO to Entity
            GSTBill bill = new GSTBill();
            bill.setBillDate(gSTBillDto.getBillDate());
            bill.setBillType(gSTBillDto.getBillType());
            bill.setCustomer(gSTBillDto.getCustomer());
            bill.setVendor(gSTBillDto.getVendor());
            bill.setVendorName(gSTBillDto.getVendorName());
            bill.setCustomerName(gSTBillDto.getCustomerName());
            bill.setGstAmount(gSTBillDto.getGstAmount());
            bill.setPaymentStatus(gSTBillDto.getPaymentStatus());
            bill.setTotalAmount(gSTBillDto.getTotalAmount());

            // Convert DTO items to entity items
            List<GSTBillItem> billItems = gSTBillDto.getgSTBillItems().stream().map(itemDto -> {
                GSTBillItem item = new GSTBillItem();
                item.setDescription(itemDto.getDescription());
                item.setHsnSac(itemDto.getHsnSac());
                item.setQuantity(itemDto.getQuantity());
                item.setRate(itemDto.getRate());
                item.setSgstRate(itemDto.getSgstRate());
                item.setCgstRate(itemDto.getCgstRate());
                item.setIgstRate(itemDto.getIgstRate());
                item.setSgstAmount(itemDto.getSgstAmount());
                item.setCgstAmount(itemDto.getCgstAmount());
                item.setIgstAmount(itemDto.getIgstAmount());
                item.setTotalAmount(itemDto.getTotalAmount());
                return item;
            }).toList();

            bill.setGSTBillItems(billItems);

            // ✅ Call service
            GSTBill savedBill = GSTBillService.createGSTBill(bill);

            // ✅ Return response with saved bill
            return ResponseEntity.ok(savedBill);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PostMapping
    public ResponseEntity<GSTBill> createBill(@RequestBody GSTBillRequestDTO gSTBill) {
        System.out.println(gSTBill);
        System.out.println(gSTBill.getgSTBillItems());
        try {
            GSTBill bill = new GSTBill();
            bill.setBillDate(gSTBill.getBillDate());
            bill.setBillType(gSTBill.getBillType());
            //bill.setGSTBillItems(gSTBill.getgSTBillItems());
            bill.setCustomer(gSTBill.getCustomer());
            bill.setVendor(gSTBill.getVendor());
            bill.setVendorName(gSTBill.getVendorName());
            bill.setCustomerName(gSTBill.getCustomerName());
            bill.setGstAmount(gSTBill.getGstAmount());
            bill.setPaymentStatus(gSTBill.getPaymentStatus());
            bill.setTotalAmount(gSTBill.getTotalAmount());
//            gstBillRepository.save(bill);

            GSTBill savedGSTBill = GSTBillService.createBill(bill);
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }


    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<GSTBill> updateBill(@PathVariable Long id, @RequestBody GSTBill GSTBillDetails) {
        try {
            GSTBill updatedGSTBill = GSTBillService.updateBill(id, GSTBillDetails);
            return ResponseEntity.ok(updatedGSTBill);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // ✅ Mark Bill as Paid
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/{id}/markPaid")
    public ResponseEntity<GSTBill> markBillAsPaid(@PathVariable Long id) {
        try {
            GSTBill updatedGSTBill = GSTBillService.paidStatusMark(id);
            return ResponseEntity.ok(updatedGSTBill);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // ✅ Delete Bill
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBill(@PathVariable Long id) {
        try {
            GSTBillService.deleteBill(id);
            return ResponseEntity.ok("Bill deleted successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting bill");
        }
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/bill/{billId}")
    public ResponseEntity<GSTBillResponseDTO> getBillDetails(@PathVariable Long billId) {
        GSTBillResponseDTO response = GSTBillService.getBillDetails(billId);
        return ResponseEntity.ok(response);
    }
}
