package com.eduelite.api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eduelite.api.repository.ApiTemplateCallCaseRepository;
import com.eduelite.api.schema.ApiTemplate;
import com.eduelite.api.schema.ApiTemplateCallCase;
import com.eduelite.common.service.EducationObjectService;


@Service
public class ApiTemplateCallCaseService extends EducationObjectService<ApiTemplateCallCase>{
	
	
	public List<ApiTemplateCallCase> findAllByTemplateIdInSession(String sessionId, String templteId){
		
		return  ((ApiTemplateCallCaseRepository)this.documentRepository).findAllByTemplateIdInSession(sessionId, templteId);
	}
	
}
