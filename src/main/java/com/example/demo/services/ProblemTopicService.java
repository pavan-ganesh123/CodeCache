package com.example.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Problem;
import com.example.demo.model.ProblemTopic;
import com.example.demo.model.Topic;
import com.example.demo.repository.ProblemRepository;
import com.example.demo.repository.ProblemTopicRepository;
import com.example.demo.repository.TopicRepository;
import java.util.List;

@Service
public class ProblemTopicService {
    @Autowired
    private ProblemRepository problemrepo;

    @Autowired
    private TopicRepository topicrepo;

    @Autowired
    private ProblemTopicRepository problemtopicRepo;

    public void addTopics(Long problemId, List<String> topicNames){
        Problem problem = problemrepo.findById(problemId)
                .orElseThrow(() ->
                        new RuntimeException("Problem not found"));
        
        for (String topicName : topicNames) {

            Topic topic = topicrepo.findByName(topicName)
                    .orElseGet(() -> {
                        Topic t = new Topic();
                        t.setName(topicName);
                        return topicrepo.save(t);
                    });

            if (!problemtopicRepo.existsByProblem_IdAndTopic_Id(
                    problem.getId(),
                    topic.getId())) {

                ProblemTopic pt = new ProblemTopic();
                pt.setProblem(problem);
                pt.setTopic(topic);

                problemtopicRepo.save(pt);
            }
        }
    }
}
