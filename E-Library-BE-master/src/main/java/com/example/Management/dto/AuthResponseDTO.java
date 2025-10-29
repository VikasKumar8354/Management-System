package com.example.Management.dto;

import lombok.Data;

@Data
public class AuthResponseDTO {
    private String token;
    private String username;
    private String role;

    public AuthResponseDTO(String token,String username, String role) {
        this.token = token;
        this.username= username;
        this.role = role;
    }

}
