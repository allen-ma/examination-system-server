package com.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 题目入参 DTO
 */
@Data
public class QuestionDTO {

    @NotBlank(message = "题目类型不能为空")
    private String type;

    @NotBlank(message = "题干内容不能为空")
    private String content;

    @NotBlank(message = "选项不能为空")
    private String options;

    @NotBlank(message = "正确答案不能为空")
    private String answer;

    private String analysis;

    @NotNull(message = "难度不能为空")
    private Integer difficulty;

    private String tags;
}
