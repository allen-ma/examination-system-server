package com.example.vo;

import lombok.Data;

import java.util.List;

/**
 * 试卷详情 VO（含题目列表，给学生答题用，不含答案）
 */
@Data
public class ExamPaperVO {

    private Long examId;
    private String title;
    private Integer durationMinutes;
    private Integer totalScore;

    private List<QuestionItem> questions;

    @Data
    public static class QuestionItem {
        private Long questionId;
        private String type;
        private String content;
        private String options;
        private Integer score;
        private Integer sortOrder;
    }
}
