package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.UserContext;
import com.example.dto.ExamDTO;
import com.example.entity.Question;
import com.example.entity.Exam;
import com.example.entity.ExamQuestion;
import com.example.entity.ExamRule;
import com.example.mapper.ExamMapper;
import com.example.mapper.ExamQuestionMapper;
import com.example.mapper.ExamRuleMapper;
import com.example.mapper.QuestionMapper;
import com.example.mapper.UserMapper;
import com.example.entity.User;
import com.example.service.ExamService;
import com.example.vo.ExamPaperVO;
import com.example.vo.ExamVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExamServiceImpl implements ExamService {

    private final ExamMapper examMapper;
    private final ExamRuleMapper examRuleMapper;
    private final ExamQuestionMapper examQuestionMapper;
    private final QuestionMapper questionMapper;
    private final UserMapper userMapper;

    public ExamServiceImpl(ExamMapper examMapper, ExamRuleMapper examRuleMapper,
                           ExamQuestionMapper examQuestionMapper, QuestionMapper questionMapper,
                           UserMapper userMapper) {
        this.examMapper = examMapper;
        this.examRuleMapper = examRuleMapper;
        this.examQuestionMapper = examQuestionMapper;
        this.questionMapper = questionMapper;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public Long create(ExamDTO dto) {
        // 1. 创建考试记录
        Exam exam = new Exam();
        exam.setTitle(dto.getTitle());
        exam.setDescription(dto.getDescription());
        exam.setStartTime(dto.getStartTime());
        exam.setEndTime(dto.getEndTime());
        exam.setDurationMinutes(dto.getDurationMinutes());
        exam.setStatus("draft");
        exam.setCreateBy(UserContext.getUserId());
        exam.setTotalScore(0);
        examMapper.insert(exam);

        // 2. 保存组卷规则并随机抽题
        int totalScore = 0;
        int sortOrder = 1;

        for (ExamDTO.RuleDTO ruleDTO : dto.getRules()) {
            // 保存规则
            ExamRule rule = new ExamRule();
            rule.setExamId(exam.getId());
            rule.setQuestionType(ruleDTO.getQuestionType());
            rule.setDifficulty(ruleDTO.getDifficulty());
            rule.setCount(ruleDTO.getCount());
            rule.setScorePerQuestion(ruleDTO.getScorePerQuestion());
            examRuleMapper.insert(rule);

            // 随机抽题
            LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Question::getType, ruleDTO.getQuestionType());
            if (ruleDTO.getDifficulty() != null) {
                wrapper.eq(Question::getDifficulty, ruleDTO.getDifficulty());
            }
            wrapper.last("ORDER BY RAND() LIMIT " + ruleDTO.getCount());
            List<Question> questions = questionMapper.selectList(wrapper);

            if (questions.size() < ruleDTO.getCount()) {
                throw new RuntimeException("题库中" + ruleDTO.getQuestionType() + "类型的题目数量不足");
            }

            // 保存考试题目关联
            for (Question q : questions) {
                ExamQuestion eq = new ExamQuestion();
                eq.setExamId(exam.getId());
                eq.setQuestionId(q.getId());
                eq.setScore(ruleDTO.getScorePerQuestion());
                eq.setSortOrder(sortOrder++);
                examQuestionMapper.insert(eq);
            }

            totalScore += questions.size() * ruleDTO.getScorePerQuestion();
        }

        // 3. 更新总分
        exam.setTotalScore(totalScore);
        examMapper.updateById(exam);

        return exam.getId();
    }

    @Override
    public Page<ExamVO> pageByTeacher(Integer current, Integer size, String title) {
        Page<Exam> page = new Page<>(current, size);
        LambdaQueryWrapper<Exam> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(title != null && !title.isEmpty(), Exam::getTitle, title)
               .eq(Exam::getCreateBy, UserContext.getUserId())
               .orderByDesc(Exam::getCreateTime);
        Page<Exam> examPage = examMapper.selectPage(page, wrapper);

        Page<ExamVO> voPage = new Page<>(examPage.getCurrent(), examPage.getSize(), examPage.getTotal());
        voPage.setRecords(examPage.getRecords().stream().map(this::toVO).toList());
        return voPage;
    }

    @Override
    public ExamVO getById(Long id) {
        Exam exam = examMapper.selectById(id);
        if (exam == null) {
            throw new RuntimeException("考试不存在");
        }
        return toVO(exam);
    }

    @Override
    public void publish(Long id) {
        Exam exam = examMapper.selectById(id);
        if (exam == null) {
            throw new RuntimeException("考试不存在");
        }
        if (!"draft".equals(exam.getStatus())) {
            throw new RuntimeException("只有草稿状态的考试可以发布");
        }
        exam.setStatus("published");
        examMapper.updateById(exam);
    }

    @Override
    public void delete(Long id) {
        examMapper.deleteById(id);
    }

    @Override
    public ExamPaperVO getPaper(Long examId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new RuntimeException("考试不存在");
        }

        ExamPaperVO paper = new ExamPaperVO();
        paper.setExamId(examId);
        paper.setTitle(exam.getTitle());
        paper.setDurationMinutes(exam.getDurationMinutes());
        paper.setTotalScore(exam.getTotalScore());

        // 获取考试题目列表
        LambdaQueryWrapper<ExamQuestion> eqWrapper = new LambdaQueryWrapper<>();
        eqWrapper.eq(ExamQuestion::getExamId, examId)
                 .orderByAsc(ExamQuestion::getSortOrder);
        List<ExamQuestion> examQuestions = examQuestionMapper.selectList(eqWrapper);

        List<ExamPaperVO.QuestionItem> items = new ArrayList<>();
        for (ExamQuestion eq : examQuestions) {
            Question q = questionMapper.selectById(eq.getQuestionId());
            if (q != null) {
                ExamPaperVO.QuestionItem item = new ExamPaperVO.QuestionItem();
                item.setQuestionId(q.getId());
                item.setType(q.getType());
                item.setContent(q.getContent());
                item.setOptions(q.getOptions());
                item.setScore(eq.getScore());
                item.setSortOrder(eq.getSortOrder());
                items.add(item);
            }
        }

        paper.setQuestions(items);
        return paper;
    }

    private ExamVO toVO(Exam exam) {
        ExamVO vo = new ExamVO();
        BeanUtils.copyProperties(exam, vo);
        // 获取教师姓名
        User teacher = userMapper.selectById(exam.getCreateBy());
        if (teacher != null) {
            vo.setTeacherName(teacher.getRealName());
        }
        return vo;
    }
}
