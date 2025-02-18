package com.springboot.answer.service;

import com.springboot.answer.entity.Answer;
import com.springboot.answer.repository.AnswerRepository;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.question.entity.Question;
import com.springboot.question.service.QuestionService;
import com.springboot.user.entity.User;
import com.springboot.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionService questionService;
    private final UserService userService;

    public AnswerService(AnswerRepository answerRepository, QuestionService questionService, UserService userService) {
        this.answerRepository = answerRepository;
        this.questionService = questionService;
        this.userService = userService;
    }

    public Answer createAnswer(Answer answer, long questionId, long userId) {
        // 관리자 검증 로직 추가
        User user = userService.findVerifiedUser(userId);
        if (!user.getRoles().contains("ADMIN")) {
            throw new BusinessLogicException(ExceptionCode.INVALID_PERMISSION);
        }

        Question question = questionService.findVerifiedQuestion(questionId);

        // 답변이 이미 등록되었는지 확인
        if (question.getAnswer() != null) {
            throw new BusinessLogicException(ExceptionCode.ANSWER_ALREADY_EXISTS);
        }

        answer.setUser(user);
        answer.setQuestion(question);
        Answer savedAnswer = answerRepository.save(answer);

        question.setAnswer(savedAnswer);
        question.setQuestionStatus(Question.QuestionStatus.QUESTION_ANSWERED);

        return savedAnswer;
    }

    public Answer updateAnswer(Answer answer, long userId) {
        Answer findAnswer = findVerifiedAnswer(answer.getAnswerId());

        // 관리자 검증 로직 추가 (본인만 수정 가능)
        if(findAnswer.getUser().getUserId() != userId){
            throw new BusinessLogicException(ExceptionCode.INVALID_PERMISSION);
        }

        Optional.ofNullable(answer.getContent())
                .ifPresent(findAnswer::setContent);

        return answerRepository.save(findAnswer);
    }

    public void deleteAnswer(long answerId, long userId) {
        Answer findAnswer = findVerifiedAnswer(answerId);

        // 관리자 검증 로직 추가 (본인만 삭제 가능)
        if(findAnswer.getUser().getUserId() != userId){
            throw new BusinessLogicException(ExceptionCode.INVALID_PERMISSION);
        }

        Question question = findAnswer.getQuestion();
        question.setQuestionStatus(Question.QuestionStatus.QUESTION_REGISTERED);
        question.setAnswer(null);

        answerRepository.delete(findAnswer);
    }

    public Answer findVerifiedAnswer(long answerId) {
        Optional<Answer> optionalAnswer = answerRepository.findById(answerId);
        return optionalAnswer.orElseThrow(() -> new BusinessLogicException(ExceptionCode.ANSWER_NOT_FOUND));
    }
}
