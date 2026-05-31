package com.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dto.SubmitExamDTO;
import com.example.vo.ExamResultVO;
import com.example.vo.ExamVO;

import java.util.List;

public interface ExamRecordService {

    List<ExamVO> getAvailableExams(Long studentId);

    Long startExam(Long examId, Long studentId);

    void submitExam(Long examId, Long studentId, SubmitExamDTO dto);

    ExamResultVO getResult(Long examId, Long studentId);

    Page<ExamResultVO> getRecordsByStudent(Long studentId, Integer current, Integer size);
}
