package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 组卷规则实体（不继承 BaseEntity，无自动填充字段）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("exam_rule")
public class ExamRule {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long examId;        // 考试ID
    private String questionType;// 题目类型
    private Integer difficulty; // 难度
    private Integer count;      // 题目数量
    private Integer scorePerQuestion; // 每题分值
}
