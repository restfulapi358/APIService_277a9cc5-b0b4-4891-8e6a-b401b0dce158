package com.eduelite.api.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.eduelite.api.business.ApiProxyService;
import com.eduelite.api.kafka.KafkaCompleteFutureManager;
import com.eduelite.api.kafka.KafkaProducer;
import com.eduelite.api.kafka.KafkaRequestMessage;
import com.eduelite.api.schema.ApiAnyconnect;
import com.eduelite.api.schema.ApiTemplate;
import com.eduelite.api.schema.ApiTemplateCallCase;
import com.eduelite.api.schema.ApiTemplateDeploy;
import com.eduelite.api.schema.ApiTemplateDevelop;
import com.eduelite.api.service.ApiTemplateCallCaseService;
import com.eduelite.api.service.ApiTemplateDeployService;
import com.eduelite.api.service.ApiTemplateService;
import com.eduelite.common.controller.EducationObjectController;
import com.eduelite.common.schema.ScoreAttribute;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v3/edu/ApiTemplateCallCase")
@CrossOrigin(allowedHeaders = "*")
public class ApiTemplateCallCaseController extends EducationObjectController<ApiTemplateCallCase> {
	
	@Autowired
	private ApiTemplateCallCaseService apiTemplateCallCaseService;

	
	@Autowired
	private ApiTemplateService apiTemplateService;
	
	@Autowired
	private ApiTemplateDeployService apiTemplateDeployService;
	
	
	@Autowired
	private ApiProxyService apiProxyService;
	
	@Autowired
	private KafkaProducer kafkaProducer;
	
	@Autowired
	private KafkaCompleteFutureManager kafkaCompleteFutureManager;
	
