package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 学生答题记录实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("exam_answer")
public class ExamAnswer {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long recordId;
    private Long questionId;
    private String studentAnswer;   // 学生答案
    private Integer isCorrect;      // 0-错误 1-正确
    private Integer score;          // 得分
}
