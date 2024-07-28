package com.eduelite.api.schema;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.eduelite.common.schema.ScoreAttribute;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.eduelite.common.schema.DataAttribute;
import com.eduelite.common.schema.EducationObject;

import lombok.Getter;
import lombok.Setter;

/*
 * 
 * 
 * 
 * 
 * 
 */

@Document(collection = "edueliteApiTemplate")
@Getter 
@Setter 
public class ApiTemplate extends EducationObject{
		
	private String keyword;
	
	private String apichannel; 
	
	private String apiDeployId; //lastest deploy
	private String apiprotocol;
	private String apiurl;
	
	
	private String anyConnectId;
	private String anyConnectDeployId;
	private String route;   //id:version
	private String routeVersion;
	
	
	private long maxTime;
	private long minTime;
	private long calls;
	private long totalTime;	
	private long avgTime;

}
