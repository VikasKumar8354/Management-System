package com.example.Management.service;

import com.example.Management.entity.Email;
import com.example.Management.repository.EmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class EmailRecordService {

    @Autowired
    private EmailRepository emailRepository;


    public List<Email> getAllEmails() {
        return emailRepository.findAll();
    }

    public Optional<Email> getEmailById(String id) {
        return emailRepository.findById(id);
    }

    public Email saveEmail(Email email) {
        Email newEmailRecord = new Email();
        newEmailRecord.setId(generateEmailRecordId());
        newEmailRecord.setDateTime(LocalDateTime.now());
        return emailRepository.save(email);
    }


    private String generateEmailRecordId() {
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        String month = today.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toUpperCase();
        String prefix = year + month;

        // Fetch the last ID that starts with the prefix, ordered by descending ID
        String lastId = emailRepository.findTopByIdStartingWithOrderByIdDesc(prefix);

        int lastNumber = 0;
        if (lastId != null && lastId.length() > prefix.length()) {
            try {
                lastNumber = Integer.parseInt(lastId.substring(prefix.length()));
            } catch (NumberFormatException e) {
                lastNumber = 0;
            }
        }

        return String.format("%s%04d", prefix, lastNumber + 1);
    }



    public Email updateEmail(String id, Email emailDetails) {
        return emailRepository.findById(id).map(email -> {
            email.setLettertype(emailDetails.getLettertype());
            email.setEmployeeName(emailDetails.getEmployeeName());
            email.setDateTime(emailDetails.getDateTime());
            email.setRecipientEmail(emailDetails.getRecipientEmail());
            email.setEmailBody(emailDetails.getEmailBody());
            return emailRepository.save(email);
        }).orElseThrow(() -> new RuntimeException("Email not found with id " + id));
    }

    public void deleteEmail(String id) {
        emailRepository.deleteById(id);
    }

    public Page<Email> getAllEmails(Pageable pageable) {
        return emailRepository.findAll(pageable);
    }


//    public List<Email> getEmailsByFilters(String empName, String typeOfLetter, LocalDate date) {
//        if (empName != null && typeOfLetter != null && date != null) {
//            return emailRepository.findByEmpNameAndTypeOfLetterAndDate(empName, typeOfLetter, date);
//        } else if (empName != null && typeOfLetter != null) {
//            return emailRepository.findByEmpNameAndTypeOfLetter(empName, typeOfLetter);
//        } else if (empName != null && date != null) {
//            return emailRepository.findByEmpNameAndDate(empName, date);
//        } else if (typeOfLetter != null && date != null) {
//            return emailRepository.findByTypeOfLetterAndDate(typeOfLetter, date);
//        } else if (empName != null) {
//            return emailRepository.findByEmpName(empName);
//        } else if (typeOfLetter != null) {
//            return emailRepository.findByTypeOfLetter(typeOfLetter);
//        } else if (date != null) {
//            return emailRepository.findByDate(date);
//        } else {
//            return emailRepository.findAll();
//        }
//    }


}