package com.eduelite.api.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;


import com.eduelite.api.business.ApiAnyConnectProxyService;
import com.eduelite.api.repository.ComputingChannelRepository;
import com.eduelite.api.schema.ComputingChannel;
import com.eduelite.common.service.EducationObjectService;

import reactor.core.publisher.Mono;


@Service
public class ComputingChannelService extends EducationObjectService<ComputingChannel>{
	
	@Autowired
	private ApiAnyConnectProxyService apiAnyConnectProxyService;
	
	
	public List<ComputingChannel> findAllByOrgId(String orgId) {
		return  ((ComputingChannelRepository)this.documentRepository).findAllByOrgId(orgId);
	}
	
	public Mono<Object> healthById(String id) {
		
		Optional<ComputingChannel> computingChannelOpt = this.findById(id);
		
		if(computingChannelOpt.isPresent()) {
			
			ComputingChannel computingChannel = computingChannelOpt.get();
			
			LinkedHashMap commandRequest = new LinkedHashMap();
			commandRequest.put("id", UUID.randomUUID().toString());
			commandRequest.put("type", "anyconnect");
			commandRequest.put("command", "health");
			
			return this.apiAnyConnectProxyService.sendCommand("channel"+computingChannel.getId(), commandRequest);
		}
		
		return Mono.just("Failed");
	}
	

}
