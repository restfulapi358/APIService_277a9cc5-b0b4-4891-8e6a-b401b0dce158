package com.eduelite.api.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Component;

import com.eduelite.api.schema.ApiTemplate;
import com.eduelite.api.schema.ApiTemplateDevelop;
import com.eduelite.common.repository.EducationObjectRepository;


public interface ApiTemplateDevelopRepository extends EducationObjectRepository<ApiTemplateDevelop> {
	
	@Query(value="{'requestId' : ?0 }")
    List<ApiTemplateDevelop> findDevelosByRequestId(String requestId);
}
