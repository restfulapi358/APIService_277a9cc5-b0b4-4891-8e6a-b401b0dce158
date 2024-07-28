package com.eduelite.api.controller;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import com.eduelite.api.business.ApiProxyService;
import com.eduelite.api.kafka.ChannelConnectRequest;
import com.eduelite.api.kafka.KafkaTopicService;
import com.eduelite.api.schema.ApiTemplate;
import com.eduelite.api.schema.ApiTemplateCallCase;
import com.eduelite.api.schema.ApiTemplateDeploy;
import com.eduelite.api.schema.ApiTemplateDevelop;
import com.eduelite.api.schema.ComputingChannel;
import com.eduelite.api.service.ApiTemplateCallCaseService;
import com.eduelite.api.service.ComputingChannelService;
import com.eduelite.common.controller.EducationObjectController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v3/edu/ApiProxy")
@CrossOrigin(allowedHeaders = "*")
public class ApiTemplateProxyController extends EducationObjectController<ApiTemplateCallCase> {

	@Autowired
	private ComputingChannelService computingChannelService;
	
	@Autowired
	private ApiProxyService apiProxyService;
	
	@Autowired
	private KafkaTopicService kafkaTopicService;
		
	@PostMapping(path = "/api/{id}", produces="application/json")
	public Mono<Object>  apiProxy(@PathVariable String id, @RequestBody LinkedHashMap request) {			
		
		request.forEach((key, value) -> {
	        System.out.println(String.format(
	          "Header '%s' = %s", key, value));
	    });
		
		return this.apiProxyService.proxy(id, request) ;		
	}
	
	@PostMapping(path = "/channel/connect", produces="application/json")
	public Mono<Object>  apiProxy(@RequestBody ChannelConnectRequest request) {			
		
		//check channelId and pin		
		Optional<ComputingChannel> channelObj = computingChannelService.findById(request.getChannelId());
		
		if(channelObj.isPresent()) {
			
			ComputingChannel channel = channelObj.get();
			String pin = channel.getPin();
			
			if(pin!=null && pin.equals(request.getPin())){			
				//passed
				kafkaTopicService.createTopic("channel"+request.getChannelId(), 1, (short)1);
				
				channel.setStatus("connected");
				this.computingChannelService.save(channel);
				
				return Mono.just(ResponseEntity.ok().body("{\"status\":\"OK\"}"));		
			}
		}
		
		return Mono.just(ResponseEntity.ok().body("{\"status\":\"Failed\"}"));		
	}	
	
	
}
