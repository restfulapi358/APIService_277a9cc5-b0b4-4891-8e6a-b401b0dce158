package com.eduelite.api.business;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriBuilder;

import com.eduelite.api.kafka.KafkaCompleteFutureManager;
import com.eduelite.api.kafka.KafkaProducer;
import com.eduelite.api.kafka.KafkaRequestMessage;
import com.eduelite.api.kafka.KafkaResponseMessage;
import com.eduelite.api.schema.ApiAnyconnect;
import com.eduelite.api.schema.ApiAnyconnectDeploy;
import com.eduelite.api.schema.ApiTemplate;
import com.eduelite.api.schema.ApiTemplateDeploy;
import com.eduelite.api.schema.ApiTemplateDevelop;
import com.eduelite.api.schema.ComputingChannel;
import com.eduelite.api.service.ApiAnyConnectDeployService;
import com.eduelite.api.service.ApiAnyconnectService;
import com.eduelite.api.service.ApiTemplateDeployService;
import com.eduelite.api.service.ApiTemplateService;
import com.eduelite.api.service.ComputingChannelService;
import com.eduelite.common.schema.DataAttribute;
import com.eduelite.common.util.VersionFormatUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

	

@Service
public class ApiAnyConnectProxyService{
		
	@Autowired
	private ApiAnyConnectDeployService apiAnyConnectDeployService;
	
	@Autowired
	private ApiAnyconnectService apiAnyconnectService; 
	
	@Autowired
	private KafkaProducer kafkaProducer;
	
	@Autowired
	private KafkaCompleteFutureManager kafkaCompleteFutureManager;
	
	
	public Mono<Object> sendRequest(String requestId, ApiAnyconnectDeploy deploy, LinkedHashMap request){
		
		//kafka
		try {
			
			KafkaRequestMessage requestMessage = new KafkaRequestMessage();
			requestMessage.setRequestId(requestId);
			requestMessage.setType("anyconnect");
			requestMessage.setRequest(request);
			requestMessage.setRoute(deploy.getAnyConnectId());
			requestMessage.setRouteVersion(VersionFormatUtil.formatVersion(deploy.getVersion()));
			requestMessage.setDeployId(deploy.getId());
			
			kafkaProducer.sendCommand("channel"+deploy.getApichannel().replace(":","").replace("//", "").replace("channel", ""), requestMessage);
			CompletableFuture<Object> future = new CompletableFuture<>();
			
			kafkaCompleteFutureManager.addRequestFuture(requestMessage.getRequestId(), future );
			System.out.println("kafkaCompleteFutureManager request Id:" + requestMessage.getRequestId().toString());
			
			try {
				
				Object response = future.get(120, TimeUnit.SECONDS);
				
				String command = request.get("command").toString();
				if(command.equals("register")) {
					LinkedHashMap workerResponse = (LinkedHashMap) ((KafkaResponseMessage) response).getResponse();
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
				return Mono.just(ResponseEntity.ok(response));
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block				
				e.printStackTrace();
				KafkaResponseMessage responseMessasge = new KafkaResponseMessage();
				LinkedHashMap responseRequest= new LinkedHashMap();
				responseRequest.put("status", "Time out for registering. Please go to Channel to check later.");
				responseMessasge.setResponse(responseRequest);
				kafkaCompleteFutureManager.removeRequestFuture(requestMessage.getRequestId());
				return Mono.just(ResponseEntity.ok(responseMessasge));
			}
			
			KafkaResponseMessage responseMessasge = new KafkaResponseMessage();
			LinkedHashMap responseRequest= new LinkedHashMap();
			responseRequest.put("status", "Failed for registering. Please check github source code to try later.");
			responseMessasge.setResponse(responseRequest);
			kafkaCompleteFutureManager.removeRequestFuture(requestMessage.getRequestId());
			System.out.println("remove future:" + requestMessage.getRequestId());
			return Mono.just(ResponseEntity.ok(responseMessasge));
			
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Mono.empty();	
		
	}
	
	
	public Mono<Object> sendCommand(String channelId, LinkedHashMap request){
		
		//kafka
		LinkedHashMap responseFailed= new LinkedHashMap();			
		try {
			
			KafkaRequestMessage requestMessage = new KafkaRequestMessage();
			requestMessage.setRequestId(UUID.randomUUID().toString());
			requestMessage.setType("anyconnect");
			requestMessage.setRequest(request);
			requestMessage.setRoute("info");
			
			kafkaProducer.sendCommand(channelId, requestMessage);
			CompletableFuture<Object> future = new CompletableFuture<>();
			
			kafkaCompleteFutureManager.addRequestFuture(requestMessage.getRequestId(), future );
			System.out.println("kafkaCompleteFutureManager request Id:" + request.get("id").toString());
			
			try {
				
				Object response = future.get(5, TimeUnit.SECONDS);
				
				return Mono.just(ResponseEntity.ok(response));
				
			} catch (Exception e) {
				
				e.printStackTrace();
				
				responseFailed.put("status", "Failed");
				responseFailed.put("message", e.getMessage());
				
			}
			
			return Mono.just(ResponseEntity.ok(responseFailed));
			
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseFailed.put("status", "Failed");
			responseFailed.put("message", e.getMessage());
		}
		
		return Mono.just(ResponseEntity.ok(responseFailed));
		
	}
	
}
