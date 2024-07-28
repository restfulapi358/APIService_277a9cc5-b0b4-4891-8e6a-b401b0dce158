package com.eduelite.api.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Component;

import com.eduelite.api.schema.ApiTemplate;
import com.eduelite.api.schema.ApiTemplateDeploy;
import com.eduelite.common.repository.EducationObjectRepository;


public interface ApiTemplateDeployRepository extends EducationObjectRepository<ApiTemplateDeploy> {
	
	@Query(value="{'templateId' : ?0 }")
    List<ApiTemplateDeploy> findDeploysByTemplateId(String templateId);
}
