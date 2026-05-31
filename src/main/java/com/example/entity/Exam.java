package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 考试实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName("exam")
public class Exam extends BaseEntity {

    private String title;           // 考试标题
    private String description;     // 考试描述
    private LocalDateTime startTime;// 开始时间
    private LocalDateTime endTime;  // 结束时间
    private Integer durationMinutes;// 时长(分钟)
    private Integer totalScore;     // 总分
    private String status;          // draft/published/closed
    private Long createBy;          // 创建教师ID
}
