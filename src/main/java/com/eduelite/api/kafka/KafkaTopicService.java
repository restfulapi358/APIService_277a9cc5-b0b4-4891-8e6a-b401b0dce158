package com.eduelite.api.kafka;

import java.util.Collections;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KafkaTopicService {

	@Autowired
	private AdminClient adminClient;
	
	public void createTopic(String topicName, int portitions, short replicationFactor) {
		NewTopic newTopic = new NewTopic(topicName, portitions, replicationFactor);
		adminClient.createTopics(Collections.singleton(newTopic));
		System.out.println("Create new topic: " + topicName);
	}
	
}
