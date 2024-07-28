package com.eduelite.api.kafka;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KafkaRequestMessage {
	
	private String requestId;
	private String type;   //anyconnect|api
	private String method;
	private String protocol;
	private String url;
	private Object attributes;
	private Object constant;
	private Object request;
	private Long starttime;
	
	private String route;  //any-connect deployId
	private String routeVersion; 
	private String deployId;
	private String apichannel;
	
}
