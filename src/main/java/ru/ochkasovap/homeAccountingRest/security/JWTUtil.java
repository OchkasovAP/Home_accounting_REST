package ru.ochkasovap.homeAccountingRest.security;

import java.time.ZonedDateTime;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

@Component
public class JWTUtil {
	@Value("${jwt_secret}")
	private String secret;
	
	public String generateToken(String login) {
		Date expirationDate = Date.from(ZonedDateTime.now().plusHours(12).toInstant());
		return JWT.create()
				.withSubject("User details")
				.withClaim("login", login)
				.withIssuedAt(new Date())
				.withIssuer("home_accounting")
				.withExpiresAt(expirationDate)
				.sign(Algorithm.HMAC256(secret));
	}
	
	public String validateTokenAndRetrieveUsernameClaim(String token) {
		JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
			.withSubject("User details")
			.withIssuer("home_accounting")
			.build();
		DecodedJWT jwt = verifier.verify(token);
		return jwt.getClaim("login").asString();
	}
}
