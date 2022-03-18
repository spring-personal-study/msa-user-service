package com.example.msauserservice.model;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
@Table(name="users")
public class UserEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 50, unique = true)
    private String userId;

    @Column(nullable = false, length = 50, unique = true)
    private String encryptedPwd;

}
