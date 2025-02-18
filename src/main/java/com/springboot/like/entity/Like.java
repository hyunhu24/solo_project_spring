package com.springboot.like.entity;

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
@Table(name = "Likes")
public class Like extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

//    @OneToOne
    @ManyToOne
    @JoinColumn(name = "QUESTION_ID")
    private Question question;

//    @OneToOne
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    public void addUser(User user){
        this.user = user;
        if(!this.user.getLikes().contains(this)){
            this.user.addLikes(this);
        }
    }

}
