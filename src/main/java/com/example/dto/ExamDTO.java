package com.example.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 考试创建入参 DTO（含组卷规则）
 */
@Data
public class ExamDTO {

    @NotBlank(message = "考试标题不能为空")
    private String title;

    private String description;

    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    @NotNull(message = "考试时长不能为空")
    private Integer durationMinutes;

    @NotEmpty(message = "组卷规则不能为空")
    @Valid
    private List<RuleDTO> rules;

    @Data
    public static class RuleDTO {
        @NotBlank(message = "题目类型不能为空")
        private String questionType;

        private Integer difficulty; // NULL表示不限

        @NotNull(message = "题目数量不能为空")
        private Integer count;

        @NotNull(message = "每题分值不能为空")
        private Integer scorePerQuestion;
    }
}
