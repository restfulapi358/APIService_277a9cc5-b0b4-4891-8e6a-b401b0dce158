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

import com.eduelite.api.schema.ApiTemplate;
import com.eduelite.api.schema.ApiTemplateRequest;
import com.eduelite.common.controller.EducationObjectController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v3/edu/ApiTemplateRequest")
@CrossOrigin(allowedHeaders = "*")
public class ApiTemplateRequestController extends EducationObjectController<ApiTemplateRequest> {
	
	
	
	
}
