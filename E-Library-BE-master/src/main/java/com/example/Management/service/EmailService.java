package com.example.Management.service;


import com.example.Management.entity.Email;
import com.example.Management.entity.EmailTemplate;
import com.example.Management.entity.Employee;
import com.example.Management.entity.IssuedDoc;
import com.example.Management.repository.EmailRepository;
import com.example.Management.repository.EmailTemplateRepository;
import com.example.Management.repository.EmployeeRepository;
import com.example.Management.repository.IssuedDocRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.activation.DataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class EmailService {

    private final EmailTemplateRepository emailTemplateRepository;
    private final EmailRepository emailRepository;
    private final JavaMailSender mailSender;
    @Autowired
    private IssuedDocRepository issuedDocRepository;
    @Autowired
    private IssuedDocService issuedDocService;

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private EmployeeRepository employeeRepository;

    public String sendEmail(Email request) {
        System.out.println("send email tak aarha hai .....!!!!");
        Optional<EmailTemplate> optionalTemplate = emailTemplateRepository.findByTemplateName(request.getTemplateName());

        if (optionalTemplate.isEmpty()) {
            return "Error: Email template not found for templateName: " + request.getTemplateName();
        }
        System.out.println("template mil rha rha hai .....!!!!");
        String subject = optionalTemplate.get().getSubject();

        try {
            byte[] pdfAttachment = generatePdf(request.getEmailBody());
            sendEmailWithAttachment(request.getRecipientEmail(), subject, request.getEmailBody(), pdfAttachment);

            // Check if employee exists by email
            Optional<Employee> optionalEmployee = employeeRepository.findByEmail(request.getRecipientEmail());
            Employee employee;

            if (optionalEmployee.isPresent()) {
                // Employee exists
                employee = optionalEmployee.get();
                System.out.println("Existing employee found: " + employee.getEmployeeName());
            } else {
                // Employee does not exist, create new
                employee = new Employee();
                employee.setId(employeeService.generateEmployeeId());
                employee.setEmployeeName(request.getEmployeeName());
                employee.setEmail(request.getRecipientEmail());
                employee.setRole("NOT UPDATED");
                employee = employeeRepository.save(employee);
                System.out.println("New employee created: " + employee.getEmployeeName());
            }

            // Save the email record
            Email savedEmail = new Email();
            savedEmail.setId(generateMailRecordId());


            savedEmail.setDateTime(LocalDateTime.now());


            savedEmail.setRecipientEmail(request.getRecipientEmail());


            savedEmail.setEmployeeName(request.getEmployeeName());


            savedEmail.setEmailBody(request.getEmailBody());


            savedEmail.setLettertype(request.getLettertype());

            savedEmail.setTemplateName(request.getTemplateName());
            emailRepository.save(savedEmail);

            // Save issued document
            IssuedDoc issuedDoc = new IssuedDoc();
            issuedDoc.setId(issuedDocService.generateIssuedDocId());
            issuedDoc.setDoc(pdfAttachment);
            issuedDoc.setEmployeeName(employee.getEmployeeName());
            issuedDoc.setEmpEmail(employee.getEmail());
            issuedDoc.setDateOfIssue(LocalDate.now());
            issuedDoc.setTypeOfDoc(request.getLettertype());
            issuedDoc.setEmployeeId(employee.getId()); // ✅ Added employeeId to IssuedDoc
            issuedDocRepository.save(issuedDoc);

            return "Email with PDF sent successfully to " + request.getRecipientEmail();
        } catch (Exception e) {

            return "Error sending email: " + e.getMessage();
        }
    }
    private String generateMailRecordId() {
        LocalDate today = LocalDate.now();
        int year = today.getYear(); // 2025
        String month = today.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toUpperCase(); // APR
        String middle = "0DOC";
        String prefix = year + month + middle; // e.g., 2025APR0DOC

        List<Email> emails = emailRepository.findAll(); // Can be optimized
        int maxId = 0;

        for (Email email : emails) {
            String emailId = email.getId();
            if (emailId != null && emailId.startsWith(prefix)) {
                try {
                    int currentId = Integer.parseInt(emailId.substring(prefix.length()));
                    if (currentId > maxId) {
                        maxId = currentId;
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Invalid email ID format: " + emailId);
                }
            }
        }

        int newIdNumber = maxId + 1;
        String numberPart = String.format("%04d", newIdNumber); // Pads with leading zeros to 4 digits

        return prefix + numberPart; // e.g., 2025APR0DOC0001
    }


    private byte[] generatePdf(String content) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        String processedContent = content.replaceAll("(?i)<br\\s*/?>", "\n");

        Font font = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
        Paragraph paragraph = new Paragraph(processedContent, font);
        document.add(paragraph);

        document.close();
        System.out.println("pdf genertaate ho rha hai .....!!!!");
        return outputStream.toByteArray();
    }

    private void sendEmailWithAttachment(String recipient, String subject, String body, byte[] pdfAttachment) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        System.out.println("helper tak aa rha hai .....!!!!");
        helper.setFrom("rohann33445@gmail.com");
        helper.setTo(recipient);
        helper.setSubject(subject);
        helper.setText(body, true); // true = HTML
        System.out.println("helper set ho rha hai .....!!!!");
        helper.addAttachment("Email_Content.pdf", new DataSource() {
            @Override
            public InputStream getInputStream() {
                return new ByteArrayInputStream(pdfAttachment);
            }

            @Override
            public java.io.OutputStream getOutputStream() {
                throw new UnsupportedOperationException("Not supported");
            }

            @Override
            public String getContentType() {
                return "application/pdf";
            }

            @Override
            public String getName() {
                return "Email_Content.pdf";
            }
        });


        mailSender.send(message);
    }

}