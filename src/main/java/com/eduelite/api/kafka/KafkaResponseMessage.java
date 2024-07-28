package com.eduelite.api.kafka;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KafkaResponseMessage {
	
	private String requestId;
	private Object response;
	private Long starttime;
	private Long endtime;
	private String status;
	private String message;
	private String command;
	
}
