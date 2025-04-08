package com.booking.ms.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

import com.booking.ms.error.NoBearerTokenError;
import com.booking.ms.util.JwtToken;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthorizationInterceptor implements HandlerInterceptor {

	@Autowired
	private JwtToken jwtService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,Object handler) {
		
		String authorizationHeader = request.getHeader("Authorization");
		
		if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			throw new NoBearerTokenError();
		}
		
		try {
			request.setAttribute("user", jwtService.tokenUser(authorizationHeader.substring(7)));
		}catch (SignatureException e) {
			// TODO: handle exception
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"invalid token");
		}catch (ExpiredJwtException e) {
			// TODO: handle exception
			
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"token expired");
		}
		
		return true;
	}
}
