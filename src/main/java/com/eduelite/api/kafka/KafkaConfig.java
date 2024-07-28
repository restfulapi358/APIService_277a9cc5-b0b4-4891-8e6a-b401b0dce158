package com.eduelite.api.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaConfig {
	
	@Value("${spring.kafka.bootstrap-servers}")
	private String kafka_bootstrap_servers;
	
	@Bean
	public KafkaAdmin kafkaAdmin() {
		return new KafkaAdmin(kafkaProperties());
	}
	
	@Bean
	public Map<String,Object> kafkaProperties(){
		Map<String,Object> props=new HashMap<>();
		props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafka_bootstrap_servers);
		return props;
	}
	
	@Bean
	public NewTopic topicName() {
		return new NewTopic("channel-response", 1, (short)1);
	}
	
	@Bean
	public AdminClient adminClient(KafkaAdmin kafkaAdmin) {
		return AdminClient.create(kafkaAdmin.getConfigurationProperties());
	}
	

}
