package com.eduelite.api.controller;

import java.util.Date;
import java.util.List;
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

import com.eduelite.api.schema.ComputingChannel;
import com.eduelite.api.service.ComputingChannelService;
import com.eduelite.common.controller.EducationObjectController;

import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/v3/edu/ComputingChannel")
@CrossOrigin(allowedHeaders = "*")
public class ComputingChannelController extends EducationObjectController<ComputingChannel> {
	
	@GetMapping(path = "/findAllByOrgId/{orgId}", produces="application/json")
	public ResponseEntity<?> findAllByOrgId(@PathVariable String orgId) {
		
		return new ResponseEntity(((ComputingChannelService)this.eduObjService).findAllByOrgId(orgId), HttpStatus.OK) ;	
	}
	
	
	@GetMapping(path = "/health/{id}", produces="application/json")
	public Mono<Object> healthById(@PathVariable String id) {
		
		return ((ComputingChannelService)this.eduObjService).healthById(id);
	}
	
	
}
