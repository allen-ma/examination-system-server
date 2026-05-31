package com.example.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.Result;
import com.example.common.UserContext;
import com.example.dto.ExamDTO;
import com.example.dto.SubmitExamDTO;
import com.example.service.ExamRecordService;
import com.example.service.ExamService;
import com.example.vo.ExamPaperVO;
import com.example.vo.ExamResultVO;
import com.example.vo.ExamVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 考试管理 Controller（教师出卷、学生答题）
 */
@RestController
@RequestMapping("/api/v1/exams")
public class ExamController {

    private final ExamService examService;
    private final ExamRecordService examRecordService;

    public ExamController(ExamService examService, ExamRecordService examRecordService) {
        this.examService = examService;
        this.examRecordService = examRecordService;
    }

    // ==================== 教师端 ====================

    /**
     * 创建考试（含组卷规则，自动随机抽题）
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody ExamDTO dto) {
        return Result.success(examService.create(dto));
    }

    /**
     * 分页查询教师创建的考试
     */
    @GetMapping
    public Result<Page<ExamVO>> pageByTeacher(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String title) {
        return Result.success(examService.pageByTeacher(current, size, title));
    }

    /**
     * 考试详情
     */
    @GetMapping("/{id}")
    public Result<ExamVO> getById(@PathVariable Long id) {
        return Result.success(examService.getById(id));
    }

    /**
     * 发布考试
     */
    @PutMapping("/{id}/publish")
    public Result<Void> publish(@PathVariable Long id) {
        examService.publish(id);
        return Result.success();
    }

    /**
     * 删除考试
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        examService.delete(id);
        return Result.success();
    }

    // ==================== 学生端 ====================

    /**
     * 获取可参加的考试列表
     */
    @GetMapping("/available")
    public Result<List<ExamVO>> getAvailable() {
        return Result.success(examRecordService.getAvailableExams(UserContext.getUserId()));
    }

    /**
     * 开始考试
     */
    @PostMapping("/{id}/start")
    public Result<Long> start(@PathVariable Long id) {
        return Result.success(examRecordService.startExam(id, UserContext.getUserId()));
    }

    /**
     * 获取试卷（不含答案）
     */
    @GetMapping("/{id}/paper")
    public Result<ExamPaperVO> getPaper(@PathVariable Long id) {
        return Result.success(examService.getPaper(id));
    }

    /**
     * 提交答卷
     */
    @PostMapping("/{id}/submit")
    public Result<Void> submit(@PathVariable Long id, @Valid @RequestBody SubmitExamDTO dto) {
        examRecordService.submitExam(id, UserContext.getUserId(), dto);
        return Result.success();
    }

    /**
     * 查看成绩
     */
    @GetMapping("/{id}/result")
    public Result<ExamResultVO> getResult(@PathVariable Long id) {
        return Result.success(examRecordService.getResult(id, UserContext.getUserId()));
    }

    /**
     * 我的考试记录
     */
    @GetMapping("/my-records")
    public Result<Page<ExamResultVO>> myRecords(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(examRecordService.getRecordsByStudent(UserContext.getUserId(), current, size));
    }
}
