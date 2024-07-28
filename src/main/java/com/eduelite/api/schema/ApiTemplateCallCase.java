package com.eduelite.api.schema;

import java.util.LinkedHashMap;
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

@Document(collection = "edueliteApiTemplateCallCase")
@Getter 
@Setter 
public class ApiTemplateCallCase extends EducationObject{
		
	private String templateId;
	private String apiDeployId; //lastest deploy
	
	private String apimethod;
	private String apiurl;
	private String apiprotocol;	
	private LinkedHashMap requestAttributes;	
	private LinkedHashMap<String, String> constants;
	private Object request;
	
	private Object response;
	
	private String message; //if error
	
	private String sessionId;
	private Long startTime;
	private Long endTime;
	
	private long spendTime;
	
	//tag: deploy-test; api-test; component-api-test; component-call (this paid)
	
	private String apichannel; //real channel if null, then default channel from apiDeployId
	private String context; //class:task:lu:component; repo-component
	
	private String route;
	private String routeVersion;
	
	
}
