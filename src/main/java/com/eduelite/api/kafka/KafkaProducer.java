package com.eduelite.api.kafka;

import java.util.Date;
import java.util.UUID;

import org.apache.kafka.common.Uuid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.eduelite.api.schema.ApiTemplateCallCase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class KafkaProducer {
	
	
	@Autowired
	private KafkaTemplate<String,String> kafkaTemplate;
	
	public void sendMessage(String topic, String message) {
		kafkaTemplate.send(topic, message);
		System.out.println(String.format("Message send to %s: %s", topic, message));
	}
	
	public KafkaRequestMessage sendRequest(
			String topic, 
			ApiTemplateCallCase apiCallcase
			) throws JsonProcessingException {
		
		ObjectMapper mapper = new ObjectMapper();
		
		KafkaRequestMessage requestMessage = new KafkaRequestMessage();
		requestMessage.setRequestId(apiCallcase.getId());
		requestMessage.setMethod(apiCallcase.getApimethod());
		requestMessage.setType("api");
		requestMessage.setProtocol(apiCallcase.getApiprotocol());
		requestMessage.setUrl(apiCallcase.getApiurl());
		requestMessage.setAttributes(apiCallcase.getRequestAttributes());
		requestMessage.setConstant(apiCallcase.getConstants());
		requestMessage.setStarttime(new Date().getTime());
		requestMessage.setRequest(apiCallcase.getRequest());
		requestMessage.setRoute(apiCallcase.getRoute());
		requestMessage.setRouteVersion(apiCallcase.getRouteVersion());
		requestMessage.setDeployId(apiCallcase.getApiDeployId());
		
		
		String strRequestMessage = mapper.writeValueAsString(requestMessage);
		sendMessage(topic, strRequestMessage);
		return requestMessage;
		
	}
	
	public KafkaRequestMessage sendRequest(
			String topic, 
			String method, String protocol, String url, 
			Object attributes,  Object params,  Object request,
			String route, String routeVersion, String deployId
			) throws JsonProcessingException {
		
		ObjectMapper mapper = new ObjectMapper();
		
		KafkaRequestMessage requestMessage = new KafkaRequestMessage();
		requestMessage.setRequestId(UUID.randomUUID().toString());
		requestMessage.setMethod(method);
		requestMessage.setType("api");
		requestMessage.setProtocol(protocol);
		requestMessage.setUrl(url);
		requestMessage.setAttributes(attributes);
		requestMessage.setConstant(params);
		requestMessage.setStarttime(new Date().getTime());
		requestMessage.setRequest(request);
		requestMessage.setRoute(route);
		requestMessage.setRouteVersion(routeVersion);
		requestMessage.setDeployId(deployId);
		
		String strRequestMessage = mapper.writeValueAsString(requestMessage);
		sendMessage(topic, strRequestMessage);
		return requestMessage;
		
	}
	
	//topic: channel{id}
	public void sendCommand(String topic, Object request) throws JsonProcessingException {
		
		ObjectMapper mapper = new ObjectMapper();
		String strRequestMessage = mapper.writeValueAsString(request);
		sendMessage(topic, strRequestMessage);
		return;
		
	}
	
	

}
