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

import com.eduelite.api.schema.ApiTemplate;
import com.eduelite.api.schema.ApiTemplateDeploy;
import com.eduelite.api.service.ApiTemplateDeployService;
import com.eduelite.api.service.ApiTemplateService;
import com.eduelite.common.controller.EducationObjectController;


import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v3/edu/ApiTemplateDeploy")
@CrossOrigin(allowedHeaders = "*")
public class ApiTemplateDeployController extends EducationObjectController<ApiTemplateDeploy> {
	
	@Autowired
	private ApiTemplateDeployService apiTemplateDeployService;
	
	@Autowired
	private ApiTemplateService apiTemplateService;
	
	@GetMapping(path = "/findDeploysByTemplateId/{templateId}", produces="application/json")
	public ResponseEntity<?> findDeploysByTemplateId(@PathVariable String templateId) {		
		
		return new ResponseEntity(apiTemplateDeployService.findDeploysByTemplateId(templateId), HttpStatus.OK) ;	
	}	
	
	@PostMapping("/register")
    public ApiTemplateDeploy register(@RequestBody ApiTemplateDeploy templateDeploy) {		
		
		//if develop.id=>template exist, update info (modified date or version)
		//need more security logic
		String authorized = MDC.get("AUTHORIZED");
		String owner = MDC.get("OWNER");
		String operator = MDC.get("OPERATOR");
		String role = MDC.get("ROLE");
		
		templateDeploy.setVisibility("private");
		templateDeploy.setOwner(owner);
		templateDeploy.setModifier(operator);
		templateDeploy.setModifyDate(new Date());
		
		templateDeploy = apiTemplateDeployService.save(templateDeploy);
		
		Optional<ApiTemplate> templateOpt = apiTemplateService.findById(templateDeploy.getTemplateId());
		if( templateOpt.isPresent() ) {
			
			ApiTemplate template = templateOpt.get();
			//template.setName(templateDeploy.getName());
			//template.setDescription(templateDeploy.getDescription());
			template.setVersion(templateDeploy.getVersion());
			template.setApiDeployId(templateDeploy.getId());
			template.setStatus("Active");
			template.setVisibility("private");
			
			template.setTag(templateDeploy.getApiprotocol());
			template.setRoute(templateDeploy.getRoute());
			template.setRouteVersion(templateDeploy.getRouteVersion());
			template.setApichannel(templateDeploy.getApichannel());
						
			template.setModifier(operator);
			template.setModifyDate(new Date());
			
			apiTemplateService.save(template);			
		}
		else {
			
			ApiTemplate template = new ApiTemplate();
			template.setId(templateDeploy.getTemplateId());
			template.setName(templateDeploy.getName());
			template.setDescription(templateDeploy.getDescription());
			
			template.setTag(templateDeploy.getApiprotocol());
			template.setDomain(templateDeploy.getDomain());
			
			template.setVersion(templateDeploy.getVersion());
			template.setApiDeployId(templateDeploy.getId());
			template.setStatus("Active");
			template.setVisibility("private");
			template.setApiurl(templateDeploy.getApiurl());
			
			template.setRoute(templateDeploy.getRoute());
			template.setRouteVersion(templateDeploy.getRouteVersion());
			template.setApichannel(templateDeploy.getApichannel());
			
			template.setOwner(owner);
			template.setModifier(operator);
			template.setModifyDate(new Date());
			
			apiTemplateService.save(template);	
		}
		
		return templateDeploy;
	}
	
}
