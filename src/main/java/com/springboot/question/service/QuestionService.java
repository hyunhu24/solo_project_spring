package com.springboot.question.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.question.entity.Question;
import com.springboot.question.repository.QuestionRepository;
import com.springboot.user.entity.User;
import com.springboot.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final UserService userService;

    public QuestionService(QuestionRepository questionRepository, UserService userService) {
        this.questionRepository = questionRepository;
        this.userService = userService;
    }

    public Question createQuestion(Question question, long userId) {
        // 회원 검증 로직 추가 (본인만 작성 가능)
        User user = userService.findVerifiedUser(userId);
        question.addUsers(user);
        question.setViews(0L);
        return questionRepository.save(question);
    }

    public Question updateQuestion(Question question, long userId) {
        Question findQuestion = findVerifiedQuestion(question.getQuestionId());

        // 작성자 검증 로직 추가 (본인만 수정 가능)
        if(findQuestion.getUser().getUserId() != userId){
            throw new BusinessLogicException(ExceptionCode.INVALID_PERMISSION);
        }

        Optional.ofNullable(question.getTitle())
                .ifPresent(findQuestion::setTitle);
        Optional.ofNullable(question.getContent())
                .ifPresent(findQuestion::setContent);
        Optional.ofNullable(question.getVisibilityStatus())
                .ifPresent(findQuestion::setVisibilityStatus);
        Optional.ofNullable(question.getQuestionStatus())
                .ifPresent(findQuestion::setQuestionStatus);

        return questionRepository.save(findQuestion);
    }

    public Question findQuestion(long questionId) {
        Question question = findVerifiedQuestion(questionId);
        question.setViews(question.getViews() + 1); // 조회수 증가
        questionRepository.save(question);
        return question;
    }

    public Page<Question> findQuestions(int page, int size) {
        return questionRepository.findAll(PageRequest.of(page, size, Sort.by("questionId").descending()));
    }

    public Page<Question> findQuestionsByUserEmail(String email, int page, int size){
        Pageable pageable = PageRequest.of(page,size, Sort.by("createdAt").descending());

        return questionRepository.findByUserEmail( email, pageable);
    }

    public Page<Question> searchQuestions(String keyword, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("questionId").descending());
        return questionRepository.findByTitleContaining(keyword, pageRequest);
    }

    public void deleteQuestion(long questionId, long userId) {
        Question findQuestion = findVerifiedQuestion(questionId);

        // 작성자 검증 로직 추가 (본인만 삭제 가능)
        if(findQuestion.getUser().getUserId() != userId){
            throw new BusinessLogicException(ExceptionCode.INVALID_PERMISSION);
        }

        findQuestion.setQuestionStatus(Question.QuestionStatus.QUESTION_DELETED);
        questionRepository.save(findQuestion);
        //questionRepository.delete(findQuestion); // 실제 삭제 대신 상태 변경
    }

    public Question findVerifiedQuestion(long questionId) {
        Optional<Question> optionalQuestion = questionRepository.findById(questionId);
        return optionalQuestion.orElseThrow(() -> new BusinessLogicException(ExceptionCode.QUESTION_NOT_FOUND));
    }
}
