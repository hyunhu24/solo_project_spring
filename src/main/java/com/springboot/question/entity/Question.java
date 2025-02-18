package com.springboot.question.entity;

import com.springboot.answer.entity.Answer;
import com.springboot.audit.Auditable;
import com.springboot.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Question extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 20, nullable = false)
    private VisibilityStatus visibilityStatus = VisibilityStatus.PUBLIC;

    @Column
    private Long views;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 20, nullable = false)
    private QuestionStatus questionStatus = QuestionStatus.QUESTION_REGISTERED;

    @OneToOne
    @JoinColumn(name = "ANSWER_ID")
    private Answer answer;

    public void setAnswer(Answer answer){
        this.answer = answer;
        if(answer.getQuestion() != this){
            answer.setQuestion(this);
        }
    }

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    public void addUsers(User user){
        this.user = user;
        if(!user.getQuestions().contains(this)){
            user.addQuestions(this);
        }
    }

    public enum VisibilityStatus{
        PUBLIC("공개글"),
        SECRET("비밀글");

        @Getter
        private String status;

        VisibilityStatus(String status) {this.status = status;}
    }

    public enum QuestionStatus{
        QUESTION_REGISTERED("질문 등록"),
        QUESTION_ANSWERED("답변 완료"),
        QUESTION_DELETED("질문 삭제"),
        QUESTION_DEACTIVED("질문 비활성화");

        @Getter
        private String status;

        QuestionStatus(String status){this.status = status;}
    }

//    QUESTION_REGISTERED- 질문 등록 상태
//    ㄴ QUESTION_ANSWERED - 답변 완료 상태
//    ㄴ QUESTION_DELETED - 질문 삭제 상태
//    ㄴ QUESTION_DEACTIVED - 질문 비활성화 상태: 회원 탈퇴 시, 질문 비활성화 상태


}
