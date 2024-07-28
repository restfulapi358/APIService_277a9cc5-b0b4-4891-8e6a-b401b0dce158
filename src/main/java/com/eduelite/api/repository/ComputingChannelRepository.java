package com.eduelite.api.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.Query;

import com.eduelite.api.schema.ComputingChannel;
import com.eduelite.common.repository.EducationObjectRepository;



public interface ComputingChannelRepository extends EducationObjectRepository<ComputingChannel> {
	
	
	@Query(value="{'orgId' : ?0}")
    List<ComputingChannel> findAllByOrgId(String orgId );
		
}
