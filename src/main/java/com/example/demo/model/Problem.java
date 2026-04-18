package com.example.demo.model;

import jakarta.persistence.*;

@Entity
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "platform_name")
    private String platformName;
    @Column(name = "question_name")
    private String questionName;
    private Integer questionId;
    private String difficulty;
    private String link;
    @Lob
    private String intuition;
    @Lob
    private String keyIdea;
    @Lob
    private String approach;
    @Lob
    private String mistakes;
    @Lob
    private String code;
    private String timeComplexity;
    private String spaceComplexity;

    public Problem(){}

    public Problem(Long id,String platformName,String questionName,Integer questionID,String difficulty,String link,String intuition,String code){
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

    public Integer getQuestionId() {
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
    public String getKeyIdea() {
        return keyIdea;
    }
    public String getApproach() {
        return approach;
    }
    public String getMistakes() {
        return mistakes;
    }

    public String getTimeComplexity() {
        return timeComplexity;
    }

    public String getSpaceComplexity() {
        
        return spaceComplexity;
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
    
    public void setQuestionId(Integer questionId) {
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

    public void setKeyIdea(String keyIdea) {
        this.keyIdea = keyIdea;
    }
    
    public void setApproach(String approach) {
        this.approach = approach;
    }
    
    public void setMistakes(String mistakes) {
        this.mistakes = mistakes;
    }
    
    public void setTimeComplexity(String timeComplexity) {
        this.timeComplexity = timeComplexity;
    }
    public void setSpaceComplexity(String spaceComplexity) {
        this.spaceComplexity = spaceComplexity;
    }
    
}
