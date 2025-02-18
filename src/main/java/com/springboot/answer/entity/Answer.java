package com.springboot.answer.entity;

import com.springboot.audit.Auditable;
import com.springboot.question.entity.Question;
import com.springboot.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Answer extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long answerId;

    @Column(nullable = false)
    private String content;

    @OneToOne
    @JoinColumn(name = "QUESTION_ID")
    private Question question;

    public void setQuestion(Question question){
        this.question = question;
        if(question.getAnswer() != this){
            question.setAnswer(this);
        }
    }

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;
}
