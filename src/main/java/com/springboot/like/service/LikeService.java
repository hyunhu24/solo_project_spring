package com.springboot.like.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.like.entity.Like;
import com.springboot.like.repository.LikeRepository;
import com.springboot.question.entity.Question;
import com.springboot.question.service.QuestionService;
import com.springboot.user.entity.User;
import com.springboot.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class LikeService {

    private final LikeRepository likeRepository;
    private final QuestionService questionService;
    private final UserService userService;

    public LikeService(LikeRepository likeRepository, QuestionService questionService, UserService userService) {
        this.likeRepository = likeRepository;
        this.questionService = questionService;
        this.userService = userService;
    }

    public Like createLike(long questionId, long userId) {
        Question question = questionService.findVerifiedQuestion(questionId);
        User user = userService.findVerifiedUser(userId);

        // 이미 좋아요를 눌렀는지 확인
        Optional<Like> existingLike = likeRepository.findByQuestion_QuestionIdAndUser_UserId(questionId, userId);
        if (existingLike.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.LIKE_ALREADY_EXISTS);
        }

        Like like = new Like();
        like.setQuestion(question);
        like.setUser(user);

        return likeRepository.save(like);
    }

    public void deleteLike(long questionId, long userId) {
        Question question = questionService.findVerifiedQuestion(questionId);
        User user = userService.findVerifiedUser(userId);

        Optional<Like> optionalLike = likeRepository.findByQuestion_QuestionIdAndUser_UserId(questionId, userId);
        Like like = optionalLike.orElseThrow(() -> new BusinessLogicException(ExceptionCode.LIKE_NOT_FOUND));

        likeRepository.delete(like);
    }

    public Long getLikeCount(long questionId) {
        questionService.findVerifiedQuestion(questionId); // 질문 존재 여부 확인
        return likeRepository.countByQuestion_QuestionId(questionId);
    }
}
