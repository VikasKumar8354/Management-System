package com.example.Management.repository;

import com.example.Management.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
//    void saveAll(List<Post> list);
    // This interface will automatically provide CRUD operations for Post entity
    // No additional methods are needed unless custom queries are required
}
