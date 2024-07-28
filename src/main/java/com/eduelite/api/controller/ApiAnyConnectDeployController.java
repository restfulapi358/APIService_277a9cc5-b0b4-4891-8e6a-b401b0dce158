package com.eduelite.api.controller;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.MDC;
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
import com.eduelite.api.schema.ApiTemplateDeploy;
import com.eduelite.api.service.ApiAnyConnectDeployService;
import com.eduelite.api.service.ApiAnyconnectService;
import com.eduelite.api.service.ApiTemplateDeployService;
import com.eduelite.api.service.ApiTemplateService;
import com.eduelite.common.controller.EducationObjectController;


import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v3/edu/ApiAnyConnectDeploy")
@CrossOrigin(allowedHeaders = "*")
public class ApiAnyConnectDeployController extends EducationObjectController<ApiAnyconnectDeploy> {
	
	@Autowired
	private ApiAnyConnectDeployService apiAnyConnectDeployService;
	
	@Autowired
	private ApiAnyconnectService apiAnyconnectService;
	
	@Autowired
	private ApiAnyConnectProxyService apiAnyConnectProxyService;
	
	@GetMapping(path = "/findDeploysByAnyConnectId/{anyConnectId}", produces="application/json")
	public ResponseEntity<?> findDeploysByTemplateId(@PathVariable String anyConnectId) {		
		
		return new ResponseEntity(apiAnyConnectDeployService.findDeploysByAnyConnectId(anyConnectId), HttpStatus.OK) ;	
	}	
	
	@PostMapping("/register")
    public Mono<Object> register(@RequestBody ApiAnyconnectDeploy templateDeploy) {		
		
		//if develop.id=>template exist, update info (modified date or version)
		//need more security logic
		String requestId = UUID.randomUUID().toString();
		
		LinkedHashMap request = new LinkedHashMap();
		request.put("id", UUID.randomUUID().toString());
		request.put("command", "register");
		request.put("params", templateDeploy);
		
		
		String authorized = MDC.get("AUTHORIZED");
		String owner = MDC.get("OWNER");
		String operator = MDC.get("OPERATOR");
		String role = MDC.get("ROLE");
		
		templateDeploy.setId(null);  //every register should be empty for new
		templateDeploy.setVisibility("private");
		templateDeploy.setOwner(owner);
		templateDeploy.setModifier(operator);
		templateDeploy.setModifyDate(new Date());
		templateDeploy.setStatus("running");
		templateDeploy.setRefid(requestId);
		
		if(templateDeploy.getAnyConnectId()==null) {
		
			ApiAnyconnect template = new ApiAnyconnect();
			
			template.setName(templateDeploy.getName());
			template.setDescription(templateDeploy.getDescription());
			
			template.setDomain(templateDeploy.getDomain());
			template.setConcept(templateDeploy.getConcept());
			template.setVersion(templateDeploy.getVersion());
			template.setDeployId(templateDeploy.getId());
			template.setStatus("running");
			template.setVisibility("private");
			template.setRoute(templateDeploy.getRoute());   //route should be the deployId
			template.setTag(templateDeploy.getTag());
			
			template.setOwner(owner);
			template.setModifier(operator);
			template.setModifyDate(new Date());
			
			//start deployId
			template.setRefid(templateDeploy.getRefid());
			
			template= apiAnyconnectService.save(template);			
			templateDeploy.setAnyConnectId(template.getId());
			templateDeploy = apiAnyConnectDeployService.save(templateDeploy);				
			
		}	
		else {
			templateDeploy = apiAnyConnectDeployService.save(templateDeploy);	
		}
		return this.apiAnyConnectProxyService.sendRequest(requestId, templateDeploy, request);
	}
	
	@PostMapping("/startWorker")
    public Mono<Object> startWorker(@RequestBody ApiAnyconnectDeploy deploy) {		
		
		String requestId = UUID.randomUUID().toString();
		
		//sent the command (start worker) to anyconnect gateway
		LinkedHashMap request = new LinkedHashMap();
		request.put("id", UUID.randomUUID().toString());
		request.put("command", "startWorker");
		request.put("params", deploy);
		
		return this.apiAnyConnectProxyService.sendRequest(requestId, deploy, request);
		
	}
	
	
	
}
