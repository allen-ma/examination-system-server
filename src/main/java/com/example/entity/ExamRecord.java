package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 考试记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName("exam_record")
public class ExamRecord extends BaseEntity {

    private Long examId;
    private Long studentId;
    private LocalDateTime startTime;    // 开始时间
    private LocalDateTime submitTime;   // 提交时间
    private String status;              // in_progress/submitted/graded
    private Integer totalScore;         // 得分
}
