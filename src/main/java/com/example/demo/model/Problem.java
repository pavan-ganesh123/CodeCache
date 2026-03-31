package com.example.demo.model;

import jakarta.persistence.*;

@Entity
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String platformName;
    private String questionName;
    private int questionId;
    private String difficulty;
    private String link;
    private String intuition;
    private String code;

    public Problem(){}

    public Problem(Long id,String platformName,String questionName,int questionID,String difficulty,String link,String intuition,String code){
        this.id=id;
        this.platformName=platformName;
        this.questionName=questionName;
        this.questionId=questionID;
        this.difficulty=difficulty;
        this.link=link;
        this.intuition=intuition;
        this.code=code;
    }

    public Long getId() {
        return id;
    }

    public String getPlatformName() {
        return platformName;
    }

    public String getQuestionName() {
        return questionName;
    }

    public int getQuestionId() {
        return questionId;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getLink() {
        return link;
    }

    public String getIntuition() {
        return intuition;
    }

    public String getCode() {
        return code;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setIntuition(String intuition) {
        this.intuition = intuition;
    }

    public void setCode(String code) {
        this.code = code;
    }
    
}
