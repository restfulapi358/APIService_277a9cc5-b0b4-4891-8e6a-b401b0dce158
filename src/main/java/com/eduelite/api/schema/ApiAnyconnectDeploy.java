package com.eduelite.api.schema;

import org.springframework.data.mongodb.core.mapping.Document;

import com.eduelite.common.schema.EducationObject;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "edueliteApiAnyconnectDeploy")
@Getter 
@Setter 
public class ApiAnyconnectDeploy extends EducationObject{
	
	//name:
	//description:
	//tag: java, python, R, javascript, others
	
	//repositoryUrl: github, gitlab,....
	private String repository;
	private String branch;
	private String apichannel;
	private String entryMainFile;
	
	private String anyConnectId;
	private String route;
	
	
}
