package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 题目实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName("question")
public class Question extends BaseEntity {

    private String type;        // single/multiple/true_false
    private String content;     // 题干
    private String options;     // JSON: {"A":"...","B":"...","C":"...","D":"..."}
    private String answer;      // 正确答案
    private String analysis;    // 解析
    private Integer difficulty; // 1-简单 2-中等 3-困难
    private String tags;        // 标签
    private Long createBy;      // 出题教师ID
}
