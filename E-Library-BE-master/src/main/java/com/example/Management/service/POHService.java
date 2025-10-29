package com.example.Management.service;


import com.example.Management.entity.POH;
import com.example.Management.repository.POHRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class POHService {

    @Autowired
    private POHRepository pohRepository;

    // Approve or Reject a POH request
    public void updatePOHStatus(Long pohId, String status) {
        POH poh = pohRepository.findById(pohId).orElseThrow(() -> new RuntimeException("POH request not found"));
        poh.setStatus(status);
        pohRepository.save(poh);
    }

    // Get count of days an employee was present on holidays in a given month
    public Long getPresentOnHolidaysCount(Long employeeId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        return pohRepository.findByEmployeeIdAndDateBetweenAndStatus(employeeId, startDate, endDate, "Approved").stream().count();
    }

    // Fetch POH requests by employee ID with Pagination
    public Page<POH> getPOHRequestsByEmployee(Long employeeId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return pohRepository.findByEmployeeId(employeeId, pageable);
    }

//    public List<POH> getPOHRequestsByEmployee(Long employeeId) {
//        return pohRepository.findByEmployeeId(employeeId);
//    }

    // Fetch all POH records with pagination
    public Page<POH> getAllPOHRecords(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return pohRepository.findAll(pageable);
    }
}
