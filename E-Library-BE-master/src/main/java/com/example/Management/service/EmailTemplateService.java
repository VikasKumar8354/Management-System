package com.example.Management.service;

import com.example.Management.entity.EmailTemplate;
import com.example.Management.repository.EmailTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailTemplateService {

    private final EmailTemplateRepository emailTemplateRepository;

    // ✅ ADD NEW TEMPLATE
    public String addTemplate(EmailTemplate template) {
        emailTemplateRepository.save(template);
        return "Template '" + template.getTemplateName() + "' added successfully!";
    }

    // ✅ GET TEMPLATE BY NAME
    public EmailTemplate getTemplateByName(String templateName) {
        return emailTemplateRepository.findByTemplateName(templateName)
                .orElseThrow(() -> new RuntimeException("Template not found!"));
    }

    // ✅ GET ALL TEMPLATES
    public List<EmailTemplate> getAllTemplates() {
        return emailTemplateRepository.findAll();
    }

    // ✅ UPDATE TEMPLATE
    public String updateTemplate(Long id, EmailTemplate updatedTemplate) {
        Optional<EmailTemplate> existingTemplate = emailTemplateRepository.findById(id);

        if (existingTemplate.isPresent()) {
            EmailTemplate template = existingTemplate.get();
            template.setTemplateName(updatedTemplate.getTemplateName());
            template.setTemplateType(updatedTemplate.getTemplateType());
            template.setSubject(updatedTemplate.getSubject());
            template.setBody(updatedTemplate.getBody());
            emailTemplateRepository.save(template);
            return "Template '" + updatedTemplate.getTemplateName() + "' updated successfully!";
        }
        return "Template not found!";
    }
    public List<EmailTemplate> getTemplateByType(String type) {
        return emailTemplateRepository.findByTemplateType(type);
    }

    // ✅ DELETE TEMPLATE
    public String deleteTemplate(Long id) {
        if (emailTemplateRepository.existsById(id)) {
            emailTemplateRepository.deleteById(id);
            return "Template deleted successfully!";
        }
        return "Template not found!";
    }
}