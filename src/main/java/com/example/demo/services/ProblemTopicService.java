package com.example.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.ProblemTopic;
import com.example.demo.model.Topic;
import com.example.demo.model.UserProblem;
import com.example.demo.repository.ProblemTopicRepository;
import com.example.demo.repository.TopicRepository;
import com.example.demo.repository.UserProblemRepository;
import java.util.List;

@Service
public class ProblemTopicService {
    @Autowired
    private UserProblemRepository userProblemRepo;

    @Autowired
    private TopicRepository topicrepo;

    @Autowired
    private ProblemTopicRepository problemtopicRepo;

    public void addTopics(Long userProblemId, List<String> topicNames) {
        UserProblem userProblem = userProblemRepo.findById(userProblemId)
                .orElseThrow(() -> new RuntimeException("UserProblem not found"));

        for (String rawName : topicNames) {
            String topicName = rawName.trim();
            if (topicName.isEmpty()) continue;

            Topic topic = topicrepo.findByNameIgnoreCase(topicName)
                    .orElseGet(() -> {
                        Topic t = new Topic();
                        t.setName(topicName);
                        return topicrepo.save(t);
                    });

            if (!problemtopicRepo.existsByUserProblem_IdAndTopic_Id(userProblem.getId(), topic.getId())) {
                ProblemTopic pt = new ProblemTopic();
                pt.setUserProblem(userProblem);
                pt.setTopic(topic);
                problemtopicRepo.save(pt);
            }
        }
    }
}