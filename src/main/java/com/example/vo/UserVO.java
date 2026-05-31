package com.example.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserVO {

    private Long id;

    private String username;

    private String realName;

    private String role;

    private String email;

    private String phone;

    private Integer status;

    private LocalDateTime createTime;
}