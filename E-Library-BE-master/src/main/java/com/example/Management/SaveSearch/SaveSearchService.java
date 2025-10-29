package com.example.Management.SaveSearch;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SaveSearchService {

    @Autowired
    private SaveSearchRepository repository;

    // Create or Update
    public SaveSearchModel saveSearch(SaveSearchModel search) {
        return repository.save(search);
    }

    // Read All
    public List<SaveSearchModel> getAllSearches() {
        return repository.findAll();
    }

    // Read by ID
    public Optional<SaveSearchModel> getSearchById(Long id) {
        return repository.findById(id);
    }

    // Read by User ID
    public List<SaveSearchModel> getSearchesByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

    // Delete
    public void deleteSearch(Long id) {
        repository.deleteById(id);
    }
}
