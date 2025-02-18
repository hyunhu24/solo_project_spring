package com.springboot.like.mapper;

import com.springboot.like.dto.LikeDto;
import com.springboot.like.entity.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeMapper {
    Like likePostToLike(LikeDto.Post requestBody);

    @Mapping(source = "question.questionId", target = "questionId")
    @Mapping(source = "user.userId", target = "userId")
    LikeDto.Response likeToLikeResponse(Like like);
    List<LikeDto.Response> likesToLikeResponses(List<Like> likes);
}
