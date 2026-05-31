package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.UserContext;
import com.example.dto.QuestionDTO;
import com.example.entity.Question;
import com.example.mapper.QuestionMapper;
import com.example.service.QuestionService;
import com.example.vo.QuestionVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionMapper questionMapper;

    public QuestionServiceImpl(QuestionMapper questionMapper) {
        this.questionMapper = questionMapper;
    }

    @Override
    public Long create(QuestionDTO dto) {
        Question question = new Question();
        BeanUtils.copyProperties(dto, question);
        question.setCreateBy(UserContext.getUserId());
        questionMapper.insert(question);
        return question.getId();
    }

    @Override
    public Page<QuestionVO> page(Integer current, Integer size, String type, Integer difficulty) {
        Page<Question> page = new Page<>(current, size);
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(type != null && !type.isEmpty(), Question::getType, type)
               .eq(difficulty != null, Question::getDifficulty, difficulty)
               .eq(Question::getCreateBy, UserContext.getUserId())
               .orderByDesc(Question::getCreateTime);
        Page<Question> questionPage = questionMapper.selectPage(page, wrapper);

        Page<QuestionVO> voPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        voPage.setRecords(questionPage.getRecords().stream().map(this::toVO).toList());
        return voPage;
    }

    @Override
    public QuestionVO getById(Long id) {
        Question question = questionMapper.selectById(id);
        if (question == null) {
            throw new RuntimeException("题目不存在");
        }
        return toVO(question);
    }

    @Override
    public QuestionVO getQuestionForStudent(Long id) {
        QuestionVO vo = getById(id);
        vo.setAnswer(null);
        vo.setAnalysis(null);
        return vo;
    }

    @Override
    public void update(Long id, QuestionDTO dto) {
        Question existing = questionMapper.selectById(id);
        if (existing == null) {
            throw new RuntimeException("题目不存在");
        }
        BeanUtils.copyProperties(dto, existing);
        existing.setId(id);
        questionMapper.updateById(existing);
    }

    @Override
    public void delete(Long id) {
        questionMapper.deleteById(id);
    }

    private QuestionVO toVO(Question q) {
        QuestionVO vo = new QuestionVO();
        BeanUtils.copyProperties(q, vo);
        return vo;
    }
}
