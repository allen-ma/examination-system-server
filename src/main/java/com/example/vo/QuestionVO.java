package com.example.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 题目出参 VO
 */
@Data
public class QuestionVO {

    private Long id;
    private String type;
    private String content;
    private String options;     // 给学生时展示选项
    private String answer;      // 教师看时包含，学生看时可为null（按需控制）
    private String analysis;
    private Integer difficulty;
    private String tags;
    private Long createBy;
    private LocalDateTime createTime;
}
