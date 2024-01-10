package ru.ochkasovap.homeAccountingRest.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.exceptions.JWTVerificationException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.ochkasovap.homeAccountingRest.services.UserDetailsServiceImpl;


@Component
public class JWTFilter extends OncePerRequestFilter {
	private final JWTUtil jwtUtil;
	private final UserDetailsServiceImpl detailsServiceImpl;

	@Autowired
	public JWTFilter(JWTUtil jwtUtil, UserDetailsServiceImpl detailsServiceImpl) {
		super();
		this.jwtUtil = jwtUtil;
		this.detailsServiceImpl = detailsServiceImpl;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");// Так принято именовать заголовок с токеном. Значение -
																// 'Bearer ${token}'
		if (authHeader != null && !authHeader.isBlank() && authHeader.startsWith("Bearer ")) {
			String jwt = authHeader.substring(7); // Токен начинается с 7 символа в заголовке "Authorization" в запросе
			if (jwt.isBlank()) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JWT Token in Bearer Header");
			} else {
				try {
					String username = jwtUtil.validateTokenAndRetrieveUsernameClaim(jwt);
					UserDetails userDetails = detailsServiceImpl.loadUserByUsername(username);
					UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
							userDetails, userDetails.getPassword(), userDetails.getAuthorities());//Обязательно использовать конструктор с 3 аргументами, так как в нем setAuthenticated(true)
					if (SecurityContextHolder.getContext().getAuthentication() == null) {
						SecurityContextHolder.getContext().setAuthentication(authenticationToken);
					}
				} catch (JWTVerificationException ex) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JWT Token");
				}
			}
		}
		filterChain.doFilter(request, response);
	}

}
