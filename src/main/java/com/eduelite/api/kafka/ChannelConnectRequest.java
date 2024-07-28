package com.eduelite.api.kafka;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChannelConnectRequest {
		
	private String username;
	private String channelId;
	private String pin;

}
