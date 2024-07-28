package com.eduelite.api.schema;

import org.springframework.data.mongodb.core.mapping.Document;

import com.eduelite.common.schema.EducationObject;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "edueliteComputingChannel")
@Getter 
@Setter 
public class ComputingChannel extends EducationObject{
	
	private String channelName;
	private String channelType;  //API; Java;
	
	private String topic; //kafka 
	
	private String orgId;  //if user-profile is pro, or enterprise

}
