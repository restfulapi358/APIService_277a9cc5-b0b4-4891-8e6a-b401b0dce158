package com.eduelite.api.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Component;

import com.eduelite.api.schema.ApiAnyconnect;
import com.eduelite.api.schema.ApiAnyconnectDeploy;
import com.eduelite.api.schema.ApiTemplate;
import com.eduelite.api.schema.ApiTemplateDeploy;
import com.eduelite.common.repository.EducationObjectRepository;


public interface ApiAnyconnectDeployRepository extends EducationObjectRepository<ApiAnyconnectDeploy> {
	
	@Query(value="{'anyConnectId' : ?0 }")
    List<ApiAnyconnectDeploy> findDeploysByAnyConnectId(String anyConnectId);
	
	@Query(value="{'refid' : ?0 }")
    List<ApiAnyconnectDeploy> findDeploysByRefId(String refid);
	
}
