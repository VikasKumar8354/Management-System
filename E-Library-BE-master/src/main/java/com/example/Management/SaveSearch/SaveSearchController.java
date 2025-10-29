package com.example.Management.SaveSearch;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saveSearch")
public class SaveSearchController {

    @Autowired
    private SaveSearchService service;

    // Create or Update
    @PostMapping
    public SaveSearchModel createSearch(@RequestBody SaveSearchModel search) {
        return service.saveSearch(search);
    }

    // Get all searches
    @GetMapping
    public List<SaveSearchModel> getAllSearches() {
        return service.getAllSearches();
    }

    // Get search by ID
    @GetMapping("/{id}")
    public ResponseEntity<SaveSearchModel> getSearchById(@PathVariable Long id) {
        return service.getSearchById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get searches by User ID
    @GetMapping("/user/{userId}")
    public List<SaveSearchModel> getSearchesByUserId(@PathVariable Long userId) {
        return service.getSearchesByUserId(userId);
    }

    // Delete search
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSearch(@PathVariable Long id) {
        service.deleteSearch(id);
        return ResponseEntity.noContent().build();
    }
}
