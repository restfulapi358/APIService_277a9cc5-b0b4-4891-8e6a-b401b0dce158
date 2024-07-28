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

@Document(collection = "edueliteApiTemplateDeploy")
@Getter 
@Setter 
public class ApiTemplateDeploy extends EducationObject{
	
	private String templateId;  //it's save as developId
	private String apimethod;
	private String keyword;
	private String apiurl;
	private String apiprotocol;
	private String apichannel;
	private LinkedHashMap requestAttributes;	
	private LinkedHashMap responseAttributes;
	private List<DataAttribute> constants;
	
	private String anyConnectId;
	private String anyConnectDeployId;
	
	private String route;
	private String routeVersion;
	
	private long maxTime;
	private long minTime;
	private long calls;
	private long totalTime;	
	private long avgTime;

	

}
