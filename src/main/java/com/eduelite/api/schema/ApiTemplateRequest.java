package com.eduelite.api.schema;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.eduelite.common.schema.ScoreAttribute;
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

@Document(collection = "edueliteApiTemplateRequest")
@Getter 
@Setter 
public class ApiTemplateRequest extends EducationObject{
	
	private String requirement;
	private Object request;
	private Object response;
	private List<String> uploadfiles;
	private String figmaUrl;

}
