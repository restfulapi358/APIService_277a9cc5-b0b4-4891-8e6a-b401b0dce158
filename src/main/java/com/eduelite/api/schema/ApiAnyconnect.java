package com.eduelite.api.schema;

import org.springframework.data.mongodb.core.mapping.Document;

import com.eduelite.common.schema.EducationObject;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "edueliteApiAnyconnect")
@Getter 
@Setter 
public class ApiAnyconnect extends EducationObject{
	
	//name:
	//description:
	//tag: java, python, R, javascript, others
	
	//repositoryUrl: github, gitlab,....	
	private String deployId;
	
	private String route;  
	
}
