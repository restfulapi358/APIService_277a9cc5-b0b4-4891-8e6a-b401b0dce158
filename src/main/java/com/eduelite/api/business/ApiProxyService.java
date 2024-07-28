package com.eduelite.api.business;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriBuilder;

import com.eduelite.api.kafka.KafkaCompleteFutureManager;
import com.eduelite.api.kafka.KafkaProducer;
import com.eduelite.api.kafka.KafkaRequestMessage;
import com.eduelite.api.schema.ApiAnyconnect;
import com.eduelite.api.schema.ApiTemplate;
import com.eduelite.api.schema.ApiTemplateDeploy;
import com.eduelite.api.schema.ApiTemplateDevelop;
import com.eduelite.api.service.ApiAnyconnectService;
import com.eduelite.api.service.ApiTemplateDeployService;
import com.eduelite.api.service.ApiTemplateService;
import com.eduelite.common.schema.DataAttribute;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

	

@Service
public class ApiProxyService{

	@Autowired
	private ApiTemplateService apiTemplateService;
	
	@Autowired
	private ApiTemplateDeployService apiTemplateDeployService;
		
	@Autowired
	private KafkaProducer kafkaProducer;
	
	
	@Autowired
	private ApiAnyconnectService apiAnyconnectService;
	
	@Autowired
	private KafkaCompleteFutureManager kafkaCompleteFutureManager;
		
	
	public Mono<Object> proxy(String id, LinkedHashMap request){
		
		
		Optional<ApiTemplateDeploy> deployOpt = apiTemplateDeployService.findById(id);
		if( deployOpt.isPresent() ) {
			
			ApiTemplateDeploy deploy = deployOpt.get();
			
			LinkedHashMap<String, String> constants = new LinkedHashMap<>();
			if(deploy.getConstants()!=null) {
				
				deploy.getConstants().forEach(attribute->{
					String name = attribute.getName();
					String value = (String) attribute.getValue();
					constants.put("{"+name+"}", value);
				});
				
			}
			
			ApiAnyconnect anyConnect = apiAnyconnectService.findById(deploy.getAnyConnectId()).get();
			
			String apiProtocol = deploy.getApiprotocol(); 
			if(apiProtocol!=null) {
				
				if(apiProtocol.equals("https://")) {
					return BuilderProxy(apiProtocol, deploy.getApiurl(), deploy.getApimethod(), deploy.getRequestAttributes(), constants, request);					
				}
				else {
					
					//kafka
					try {
						
						KafkaRequestMessage requestMessage = kafkaProducer.sendRequest(
							"channel" + apiProtocol.replace(":","").replace("//", "").replace("channel", ""), 
							deploy.getApimethod(), apiProtocol, deploy.getApiurl(), 
							deploy.getRequestAttributes(), constants,  request,
							anyConnect.getId(), anyConnect.getVersion(), anyConnect.getDeployId());
						CompletableFuture<Object> future = new CompletableFuture<>();
						
						kafkaCompleteFutureManager.addRequestFuture(requestMessage.getRequestId(), future );
						
						try {
							Object response = future.get(2, TimeUnit.SECONDS);
							
							return Mono.just(ResponseEntity.ok(response));
							
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (TimeoutException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						return Mono.just(ResponseEntity.badRequest().body("{\"response\":\"Failed\"}"));
						
					} catch (JsonProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			}
			else {
				//match the old apis without protocol
				return BuilderProxy("", deploy.getApiurl(), deploy.getApimethod(), deploy.getRequestAttributes(), constants, request);
			}
			
		}
		return Mono.just(ResponseEntity.badRequest().body("Failed"));
			
		
	}
	
	
	
	private UriBuilder UriBuilderParams(UriBuilder uriBuilder, URL url, LinkedHashMap<String,String> paramVariableMap ) {
		
		uriBuilder.path(url.getPath());
	  	
		paramVariableMap.forEach(
		    (key, value) ->{
		    	
		    	uriBuilder.queryParam(key, value);
		    	
		    }
		);
		return uriBuilder;
		
	}
	
	public Mono<Object> BuilderProxy(String apiProtocol,  String apiUrl, String apiMethod, LinkedHashMap requestAttributes, LinkedHashMap constants,  LinkedHashMap request){
		
		
		try {
			
			String api_url = apiProtocol  + apiUrl;
			
			URL url;
			url = new URL(api_url);
			
			System.out.println(url.getProtocol());
			System.out.println(url.getPath());
			
			ArrayList pathVariableArray = new ArrayList();
			LinkedHashMap<String,String> paramVariableMap = new LinkedHashMap<>();
			LinkedHashMap<String,Object> requestBodyMap = new LinkedHashMap<>();
			LinkedHashMap<String,String> headerVariableMap = new LinkedHashMap<>();
						 
			((List<LinkedHashMap>)requestAttributes.get("schema")).forEach(
					
				(attribute) ->{					
								
					String options = (String) attribute.get("options");
					String name = (String) attribute.get("name");
					String value;
					
					if(options!=null) {
						switch(options) {
						
						case "Path Vairable":
							value = (String)request.get(name);
							if( constants.containsKey(value)) {
								value = (String)constants.get(value);
							}
							pathVariableArray.add(value);
							break;
						case "Request Parameter":
							value = request.get(name).toString();
							if( constants.containsKey(value)) {
								value = (String)constants.get(value);
							}
							paramVariableMap.put(name,value);
							break;
						case "Header Parameter":
							value = request.get(name).toString();
							if( constants.containsKey(value)) {
								value = (String)constants.get(value);
							}
							headerVariableMap.put(name, value);
							break;
						case "Request Body":
							requestBodyMap.put(name, request.get(name));
							break;							
						}
					}
				}
			);	
			
			Consumer<HttpHeaders> headersConsumer = headers -> {
	           
	            headerVariableMap.forEach((key,value)->{
	            	
	            	System.out.println(key + ":" + value);
	            	headers.set(key, value);
	            });
	            
	        };
	        
	        
	        
	        WebClient.Builder webClientBuilder = WebClient.builder();
	        webClientBuilder.defaultHeaders(headers -> headersConsumer.accept(headers));	
	        webClientBuilder.baseUrl(url.getProtocol() + "://" +  url.getHost() + ":" + url.getPort());
	        WebClient webClient = webClientBuilder.build();
	        	        
			return webClient					  
					  .method(HttpMethod.GET)	
		              .uri(uriBuilder-> UriBuilderParams(uriBuilder, url, paramVariableMap)
		              			.build(pathVariableArray.toArray())
		              			
		               )
		              .body(requestBodyMap.size()==0?null:BodyInserters.fromValue(requestBodyMap))
		              .retrieve()
		              .bodyToMono(Object.class)
		              .timeout(Duration.ofSeconds(2));		
		              
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	
}