	@GetMapping(path = "/findReactiveCallCasesByTemplateIdInSession/{sessionId}/{templateId}", produces="application/json")
	public ResponseEntity<?> findReactiveComponentsByTemplateIdInSession(@PathVariable String sessionId,@PathVariable String templateId) {
		
		return new ResponseEntity(apiTemplateCallCaseService.findAllByTemplateIdInSession(sessionId, templateId), HttpStatus.OK) ;	
	}
	
	
	/*
	 * @Getter
		@Setter
		public class ReactiveComponent {
			
			private ComponentRuntime runtime;
			
			private UiComponent component;
			
			private Object constants; //get the constans from template
			
			private ScoreAttribute scoreAttribute;
			
			private Object templateDeploy;
			
		}
	 */	
	//transfor reactiveComponent to apiTemplateCallcase
		//@PostMapping(path = "/apiCall", produces="application/json")
		//public Mono<Object>  apiCall(@RequestBody ApiTemplateCallCase apiCallcase) {	
		/*
		 * this.apiCallcase = {
	          sessionId: 'TESTSESSION',
	          tag:'api-test',
	          templateId: this.template?this.template.id:null,
	          apiDeployId: this.templateDeploy.id,
	          request: this.reactiveComponent.request,
	          response: null,
	          message: '',
	          startTime:  performance.now(),
	          endTime: 0,
	          apimethod: this.templateDeploy.apimethod,
	          apiurl:this.templateDeploy.apiurl,
	          apiprotocol:	this.templateDeploy.apiprotocol,
	          requestAttributes: this.templateDeploy.requestAttributes,
	          constants: null,
	    //tag: deploy-test; api-test; component-api-test; component-call (this paid)    
	          route:this.templateDeploy.route,
	          routeVersion: this.templateDeploy.routeVersion,
	          apichannel: this.apichannel==='default'?this.templateDeploy.apichannel:this.apichannel, //real channel if null, then default channel from apiDeployId
	          context: 'deploy-test' //class:task:lu:component; repo-component
	        }
	  
		 * 
		 */
	@PostMapping(path = "/apiCallByReactiveComponent/{apiTemplateId}", produces="application/json")
	public Mono<Object>  apiCallByReactiveComponent(@PathVariable String apiTemplateId, @RequestBody LinkedHashMap request) {		
		
		//transfer reactive component to ApiTemplateCallcase
		ApiTemplateCallCase apiCallcase = new ApiTemplateCallCase();
		
		
		
		//call the case
		
		return Mono.just(null);
		
	}
	
	
	
	
	@PostMapping(path = "/apiCall", produces="application/json")
	public Mono<Object>  apiCall(@RequestBody ApiTemplateCallCase apiCallcase) {		
		
		String authorized = MDC.get("AUTHORIZED");
		String owner = MDC.get("OWNER");
		String operator = MDC.get("OPERATOR");
		String role = MDC.get("ROLE");
		
				
		apiCallcase.setId(UUID.randomUUID().toString());
		apiCallcase.setOwner(owner);
		apiCallcase.setModifier(operator);
		apiCallcase.setStartTime(new Date().getTime());		
		apiCallcase.setCreatedate(new Date());
		
		String request_topic = null;
		if(apiCallcase.getApiprotocol().equals("https://")) {
			//test purpuse
			//apiprotocol = "http://";
			request_topic = "api_http_request_topic";
			//return this.apiProxyService.BuilderProxy(apiprotocol, (String)develop.get("apiurl"), (String)develop.get("aipmethod"),(LinkedHashMap) develop.get("requestAttributes"), constants, (LinkedHashMap)  developRequest.get("request")) ;
		}
		else {
			
			request_topic = "channel"+ apiCallcase.getApichannel().replace(":","").replace("//", "").replace("channel", "");
		}
		//kafka
		try {
			
			//save callcase:			
			apiTemplateCallCaseService.save(apiCallcase);
			
			KafkaRequestMessage requestMessage =kafkaProducer.sendRequest(
					request_topic,
					apiCallcase
					);
			CompletableFuture<Object> future = new CompletableFuture<>();
			
			kafkaCompleteFutureManager.addRequestFuture(requestMessage.getRequestId(), future );
			
			try {
				Object response = future.get(10, TimeUnit.SECONDS);
				
				//update apiCallcase
				apiCallcase.setResponse(response);
				apiCallcase.setEndTime(new Date().getTime());
				apiCallcase.setModifyDate(new Date());
				apiCallcase.setSpendTime(apiCallcase.getEndTime()-apiCallcase.getStartTime());
				apiCallcase.setStatus("OK");
				apiTemplateCallCaseService.save(apiCallcase);
				
				
				if(apiCallcase.getTemplateId()!=null) {
					
					ApiTemplate apiTemplate = apiTemplateService.findById(apiCallcase.getTemplateId()).get();
					if(apiTemplate.getMaxTime()<apiCallcase.getSpendTime()) {
						apiTemplate.setMaxTime(apiCallcase.getSpendTime());
					}
					if(apiTemplate.getMinTime()==0 || apiTemplate.getMinTime()>apiCallcase.getSpendTime()) {
						apiTemplate.setMinTime(apiCallcase.getSpendTime());
					}
					apiTemplate.setTotalTime(apiTemplate.getTotalTime()+ apiCallcase.getSpendTime());
					apiTemplate.setCalls(apiTemplate.getCalls()+1);
					apiTemplate.setAvgTime(Math.floorDiv(apiTemplate.getTotalTime(),apiTemplate.getCalls()));
					apiTemplateService.save(apiTemplate);
				}
				if(apiCallcase.getApiDeployId()!=null) {
					
					ApiTemplateDeploy deploy = apiTemplateDeployService.findById(apiCallcase.getApiDeployId()).get();
					if(deploy.getMaxTime()<apiCallcase.getSpendTime()) {
						deploy.setMaxTime(apiCallcase.getSpendTime());
					}
					
					if(deploy.getMinTime()==0 || deploy.getMinTime()>apiCallcase.getSpendTime()) {
						deploy.setMinTime(apiCallcase.getSpendTime());
					}
					deploy.setTotalTime(deploy.getTotalTime()+ apiCallcase.getSpendTime());
					deploy.setCalls(deploy.getCalls()+1);
					deploy.setAvgTime(Math.floorDiv(deploy.getTotalTime(),deploy.getCalls()));
					apiTemplateDeployService.save(deploy);
				}				
				
				return Mono.just(ResponseEntity.ok(response));
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				kafkaCompleteFutureManager.removeRequestFuture(requestMessage.getRequestId());
			
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();				
				kafkaCompleteFutureManager.removeRequestFuture(requestMessage.getRequestId());
			
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				kafkaCompleteFutureManager.removeRequestFuture(requestMessage.getRequestId());
				
			}
			
			apiCallcase.setEndTime(new Date().getTime());
			apiCallcase.setModifyDate(new Date());
			apiCallcase.setSpendTime(apiCallcase.getEndTime()-apiCallcase.getStartTime());
			apiCallcase.setStatus("Pre-Failed");
			apiTemplateCallCaseService.save(apiCallcase);
			
			return Mono.just(ResponseEntity.badRequest().body("{\"response\":\"Failed\"}"));
			
			
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
			
		
		
	}
	
	
	@PostMapping(path = "/apideploy/{id}", produces="application/json")
	public Mono<Object>  apiProxy(@PathVariable String id, @RequestBody LinkedHashMap request) {			
		
		request.forEach((key, value) -> {
	        System.out.println(String.format(
	          "Header '%s' = %s", key, value));
	    });
		
		return this.apiProxyService.proxy(id, request) ;
		
	}
	
		
	@PostMapping(path = "/apidevelop", produces="application/json")
	public Mono<Object>  apiProxyDevelop(@RequestBody LinkedHashMap developRequest) {			
		
		developRequest.forEach((key, value) -> {
	        System.out.println(String.format(
	          "Header '%s' = %s", key, value));
	    });
		
		LinkedHashMap develop = (LinkedHashMap) developRequest.get("develop");
		ApiAnyconnect anyConnect = (ApiAnyconnect) developRequest.get("anyConnect");
		if(anyConnect==null) {
			anyConnect = new ApiAnyconnect(); //new empty instance
		}
		
		String apiprotocol = "";
		if(develop.containsKey("apiprotocol")) {
			apiprotocol= (String)develop.get("apiprotocol");
			if(apiprotocol==null) {
				apiprotocol="";
			}
		}
	
		System.out.println(String.format(
		          "apiprotocol %s", apiprotocol));
		
		LinkedHashMap<String, String> constants = new LinkedHashMap<>();
		ArrayList<LinkedHashMap> dev_constants = (ArrayList) develop.get("constants");
		if( dev_constants != null ) {
			
			dev_constants.forEach(attribute->{
				String name = (String) attribute.get("name");
				String value = (String) attribute.get("value");
				constants.put("{"+name+"}", value);
			});
		}
		
		String request_topic = null;
		if(apiprotocol.equals("https://")) {
			//test purpuse
			//apiprotocol = "http://";
			request_topic = "api_http_request_topic";
			//return this.apiProxyService.BuilderProxy(apiprotocol, (String)develop.get("apiurl"), (String)develop.get("aipmethod"),(LinkedHashMap) develop.get("requestAttributes"), constants, (LinkedHashMap)  developRequest.get("request")) ;
		}
		else {
			
			request_topic = "channel"+ apiprotocol.replace(":","").replace("//", "").replace("channel", "");
		}
		//kafka
		try {
			
			//save callcase:
			
			
			
			KafkaRequestMessage requestMessage =kafkaProducer.sendRequest(
					request_topic,
					(String)develop.get("aipmethod"), apiprotocol, (String)develop.get("apiurl"), 
					(LinkedHashMap) develop.get("requestAttributes") , constants,  developRequest.get("request"),
					anyConnect.getId(), anyConnect.getVersion(), anyConnect.getDeployId()
					);
			CompletableFuture<Object> future = new CompletableFuture<>();
			
			kafkaCompleteFutureManager.addRequestFuture(requestMessage.getRequestId(), future );
			
			try {
				Object response = future.get(10, TimeUnit.SECONDS);
				
				//
				
				return Mono.just(ResponseEntity.ok(response));
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				kafkaCompleteFutureManager.removeRequestFuture(requestMessage.getRequestId());
			
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();				
				kafkaCompleteFutureManager.removeRequestFuture(requestMessage.getRequestId());
			
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				kafkaCompleteFutureManager.removeRequestFuture(requestMessage.getRequestId());
				
			}
			
			return Mono.just(ResponseEntity.badRequest().body("{\"response\":\"Failed\"}"));
			
			
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
			
		
		
	}
	
	/*
	 * @PostMapping(path = "/apideploy", produces="application/json") public
	 * Mono<Object> apiProxyDeploy(@RequestBody LinkedHashMap developRequest) {
	 * 
	 * developRequest.forEach((key, value) -> { System.out.println(String.format(
	 * "Header '%s' = %s", key, value)); });
	 * 
	 * LinkedHashMap deploy = (LinkedHashMap) developRequest.get("develop");
	 * 
	 * LinkedHashMap<String, String> constants = new LinkedHashMap<>();
	 * ArrayList<LinkedHashMap> dev_constants = (ArrayList) deploy.get("constants");
	 * if( dev_constants != null ) {
	 * 
	 * dev_constants.forEach(attribute->{ String name = (String)
	 * attribute.get("name"); String value = (String) attribute.get("value");
	 * constants.put("{"+name+"}", value); }); }
	 * 
	 * String apiprotocol = ""; if(deploy.containsKey("apiprotocol")) { apiprotocol=
	 * (String)deploy.get("apiprotocol"); if(apiprotocol==null) { apiprotocol=""; }
	 * }
	 * 
	 * if(apiprotocol.equals("https://")) {
	 * 
	 * 
	 * return this.apiProxyService.BuilderProxy(apiprotocol,
	 * (String)deploy.get("apiurl"), (String)deploy.get("aipmethod"),(LinkedHashMap)
	 * deploy.get("requestAttributes"), constants, (LinkedHashMap)
	 * developRequest.get("request")) ; } else { //kafka try {
	 * kafkaProducer.sendRequest(apiprotocol.replace(":","").replace("//", ""),
	 * (String)deploy.get("aipmethod"), apiprotocol, (String)deploy.get("apiurl"),
	 * (LinkedHashMap) deploy.get("requestAttributes"), constants,
	 * developRequest.get("request")); } catch (JsonProcessingException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } return null;
	 * 
	 * }
	 * 
	 * 
	 * 
	 * }
	 */
}
