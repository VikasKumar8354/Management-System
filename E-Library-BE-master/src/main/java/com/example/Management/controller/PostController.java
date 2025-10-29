package com.example.Management.controller;

import com.example.Management.entity.UserExternal;
import com.example.Management.entity.Post;
import com.example.Management.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PostRepository postRepository;

    //token based authentication
    private String getAuthToken() {
        String authUrl = "http://localhost:8080/login";

        // Replace with real credentials
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "user");
        credentials.put("password", "user123");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(credentials, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                authUrl,
                HttpMethod.POST,
                request,
                Map.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            // Adjust key if token is inside nested object like data.token
            return (String) response.getBody().get("token");
        } else {
            throw new RuntimeException("Failed to get token from App B");
        }
    }

    // Step 2: Use token to call another secured API
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/all")
    public String getTasksFromAppB() {
        String token = getAuthToken();
        String url = "http://localhost:8080/getalltasks";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                String.class
        );

        return response.getBody();
    }

//    private static final String POSTS_API_URL = "https://jsonplaceholder.typicode.com/posts";

//    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
//    @GetMapping("/{id}")
//    public String getPosts(@PathVariable int id) {
//        String response = restTemplate.getForObject("http://localhost:8080/user/2", String.class);
//        return response;
//    }

//    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
//    @GetMapping("/all")
//   public String getAllUsers(){
//        String url = "http://localhost:8080/getallusers";
//        String token = getAuthToken();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth(token);
//        HttpEntity<Void> request = new HttpEntity<>(headers);
//        ResponseEntity<String> response = restTemplate.exchange(
//                url,
//                HttpMethod.GET,
//                request,
//                String.class
//        );
//        return response.getBody();
//    }


//    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
//    @PostMapping("/create")
//    public ResponseEntity<String> sendData(@RequestBody UserExternal userExternal) {
//        String externalApiUrl = "http://localhost:8080/user/create";
//        ResponseEntity<String> response = restTemplate.postForEntity(externalApiUrl, userExternal, String.class);
//        return ResponseEntity.ok(response.getBody());
//    }


}
