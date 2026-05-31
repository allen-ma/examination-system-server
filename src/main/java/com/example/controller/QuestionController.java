package com.example.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.Result;
import com.example.common.UserContext;
import com.example.dto.QuestionDTO;
import com.example.service.QuestionService;
import com.example.vo.QuestionVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 题目管理 Controller（教师使用）
 */
@RestController
@RequestMapping("/api/v1/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    /**
     * 创建题目
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody QuestionDTO dto) {
        return Result.success(questionService.create(dto));
    }

    /**
     * 分页查询题目（教师查看自己的题目）
     */
    @GetMapping
    public Result<Page<QuestionVO>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer difficulty) {
        return Result.success(questionService.page(current, size, type, difficulty));
    }

    /**
     * 题目详情（含答案，教师用）
     */
    @GetMapping("/{id}")
    public Result<QuestionVO> getById(@PathVariable Long id) {
        return Result.success(questionService.getById(id));
    }

    /**
     * 修改题目
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody QuestionDTO dto) {
        questionService.update(id, dto);
        return Result.success();
    }

    /**
     * 删除题目
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        questionService.delete(id);
        return Result.success();
    }
}
