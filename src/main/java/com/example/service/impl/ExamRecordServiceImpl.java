package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dto.SubmitExamDTO;
import com.example.entity.*;
import com.example.mapper.*;
import com.example.service.ExamRecordService;
import com.example.vo.ExamResultVO;
import com.example.vo.ExamVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExamRecordServiceImpl implements ExamRecordService {

    private final ExamMapper examMapper;
    private final ExamRecordMapper examRecordMapper;
    private final ExamQuestionMapper examQuestionMapper;
    private final ExamAnswerMapper examAnswerMapper;
    private final QuestionMapper questionMapper;

    public ExamRecordServiceImpl(ExamMapper examMapper, ExamRecordMapper examRecordMapper,
                                 ExamQuestionMapper examQuestionMapper, ExamAnswerMapper examAnswerMapper,
                                 QuestionMapper questionMapper) {
        this.examMapper = examMapper;
        this.examRecordMapper = examRecordMapper;
        this.examQuestionMapper = examQuestionMapper;
        this.examAnswerMapper = examAnswerMapper;
        this.questionMapper = questionMapper;
    }

    @Override
    public List<ExamVO> getAvailableExams(Long studentId) {
        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<Exam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Exam::getStatus, "published")
               .le(Exam::getStartTime, now)
               .ge(Exam::getEndTime, now)
               .orderByAsc(Exam::getStartTime);
        List<Exam> exams = examMapper.selectList(wrapper);
        return exams.stream().map(this::toVO).toList();
    }

    @Override
    @Transactional
    public Long startExam(Long examId, Long studentId) {
        // 检查是否已有记录
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamRecord::getExamId, examId)
               .eq(ExamRecord::getStudentId, studentId);
        ExamRecord existing = examRecordMapper.selectOne(wrapper);
        if (existing != null) {
            if ("submitted".equals(existing.getStatus()) || "graded".equals(existing.getStatus())) {
                throw new RuntimeException("您已经参加过该考试");
            }
            return existing.getId();
        }

        // 检查考试是否存在且已发布
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new RuntimeException("考试不存在");
        }
        if (!"published".equals(exam.getStatus())) {
            throw new RuntimeException("该考试尚未发布");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(exam.getStartTime())) {
            throw new RuntimeException("考试尚未开始");
        }
        if (now.isAfter(exam.getEndTime())) {
            throw new RuntimeException("考试已结束");
        }

        // 创建考试记录
        ExamRecord record = new ExamRecord();
        record.setExamId(examId);
        record.setStudentId(studentId);
        record.setStartTime(now);
        record.setStatus("in_progress");
        examRecordMapper.insert(record);

        return record.getId();
    }

    @Override
    @Transactional
    public void submitExam(Long examId, Long studentId, SubmitExamDTO dto) {
        // 获取考试记录
        LambdaQueryWrapper<ExamRecord> recordWrapper = new LambdaQueryWrapper<>();
        recordWrapper.eq(ExamRecord::getExamId, examId)
                     .eq(ExamRecord::getStudentId, studentId);
        ExamRecord record = examRecordMapper.selectOne(recordWrapper);
        if (record == null) {
            throw new RuntimeException("请先开始考试");
        }
        if ("submitted".equals(record.getStatus()) || "graded".equals(record.getStatus())) {
            throw new RuntimeException("已经提交过答卷");
        }

        // 检查是否超时
        Exam exam = examMapper.selectById(examId);
        if (exam != null && record.getStartTime().plusMinutes(exam.getDurationMinutes()).isBefore(LocalDateTime.now())) {
            // 超时自动提交
        }

        // 获取该考试的题目和正确答案
        LambdaQueryWrapper<ExamQuestion> eqWrapper = new LambdaQueryWrapper<>();
        eqWrapper.eq(ExamQuestion::getExamId, examId);
        List<ExamQuestion> examQuestions = examQuestionMapper.selectList(eqWrapper);

        Map<Long, ExamQuestion> questionMap = examQuestions.stream()
                .collect(Collectors.toMap(ExamQuestion::getQuestionId, q -> q));

        // 将学生答案转为 Map
        Map<Long, String> answerMap = dto.getAnswers().stream()
                .collect(Collectors.toMap(
                        SubmitExamDTO.AnswerItemDTO::getQuestionId,
                        SubmitExamDTO.AnswerItemDTO::getStudentAnswer
                ));

        // 逐题判分
        int totalScore = 0;
        List<ExamAnswer> answers = new ArrayList<>();

        for (ExamQuestion eq : examQuestions) {
            Question question = questionMapper.selectById(eq.getQuestionId());
            if (question == null) continue;

            String studentAns = answerMap.getOrDefault(eq.getQuestionId(), "");
            boolean correct = checkAnswer(question.getType(), question.getAnswer(), studentAns);
            int score = correct ? eq.getScore() : 0;
            totalScore += score;

            ExamAnswer answer = new ExamAnswer();
            answer.setRecordId(record.getId());
            answer.setQuestionId(eq.getQuestionId());
            answer.setStudentAnswer(studentAns);
            answer.setIsCorrect(correct ? 1 : 0);
            answer.setScore(score);
            answers.add(answer);
        }

        // 保存答题记录
        for (ExamAnswer answer : answers) {
            examAnswerMapper.insert(answer);
        }

        // 更新考试记录
        record.setSubmitTime(LocalDateTime.now());
        record.setStatus("graded");
        record.setTotalScore(totalScore);
        examRecordMapper.updateById(record);
    }

    @Override
    public ExamResultVO getResult(Long examId, Long studentId) {
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamRecord::getExamId, examId)
               .eq(ExamRecord::getStudentId, studentId);
        ExamRecord record = examRecordMapper.selectOne(wrapper);
        if (record == null) {
            throw new RuntimeException("暂无考试记录");
        }

        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new RuntimeException("考试不存在");
        }

        ExamResultVO result = new ExamResultVO();
        result.setRecordId(record.getId());
        result.setExamId(examId);
        result.setExamTitle(exam.getTitle());
        result.setStartTime(record.getStartTime());
        result.setSubmitTime(record.getSubmitTime());
        result.setStatus(record.getStatus());
        result.setTotalScore(record.getTotalScore());
        result.setExamTotalScore(exam.getTotalScore());

        // 获取答题详情
        LambdaQueryWrapper<ExamAnswer> ansWrapper = new LambdaQueryWrapper<>();
        ansWrapper.eq(ExamAnswer::getRecordId, record.getId());
        List<ExamAnswer> studentAnswers = examAnswerMapper.selectList(ansWrapper);

        List<ExamResultVO.AnswerDetail> details = new ArrayList<>();
        for (ExamAnswer ans : studentAnswers) {
            Question q = questionMapper.selectById(ans.getQuestionId());
            if (q == null) continue;

            ExamResultVO.AnswerDetail detail = new ExamResultVO.AnswerDetail();
            detail.setQuestionId(q.getId());
            detail.setQuestionContent(q.getContent());
            detail.setType(q.getType());
            detail.setStudentAnswer(ans.getStudentAnswer());
            detail.setCorrectAnswer(q.getAnswer());
            detail.setIsCorrect(ans.getIsCorrect());
            detail.setScore(ans.getScore());
            details.add(detail);
        }

        result.setAnswers(details);
        return result;
    }

    @Override
    public Page<ExamResultVO> getRecordsByStudent(Long studentId, Integer current, Integer size) {
        Page<ExamRecord> page = new Page<>(current, size);
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamRecord::getStudentId, studentId)
               .orderByDesc(ExamRecord::getCreateTime);
        Page<ExamRecord> recordPage = examRecordMapper.selectPage(page, wrapper);

        Page<ExamResultVO> voPage = new Page<>(recordPage.getCurrent(), recordPage.getSize(), recordPage.getTotal());
        voPage.setRecords(recordPage.getRecords().stream()
                .map(r -> {
                    Exam exam = examMapper.selectById(r.getExamId());
                    ExamResultVO vo = new ExamResultVO();
                    vo.setRecordId(r.getId());
                    vo.setExamId(r.getExamId());
                    vo.setExamTitle(exam != null ? exam.getTitle() : "未知考试");
                    vo.setStartTime(r.getStartTime());
                    vo.setSubmitTime(r.getSubmitTime());
                    vo.setStatus(r.getStatus());
                    vo.setTotalScore(r.getTotalScore());
                    vo.setExamTotalScore(exam != null ? exam.getTotalScore() : 0);
                    return vo;
                })
                .toList());
        return voPage;
    }

    /**
     * 检查答案是否正确
     */
    private boolean checkAnswer(String type, String correctAnswer, String studentAnswer) {
        if (studentAnswer == null || studentAnswer.trim().isEmpty()) {
            return false;
        }

        switch (type) {
            case "single":
            case "true_false":
                return correctAnswer.trim().equalsIgnoreCase(studentAnswer.trim());
            case "multiple":
                // 多选：选项完全一致（顺序无关）
                Set<String> correct = Set.of(correctAnswer.replaceAll("\\s+", "").split(","));
                Set<String> student = Set.of(studentAnswer.replaceAll("\\s+", "").split(","));
                return correct.equals(student);
            default:
                return false;
        }
    }

    private ExamVO toVO(Exam exam) {
        ExamVO vo = new ExamVO();
        vo.setId(exam.getId());
        vo.setTitle(exam.getTitle());
        vo.setDescription(exam.getDescription());
        vo.setStartTime(exam.getStartTime());
        vo.setEndTime(exam.getEndTime());
        vo.setDurationMinutes(exam.getDurationMinutes());
        vo.setTotalScore(exam.getTotalScore());
        vo.setStatus(exam.getStatus());
        vo.setCreateTime(exam.getCreateTime());
        return vo;
    }
}
