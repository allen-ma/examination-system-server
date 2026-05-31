package com.example.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 考试出参 VO
 */
@Data
public class ExamVO {

    private Long id;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer durationMinutes;
    private Integer totalScore;
    private String status;
    private Long createBy;
    private String teacherName;
    private LocalDateTime createTime;
}
