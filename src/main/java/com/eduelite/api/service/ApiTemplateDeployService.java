package com.eduelite.api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eduelite.api.repository.ApiTemplateDeployRepository;
import com.eduelite.api.schema.ApiTemplateDeploy;
import com.eduelite.common.service.EducationObjectService;

@Service
public class ApiTemplateDeployService extends EducationObjectService<ApiTemplateDeploy>{
	
	public List<ApiTemplateDeploy> findDeploysByTemplateId(String templateId){
		return ((ApiTemplateDeployRepository)this.documentRepository).findDeploysByTemplateId(templateId);
	}
	
}
