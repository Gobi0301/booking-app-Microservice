package com.booking.ms.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.method.AuthorizationInterceptorsOrder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.booking.ms.interceptor.AuthorizationInterceptor;

import java.util.List;


@Configuration
public class WebMvcConfig implements WebMvcConfigurer{

	
	@Autowired
	private AuthorizationInterceptor authorizationInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		List<String> excludePatterns = List.of();
		
		List<String> pathPatterns = List.of("/api/**");
		
		registry.addInterceptor(authorizationInterceptor).addPathPatterns(pathPatterns)
		.excludePathPatterns(excludePatterns);
	}
	
}
