package com.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dto.ExamDTO;
import com.example.vo.ExamPaperVO;
import com.example.vo.ExamVO;

public interface ExamService {

    Long create(ExamDTO dto);

    Page<ExamVO> pageByTeacher(Integer current, Integer size, String title);

    ExamVO getById(Long id);

    void publish(Long id);

    void delete(Long id);

    ExamPaperVO getPaper(Long examId);
}
