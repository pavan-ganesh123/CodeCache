package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name ="problem_topic",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"problem_id","topic_id"})
    }
)
public class ProblemTopic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @ManyToOne(optional = false)
    @JoinColumn(name ="topic_id")
    private Topic topic;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    
}
