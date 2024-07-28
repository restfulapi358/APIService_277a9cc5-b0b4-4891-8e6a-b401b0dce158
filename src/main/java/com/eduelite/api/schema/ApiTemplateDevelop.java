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

@Document(collection = "edueliteApiTemplateDevelop")
@Getter 
@Setter 
public class ApiTemplateDevelop extends EducationObject{
	
	
	private String apimethod;
	private String keyword;
	private String apiurl;
	private String apiprotocol;
	private String apichannel;
	private LinkedHashMap requestAttributes;	
	private List<DataAttribute> constants;
	private Object responseAttributes;
	private String requestId;
	
	private String anyConnectId;
	private String anyConnectDeployId;
	
	
	
	

}
