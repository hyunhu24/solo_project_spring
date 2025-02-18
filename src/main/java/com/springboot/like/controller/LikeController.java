package com.springboot.like.controller;

import com.springboot.dto.SingleResponseDto;
import com.springboot.like.dto.LikeDto;
import com.springboot.like.entity.Like;
import com.springboot.like.mapper.LikeMapper;
import com.springboot.like.service.LikeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/api/likes")
@Validated
public class LikeController {

    private final LikeService likeService;
    private final LikeMapper mapper;

    public LikeController(LikeService likeService, LikeMapper mapper) {
        this.likeService = likeService;
        this.mapper = mapper;
    }

    @PostMapping("/{question-id}/{user-id}")
    public ResponseEntity postLike(@PathVariable("question-id") @Positive long questionId,
                                   @PathVariable("user-id") @Positive long userId) {
        Like createdLike = likeService.createLike(questionId, userId);
        LikeDto.Response response = mapper.likeToLikeResponse(createdLike);
        return new ResponseEntity<>(new SingleResponseDto<>(response), HttpStatus.CREATED);
    }

    @DeleteMapping("/{question-id}/{user-id}")
    public ResponseEntity deleteLike(@PathVariable("question-id") @Positive long questionId,
                                     @PathVariable("user-id") @Positive long userId) {
        likeService.deleteLike(questionId, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/count/{question-id}")
    public ResponseEntity getLikeCount(@PathVariable("question-id") @Positive long questionId) {
        Long likeCount = likeService.getLikeCount(questionId);
        return new ResponseEntity<>(likeCount, HttpStatus.OK);
    }
}
