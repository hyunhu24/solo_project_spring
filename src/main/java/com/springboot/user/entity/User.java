package com.springboot.user.entity;

import com.springboot.audit.Auditable;
import com.springboot.like.entity.Like;
import com.springboot.question.entity.Question;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class User extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, updatable = false, unique = true)
    private String email;

    @Column(length = 100, nullable = false)
    private String password;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 13, nullable = false, unique = true)
    private String phone;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 20, nullable = false)
    private UserStatus userStatus = UserStatus.USER_ACTIVE;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    @OneToMany(mappedBy = "user" , cascade = CascadeType.PERSIST)
    private List<Like> likes = new ArrayList<>();

    public void addLikes(Like like){
        this.likes.add(like);
        if(like.getUser() != this){
            like.addUser(this);
        }
    }

    @OneToMany(mappedBy = "user" , cascade = CascadeType.PERSIST)
    private List<Question> questions = new ArrayList<>();

    public void addQuestions(Question question){
        this.questions.add(question);
        if(question.getUser() != this){
            question.addUsers(this);
        }
    }

    public User(String email) {
        this.email = email;
    }

    public User(String email, String name, String phone) {
        this.email = email;
        this.name = name;
        this.phone = phone;
    }

    public enum UserStatus {
        USER_ACTIVE("활동중"),
        USER_SLEEP("휴면 상태"),
        USER_QUIT("탈퇴 상태");

        @Getter
        private String status;

        UserStatus(String status) {
            this.status = status;
        }
    }
}
