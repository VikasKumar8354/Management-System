package com.example.Management.Library.service;


import com.example.Management.Library.dto.DocumentRequestDTO;
import com.example.Management.Library.dto.DocumentResponseDTO;
import com.example.Management.Library.enums.Department;
import com.example.Management.Library.enums.DocumentCategory;
import com.example.Management.Library.model.DocumentBook;
import com.example.Management.Library.repository.DocumentBookRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DocumentBookService {

    @Autowired
    private DocumentBookRepository documentRepository;

    private DocumentResponseDTO toResponseDTO(DocumentBook document) {
        DocumentResponseDTO dto = new DocumentResponseDTO();
        dto.setId(document.getId());
        dto.setTitle(document.getTitle());
        dto.setDepartment(String.valueOf(document.getDepartment()));
        dto.setCategories(String.valueOf(document.getCategories()));
        dto.setPublicationDate(document.getPublicationDate());
        dto.setTags(document.getTags());
        dto.setActiveVersion(document.isActiveVersion());
        dto.setDownloadLink("/api/documents/download/" + document.getId());
        return dto;
    }

    public DocumentResponseDTO uploadDocument(DocumentRequestDTO request, MultipartFile file) throws IOException {
        DocumentBook document = new DocumentBook();
        document.setTitle(request.getTitle());
        document.setDepartment(Department.valueOf(request.getDepartment()));
        document.setCategories(DocumentCategory.valueOf(request.getCategory()));
        document.setPublicationDate(request.getPublicationDate());

        // Normalize tags to lowercase
        List<String> normalizedTags = request.getTags().stream()
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(tag -> !tag.isEmpty())
                .collect(Collectors.toList());
        document.setTags(normalizedTags);

        document.setFileName(file.getOriginalFilename());
        document.setFileType(file.getContentType());
        document.setFileData(file.getBytes()); // ✅ Store bytes in DB

        document.setCreatedBy("System");
        document.setCreatedOn(new Date());

        document = documentRepository.save(document);
        return toResponseDTO(document);
    }

    public DocumentResponseDTO getDocumentById(Long id) {
        DocumentBook document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        return toResponseDTO(document);
    }

    public void deleteDocument(Long id) {
        documentRepository.deleteById(id);
    }

    public Page<DocumentResponseDTO> searchDocuments(String query, Pageable pageable) {
        Page<DocumentBook> documents = documentRepository
                .findByTitleContainingIgnoreCaseOrDepartmentContainingIgnoreCase(query, query, pageable);
        return documents.map(this::toResponseDTO);
    }

    public Page<DocumentResponseDTO> filterDocuments(
            Department department,
            DocumentCategory categories,
            String title,
            Date startDate,
            Date endDate,
            Date publicationStartDate,
            Date publicationEndDate,
            List<String> tags,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdOn").descending());

        Specification<DocumentBook> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Department filter
            if (department != null) {
                predicates.add(cb.equal(root.get("department"), department));
            }

            // Category filter
            if (categories != null) {
                predicates.add(cb.equal(root.get("categories"), categories));
            }

            // Title filter
            if (title != null && !title.trim().isEmpty()) {
                String cleanedTitle = title.trim().toLowerCase().replaceAll("[^a-z0-9]", "");
                Expression<String> dbTitle = cb.lower(root.get("title"));
                dbTitle = cb.function("REPLACE", String.class, dbTitle, cb.literal("-"), cb.literal(""));
                dbTitle = cb.function("REPLACE", String.class, dbTitle, cb.literal(" "), cb.literal(""));
                dbTitle = cb.function("REPLACE", String.class, dbTitle, cb.literal("_"), cb.literal(""));
                predicates.add(cb.like(dbTitle, "%" + cleanedTitle + "%"));
            }

            // CreatedOn date filters (inclusive)
            if (startDate != null && endDate != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(endDate);
                cal.add(Calendar.DAY_OF_MONTH, 1);
                Date inclusiveEndDate = cal.getTime();

                predicates.add(cb.greaterThanOrEqualTo(root.get("createdOn"), startDate));
                predicates.add(cb.lessThan(root.get("createdOn"), inclusiveEndDate));
            } else if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdOn"), startDate));
            } else if (endDate != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(endDate);
                cal.add(Calendar.DAY_OF_MONTH, 1);
                Date inclusiveEndDate = cal.getTime();
                predicates.add(cb.lessThan(root.get("createdOn"), inclusiveEndDate));
            }

            // ✅ Publication Date filters (inclusive)
            if (publicationStartDate != null && publicationEndDate != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(publicationEndDate);
                cal.add(Calendar.DAY_OF_MONTH, 1);
                Date inclusivePubEndDate = cal.getTime();

                predicates.add(cb.greaterThanOrEqualTo(root.get("publicationDate"), publicationStartDate));
                predicates.add(cb.lessThan(root.get("publicationDate"), inclusivePubEndDate));
            } else if (publicationStartDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("publicationDate"), publicationStartDate));
            } else if (publicationEndDate != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(publicationEndDate);
                cal.add(Calendar.DAY_OF_MONTH, 1);
                Date inclusivePubEndDate = cal.getTime();
                predicates.add(cb.lessThan(root.get("publicationDate"), inclusivePubEndDate));
            }

            // Tags filter
            if (tags != null && !tags.isEmpty()) {
                List<Predicate> tagPredicates = new ArrayList<>();
                Join<DocumentBook, String> tagJoin = root.join("tags");

                for (String tag : tags) {
                    if (tag != null && !tag.trim().isEmpty()) {
                        String cleanedTag = tag.trim().toLowerCase().replaceAll("[^a-z0-9]", "");

                        Expression<String> dbTag = cb.lower(tagJoin);
                        dbTag = cb.function("REPLACE", String.class, dbTag, cb.literal("-"), cb.literal(""));
                        dbTag = cb.function("REPLACE", String.class, dbTag, cb.literal(" "), cb.literal(""));

                        tagPredicates.add(cb.like(dbTag, "%" + cleanedTag + "%"));
                    }
                }

                if (!tagPredicates.isEmpty()) {
                    predicates.add(cb.or(tagPredicates.toArray(new Predicate[0])));
                    query.distinct(true);
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<DocumentBook> filteredDocs = documentRepository.findAll(spec, pageable);

        List<DocumentResponseDTO> dtoList = filteredDocs.getContent().stream()
                .distinct()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, filteredDocs.getTotalElements());
    }




    public Optional<DocumentBook> getDocumentForDownload(Long id) {
        return documentRepository.findById(id);
    }

    public Page<DocumentResponseDTO> getDocumentsPaginated(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<DocumentBook> documentsPage = documentRepository.findAll(pageable);

        List<DocumentResponseDTO> dtoList = documentsPage.getContent()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, documentsPage.getTotalElements());
    }

    public Page<DocumentResponseDTO> searchDocumentsByTags(List<String> tags, Pageable pageable) {
        Page<DocumentBook> documents = documentRepository.findByTagsIn(tags, pageable);
        return documents.map(this::toResponseDTO);
    }

    public DocumentResponseDTO updateDocument(
            Long id,
            String title,
            String department,
            String category,
            Date publicationDate,
            List<String> tags,
            MultipartFile file,
            String updatedBy) throws IOException {

        DocumentBook document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found with ID: " + id));

        // ✅ Only update if parameter is provided
        if (title != null && !title.trim().isEmpty()) {
            document.setTitle(title);
        }

        if (department != null && !department.trim().isEmpty()) {
            try {
                document.setDepartment(Department.valueOf(department.toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid department value: " + department);
            }
        }

        if (category != null && !category.trim().isEmpty()) {
            try {
                document.setCategories(DocumentCategory.valueOf(category.toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid category value: " + category);
            }
        }

        if (publicationDate != null) {
            document.setPublicationDate(publicationDate);
        }

        if (tags != null && !tags.isEmpty()) {
            document.setTags(tags);
        }

        if (file != null && !file.isEmpty()) {
            document.setFileName(file.getOriginalFilename());
            document.setFileType(file.getContentType());
            document.setFileData(file.getBytes());
        }

        // ✅ Update tracking fields only
        document.setUpdatedBy(updatedBy);
        document.setUpdatedOn(new Date());

        document = documentRepository.save(document);

        return toResponseDTO(document);
    }

    public DocumentResponseDTO updateFileOnly(Long id, MultipartFile file) throws IOException {
        DocumentBook document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found with ID: " + id));

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("No file provided for update");
        }

        // ✅ Debug: print file details
        System.out.println("Received file:");
        System.out.println("Original filename: " + file.getOriginalFilename());
        System.out.println("File size (bytes): " + file.getSize());
        System.out.println("Content type: " + file.getContentType());

        // Update only file data
        document.setFileData(file.getBytes());

        // Log the update time
        document.setUpdatedOn(new Date());

        // Debug: confirm saved file size
        System.out.println("Saving file of size: " + document.getFileData().length);

        document = documentRepository.save(document);
        documentRepository.flush(); // Force DB update

        return toResponseDTO(document);
    }

    public List<DocumentResponseDTO> getAllDocuments() {
        return documentRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

}


