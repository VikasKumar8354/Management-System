package com.example.Management.SaveSearch;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaveSearchRepository extends JpaRepository<SaveSearchModel, Long> {
    List<SaveSearchModel> findByUserId(Long userId);
}
