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

import com.eduelite.api.business.ApiAnyConnectProxyService;
import com.eduelite.api.schema.ApiAnyconnect;
import com.eduelite.api.schema.ApiAnyconnectDeploy;
import com.eduelite.api.schema.ApiTemplate;
import com.eduelite.api.schema.ApiTemplateDevelop;
import com.eduelite.api.service.ApiTemplateDevelopService;
import com.eduelite.api.service.ApiTemplateService;
import com.eduelite.common.controller.EducationObjectController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v3/edu/ApiAnyconnect")
@CrossOrigin(allowedHeaders = "*")
public class ApiAnyconnectController extends EducationObjectController<ApiAnyconnect> {
	
	@Autowired
	private ApiAnyConnectProxyService apiAnyConnectProxyService;
	
	@PostMapping("/monitorCommand/{channelId}/{command}")
    public Mono<Object> monitorCommand(@PathVariable String channelId, @PathVariable String command, @RequestBody LinkedHashMap request) {		
		
		//sent the command (start worker) to anyconnect gateway
		LinkedHashMap commandRequest = new LinkedHashMap();
		commandRequest.put("id", UUID.randomUUID().toString());
		commandRequest.put("type", "anyconnect");
		commandRequest.put("command", command);
		commandRequest.put("params", request);
		
		return this.apiAnyConnectProxyService.sendCommand("channel"+channelId, commandRequest);
		
	}
	
}
