package com.example.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 提交答卷入参 DTO
 */
@Data
public class SubmitExamDTO {

    @NotEmpty(message = "答案列表不能为空")
    @Valid
    private List<AnswerItemDTO> answers;

    @Data
    public static class AnswerItemDTO {
        private Long questionId;
        private String studentAnswer;
    }
}
