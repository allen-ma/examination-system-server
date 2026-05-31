package com.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dto.QuestionDTO;
import com.example.vo.QuestionVO;

public interface QuestionService {

    Long create(QuestionDTO dto);

    Page<QuestionVO> page(Integer current, Integer size, String type, Integer difficulty);

    QuestionVO getById(Long id);

    QuestionVO getQuestionForStudent(Long id);

    void update(Long id, QuestionDTO dto);

    void delete(Long id);
}
