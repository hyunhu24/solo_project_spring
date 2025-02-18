package com.springboot.question.dto;

import com.springboot.question.entity.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class QuestionDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Post {
        @NotBlank(message = "제목은 필수 입력 항목입니다.")
        private String title;

        @NotBlank(message = "내용은 필수 입력 항목입니다.")
        private String content;

        @NotNull(message = "공개 여부를 선택해야 합니다.")
        private Question.VisibilityStatus visibilityStatus;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Patch {
        private Long questionId;

        @NotBlank(message = "제목은 필수 입력 항목입니다.")
        private String title;

        @NotBlank(message = "내용은 필수 입력 항목입니다.")
        private String content;

        @NotNull(message = "공개 여부를 선택해야 합니다.")
        private Question.VisibilityStatus visibilityStatus;

        private Question.QuestionStatus questionStatus;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long questionId;
        private String title;
        private String content;
        private Question.VisibilityStatus visibilityStatus;
        private Long views;
        private Question.QuestionStatus questionStatus;
        private Long userId; // 작성자 ID
        private String userName; // 작성자 이름 (추가)

        // 필요에 따라 답변 관련 정보 추가
    }
}
