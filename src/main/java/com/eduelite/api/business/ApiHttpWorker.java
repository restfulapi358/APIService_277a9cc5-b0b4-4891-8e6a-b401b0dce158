package com.eduelite.api.business;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiHttpWorker {
	
	
	private String workerId;
	
	private String workerUrl;  //https:/eduapi.netlify.com/.netlify/functions/hello
	
	private int maxLoad = 5;
	
	private int currentLoad;
	
	private String status="ready"; //running; exception
	
	private int calls; //how many calls
	
	private int maxtime;
	
	private int mintime;
	
	private int avgtime;
		

}
