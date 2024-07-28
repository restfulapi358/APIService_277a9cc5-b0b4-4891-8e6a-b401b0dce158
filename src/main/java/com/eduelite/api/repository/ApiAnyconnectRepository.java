package com.eduelite.api.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Component;

import com.eduelite.api.schema.ApiAnyconnect;
import com.eduelite.api.schema.ApiTemplate;
import com.eduelite.common.repository.EducationObjectRepository;


public interface ApiAnyconnectRepository extends EducationObjectRepository<ApiAnyconnect> {
	
	
}
