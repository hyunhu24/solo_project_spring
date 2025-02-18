package com.springboot.answer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

public class AnswerDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Post {
        @NotBlank(message = "답변 내용은 필수 입력 항목입니다.")
        private String content;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Patch {
        private Long answerId;

        @NotBlank(message = "답변 내용은 필수 입력 항목입니다.")
        private String content;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long answerId;
        private String content;
        private Long questionId;
        private Long userId; // 작성자 ID (관리자)
        private String userName; // 작성자 이름 (관리자)

        // 필요에 따라 질문 관련 정보 추가
    }
}
