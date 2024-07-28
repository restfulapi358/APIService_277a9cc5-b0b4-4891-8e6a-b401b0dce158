package com.eduelite.api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eduelite.api.repository.ApiTemplateDevelopRepository;
import com.eduelite.api.schema.ApiTemplateDevelop;
import com.eduelite.common.service.EducationObjectService;


@Service
public class ApiTemplateDevelopService extends EducationObjectService<ApiTemplateDevelop>{
	
	public List<ApiTemplateDevelop> findDeveloperByRequestId(String requestId){
		return ((ApiTemplateDevelopRepository)this.documentRepository).findDevelosByRequestId(requestId);
	}
	
}
