package com.example.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 考试结果 VO（含得分详情）
 */
@Data
public class ExamResultVO {

    private Long recordId;
    private Long examId;
    private String examTitle;
    private LocalDateTime startTime;
    private LocalDateTime submitTime;
    private String status;
    private Integer totalScore;    // 得分
    private Integer examTotalScore; // 试卷总分

    private List<AnswerDetail> answers;

    @Data
    public static class AnswerDetail {
        private Long questionId;
        private String questionContent;
        private String type;
        private String studentAnswer;
        private String correctAnswer;
        private Integer isCorrect;
        private Integer score;
    }
}
