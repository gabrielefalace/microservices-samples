package com.lingua.client.controller;

import static java.lang.String.format;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class LinguaClientController {

	@Autowired
	private LoadBalancerClient ribbonClient;

	@Autowired
	private RestTemplate restTemplate;

	@RequestMapping("/")
	public String checkHealth() {
		ServiceInstance instance = ribbonClient.choose("lingua");
		String result = "NOT FOUND";
		if (instance != null) {
			URI serviceUri = URI.create(format("http://%s:%s/health/", instance.getHost(), instance.getPort()));
			HttpEntity<?> response = restTemplate.getForEntity(serviceUri, HttpEntity.class);
			ObjectMapper mapper = new ObjectMapper();
			try {
				result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response.getBody());
			} catch (JsonProcessingException e) {
				result = "IMPOSSIBLE TO PARSE JSON";
			}
		}
		return result;
	}

}
