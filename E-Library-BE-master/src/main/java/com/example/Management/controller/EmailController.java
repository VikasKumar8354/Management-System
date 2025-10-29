package com.example.Management.controller;

import com.example.Management.entity.Email;
import com.example.Management.entity.EmailTemplate;
import com.example.Management.service.EmailRecordService;
import com.example.Management.service.EmailService;
import com.example.Management.service.EmailTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailRecordService emailRecordService;
    private final EmailService emailService;
    private final EmailTemplateService emailTemplateService;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN','USER')")
    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody Email request) {
        System.out.println("Received Email request: " + request);
        String response = emailService.sendEmail(request);
        if (response.startsWith("Error")) {
            return ResponseEntity.ok().body(response);
        }
        return ResponseEntity.ok(response);
    }


//        @PostMapping("/send")
//        public ResponseEntity<String> sendEmail (
//                @RequestParam String templateName,
//                @RequestParam String recipientEmail,
//                @RequestParam Map < String, String > placeholders){
//
//            String response = emailService.sendEmail(templateName, recipientEmail, placeholders);
//            return ResponseEntity.ok(response);
//        }



    // ✅ ADD NEW EMAIL TEMPLATE
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PostMapping("/template")
    public ResponseEntity<String> addTemplate (@RequestBody EmailTemplate template){
        return ResponseEntity.ok(emailTemplateService.addTemplate(template));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/templatetype/{type}")
    public ResponseEntity<List<EmailTemplate>> getTemplateByType(@PathVariable String type){
        return ResponseEntity.ok(emailTemplateService.getTemplateByType(type));
    }
    // ✅ GET EMAIL TEMPLATE BY NAME
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/template/{name}")
    public ResponseEntity<EmailTemplate> getTemplate (@PathVariable String name){
        return ResponseEntity.ok(emailTemplateService.getTemplateByName(name));
    }



    // ✅ GET ALL EMAIL TEMPLATES
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/templates")
    public ResponseEntity<List<EmailTemplate>> getAllTemplates () {
        return ResponseEntity.ok(emailTemplateService.getAllTemplates());
    }

    // ✅ UPDATE AN EMAIL TEMPLATE
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PutMapping("/template/{id}")
    public ResponseEntity<String> updateTemplate (@PathVariable Long id, @RequestBody EmailTemplate updatedTemplate)
    {
        return ResponseEntity.ok(emailTemplateService.updateTemplate(id, updatedTemplate));
    }

    // ✅ DELETE AN EMAIL TEMPLATE
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @DeleteMapping("/template/{id}")
    public ResponseEntity<String> deleteTemplate (@PathVariable Long id){
        return ResponseEntity.ok(emailTemplateService.deleteTemplate(id));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/mailrecordpage/getall")
    public ResponseEntity<Page<Email>> getAllEmails(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Email> emails = emailRecordService.getAllEmails(pageable);
        return ResponseEntity.ok(emails);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/mailrecord/{id}")
    public ResponseEntity<Email> getEmailById(@PathVariable String id) {
        Optional<Email> email = emailRecordService.getEmailById(id);
        return email.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PostMapping("/mailrecord/save")
    public ResponseEntity<Email> createEmail(@RequestBody Email email) {
        Email savedEmail = emailRecordService.saveEmail(email);
        return ResponseEntity.ok(savedEmail);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PutMapping("/mailrecord/{id}")
    public ResponseEntity<Email> updateEmail(@PathVariable String id, @RequestBody Email emailDetails) {
        Email updatedEmail = emailRecordService.updateEmail(id, emailDetails);
        return ResponseEntity.ok(updatedEmail);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @DeleteMapping("/mailrecord/{id}")
    public ResponseEntity<Void> deleteEmail(@PathVariable String id) {
        emailRecordService.deleteEmail(id);
        return ResponseEntity.noContent().build();
    }


}