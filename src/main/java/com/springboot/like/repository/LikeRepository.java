package com.springboot.like.repository;

import com.springboot.like.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByQuestion_QuestionIdAndUser_UserId(Long questionId, Long userId);
    Long countByQuestion_QuestionId(Long questionId); // 좋아요 개수
}
