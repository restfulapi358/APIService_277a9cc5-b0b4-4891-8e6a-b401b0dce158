package com.eduelite.api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eduelite.api.repository.ApiAnyconnectDeployRepository;
import com.eduelite.api.repository.ApiTemplateDeployRepository;
import com.eduelite.api.schema.ApiAnyconnect;
import com.eduelite.api.schema.ApiAnyconnectDeploy;
import com.eduelite.api.schema.ApiTemplateDeploy;
import com.eduelite.common.service.EducationObjectService;

@Service
public class ApiAnyConnectDeployService extends EducationObjectService<ApiAnyconnectDeploy>{
	
	public List<ApiAnyconnectDeploy> findDeploysByAnyConnectId(String anyConnectId){
		return ((ApiAnyconnectDeployRepository)this.documentRepository).findDeploysByAnyConnectId(anyConnectId);
	}
	
	public List<ApiAnyconnectDeploy> findDeploysByRefId(String refId){
		return ((ApiAnyconnectDeployRepository)this.documentRepository).findDeploysByRefId(refId);
	}
	
}
