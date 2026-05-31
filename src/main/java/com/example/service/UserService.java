package com.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dto.UserDTO;
import com.example.vo.UserVO;

public interface UserService {

    Page<UserVO> page(Integer current, Integer size, String username);

    Long create(UserDTO dto);

    void update(Long id, UserDTO dto);

    void delete(Long id);

    UserVO getById(Long id);
}