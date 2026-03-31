package com.example.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Problem;
import com.example.demo.repository.ProblemRepository;

import java.util.List;

@Service
public class ProblemService {
    
    @Autowired
    private ProblemRepository repo;

    public Problem save(Problem p){
        return repo.save(p);
    }
    public List<Problem> getAll(){
        return repo.findAll();
    }

    public Problem getById(Long id){
        return repo.findById(id).orElse(null);
    }
    
}
