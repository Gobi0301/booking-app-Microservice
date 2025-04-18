package com.booking.ms.util;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtToken {

	@Value("${application.security.jwt-token.secret: 5970337336763979244226452948404D62516554}")
	private String SECRET;
	
	private final Integer VALIDITY_MINUTES = 120;
	
	public String token(String email) {
		
		var issuedDate = Instant.now();
		byte[] keyBytes = SECRET.getBytes();
		Key signInKey = Keys.hmacShaKeyFor(keyBytes);
		
		return Jwts.builder().setSubject(email).claim("email", email).setIssuedAt(Date.from(issuedDate))
				.setExpiration(Date.from(issuedDate.plus(VALIDITY_MINUTES,ChronoUnit.MINUTES)))
				.signWith(signInKey,SignatureAlgorithm.HS256).compact();
		
	}
	
	public String tokenUser(String token) {
		byte[] keyBytes = SECRET.getBytes();
		Key signInKey = Keys.hmacShaKeyFor(keyBytes);
		
		
		return((Claims) Jwts.parserBuilder().setSigningKey(signInKey).build().parse(token).getBody())
				.get("email",String.class);
	}
}
