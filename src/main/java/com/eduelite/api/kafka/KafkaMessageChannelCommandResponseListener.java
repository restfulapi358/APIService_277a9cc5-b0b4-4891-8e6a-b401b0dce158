package com.eduelite.api.kafka;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.eduelite.api.schema.ApiAnyconnect;
import com.eduelite.api.schema.ApiAnyconnectDeploy;
import com.eduelite.api.service.ApiAnyConnectDeployService;
import com.eduelite.api.service.ApiAnyconnectService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class KafkaMessageChannelCommandResponseListener {
	
	@Autowired
	private ApiAnyConnectDeployService apiAnyConnectDeployService;
	
	@Autowired
	private ApiAnyconnectService apiAnyconnectService; 
	
	private final String CHANNEL_COMMAND_TOPIC="channel-command_response";
	
	@Autowired
	private KafkaCompleteFutureManager kafkaCompleteFutureManager;
	
	@KafkaListener(topics=CHANNEL_COMMAND_TOPIC, groupId="channel-response")
	public void listen(String message) {
		
		System.out.println("Received message from channel-response:" + message);
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			KafkaResponseMessage response = mapper.readValue(message, KafkaResponseMessage.class);
			
			String requestId = response.getRequestId();
			
			System.out.println("kafkaCompleteFutureManager response Id:" + requestId);

	        CompletableFuture<Object> future = kafkaCompleteFutureManager.getRequestFuture(requestId);
	        if (future != null) {
	        	
	            future.complete(response);
	            kafkaCompleteFutureManager.removeRequestFuture(requestId);
	        }
	        else {
	        	//it's timeout, we need to update in backend
	        	
	        	List<ApiAnyconnectDeploy> deploys = apiAnyConnectDeployService.findDeploysByRefId(requestId);
	        	if(deploys.size()==1) {
	        		
	        		ApiAnyconnectDeploy deploy = deploys.get(0);
	        		
	        		if(response.getCommand().equals("register")) {
	        			LinkedHashMap workerResponse = (LinkedHashMap) response.getResponse();
		        		if(workerResponse!=null && workerResponse.containsKey("status")) {
		        			
		        			String status = workerResponse.get("status")!=null?workerResponse.get("status").toString():null;
		        			deploy.setStatus("Failed");
		        			if(status!=null && status.equals("Success")) {	        				
		        				deploy.setStatus("Active");	
		        				
		        				ApiAnyconnect template = apiAnyconnectService.findById(deploy.getAnyConnectId()).get();	  
		        				template.setDeployId(deploy.getId());
			        			template.setVersion(deploy.getVersion());
			        			if(template.getStatus().equals("running")) {
			        				template.setStatus("Active");
			        			}
			    				apiAnyconnectService.save(template);
		        				
		        			}
		        			apiAnyConnectDeployService.save(deploy);
		        			        			
		        		}
	        		}	        		
	        		
	        	}
	        	else {
	        	
	        		//throw exception: data no-consistant
	        	
	        	}
	        	
	        	
	        }
			
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
