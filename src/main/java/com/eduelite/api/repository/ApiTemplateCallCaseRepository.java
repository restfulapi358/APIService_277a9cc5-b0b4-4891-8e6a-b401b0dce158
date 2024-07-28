package com.eduelite.api.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Component;

import com.eduelite.api.schema.ApiTemplate;
import com.eduelite.api.schema.ApiTemplateCallCase;
import com.eduelite.common.repository.EducationObjectRepository;


public interface ApiTemplateCallCaseRepository extends EducationObjectRepository<ApiTemplateCallCase> {
	
	@Query(value="{'sessionId' : ?0, 'templateId': ?1}")
    List<ApiTemplateCallCase> findAllByTemplateIdInSession(String sessionId, String templateId );
	
}
