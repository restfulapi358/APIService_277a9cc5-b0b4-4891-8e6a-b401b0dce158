package com.eduelite.api.kafka;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.eduelite.api.schema.ApiAnyconnect;
import com.eduelite.api.schema.ApiAnyconnectDeploy;
import com.eduelite.api.schema.ApiTemplate;
import com.eduelite.api.schema.ApiTemplateCallCase;
import com.eduelite.api.schema.ApiTemplateDeploy;
import com.eduelite.api.service.ApiAnyConnectDeployService;
import com.eduelite.api.service.ApiAnyconnectService;
import com.eduelite.api.service.ApiTemplateCallCaseService;
import com.eduelite.api.service.ApiTemplateDeployService;
import com.eduelite.api.service.ApiTemplateService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class KafkaMessageApiChannelResponseListener {
	
	@Autowired
	private ApiTemplateCallCaseService apiTemplateCallCaseService;
	
	@Autowired
	private ApiTemplateService apiTemplateService;
	
	@Autowired
	private ApiTemplateDeployService apiTemplateDeployService;
	
	@Autowired
	private KafkaCompleteFutureManager kafkaCompleteFutureManager;
	
	@KafkaListener(topics="channel-response", groupId="channel-response")
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
	        	
	        	//callback to process callcase
	        	//update apiCallcase
	        	ApiTemplateCallCase apiCallcase = apiTemplateCallCaseService.findById(requestId).get();
				apiCallcase.setResponse(response);
				apiCallcase.setEndTime(new Date().getTime());
				apiCallcase.setModifyDate(new Date());
				apiCallcase.setSpendTime(apiCallcase.getEndTime()-apiCallcase.getStartTime());
				apiCallcase.setStatus("OK");
				apiTemplateCallCaseService.save(apiCallcase);
				
				if(apiCallcase.getTemplateId()!=null) {
					
					ApiTemplate apiTemplate = apiTemplateService.findById(apiCallcase.getTemplateId()).get();
					if(apiTemplate.getMaxTime()<apiCallcase.getSpendTime()) {
						apiTemplate.setMaxTime(apiCallcase.getSpendTime());
					}
					if(apiTemplate.getMinTime()==0 || apiTemplate.getMinTime()>apiCallcase.getSpendTime()) {
						apiTemplate.setMinTime(apiCallcase.getSpendTime());
					}
					apiTemplate.setTotalTime(apiTemplate.getTotalTime()+ apiCallcase.getSpendTime());
					apiTemplate.setCalls(apiTemplate.getCalls()+1);
					apiTemplate.setAvgTime(Math.floorDiv(apiTemplate.getTotalTime(),apiTemplate.getCalls()));
					apiTemplateService.save(apiTemplate);
				}
				if(apiCallcase.getApiDeployId()!=null) {
					
					ApiTemplateDeploy deploy = apiTemplateDeployService.findById(apiCallcase.getApiDeployId()).get();
					if(deploy.getMaxTime()<apiCallcase.getSpendTime()) {
						deploy.setMaxTime(apiCallcase.getSpendTime());
					}
					if(deploy.getMinTime()==0 || deploy.getMinTime()>apiCallcase.getSpendTime()) {
						deploy.setMinTime(apiCallcase.getSpendTime());
					}
					deploy.setTotalTime(deploy.getTotalTime()+ apiCallcase.getSpendTime());
					deploy.setCalls(deploy.getCalls()+1);
					deploy.setAvgTime(Math.floorDiv(deploy.getTotalTime(),deploy.getCalls()));
					apiTemplateDeployService.save(deploy);
				}				
	        	
	        }
			
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
