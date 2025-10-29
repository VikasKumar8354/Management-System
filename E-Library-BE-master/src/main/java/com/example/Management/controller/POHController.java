package com.example.Management.controller;


import com.example.Management.entity.Employee;
import com.example.Management.entity.POH;
import com.example.Management.repository.POHRepository;
import com.example.Management.service.POHService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/poh")
public class POHController {

    @Autowired
    private POHService pohService;
    @Autowired
    private POHRepository pohRepository;

    // Add a new POH request , used by Employees
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PostMapping("/save")
    public POH addPOHRequest(@RequestParam Long employeeId, @RequestParam LocalDate date)
    {
        POH poh = new POH();
        Employee employee = new Employee();
        employee.setId(employeeId); // Setting only ID to avoid fetching full object

        poh.setEmployee(employee);
        poh.setDate(date);
        poh.setStatus("Pending"); // Default status

        return pohRepository.save(poh);
    }

    // API to approve or reject a POH request
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PutMapping("/update-status")
    public String updatePOHStatus(
            @RequestParam Long pohId,
            @RequestParam String status) {
        pohService.updatePOHStatus(pohId, status);
        return "POH status updated successfully.";
    }

    // API to get count of present on holidays for an employee in a given month
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/present-count")
    public Long getPresentOnHolidaysCount(
            @RequestParam Long employeeId,
            @RequestParam int year,
            @RequestParam int month) {
        return pohService.getPresentOnHolidaysCount(employeeId, year, month);
    }

//    //retrieves POH records based on employeeId:
//    @GetMapping("/employee/{employeeId}")
//    public List<POH> getPOHRequestsByEmployee(@PathVariable Long employeeId) {
//        return pohService.getPOHRequestsByEmployee(employeeId);
//    }

    // Endpoint to approve a POH request
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PutMapping("/{id}/approve")
    public ResponseEntity<String> approvePOH(@PathVariable Long id) {
        Optional<POH> pohOptional = pohRepository.findById(id);

        if (pohOptional.isPresent()) {
            POH poh = pohOptional.get();
            poh.setStatus("Approved");
            pohRepository.save(poh);
            return ResponseEntity.ok("POH status updated to: Approved");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint to reject a POH request
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PutMapping("/{id}/reject")
    public ResponseEntity<String> rejectPOH(@PathVariable Long id) {
        Optional<POH> pohOptional = pohRepository.findById(id);

        if (pohOptional.isPresent()) {
            POH poh = pohOptional.get();
            poh.setStatus("Rejected");
            pohRepository.save(poh);
            return ResponseEntity.ok("POH status updated to: Rejected");
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    // Get POH Requests by Employee with Pagination
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/employee/{employeeId}")

    public Page<POH> getPOHRequestsByEmployee(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return pohService.getPOHRequestsByEmployee(employeeId, page, size);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/all")
    public Page<POH> getAllPOHRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return pohService.getAllPOHRecords(page, size);
    }
}
