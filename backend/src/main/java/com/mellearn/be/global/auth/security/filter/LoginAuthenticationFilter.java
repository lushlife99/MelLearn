package com.mellearn.be.global.auth.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mellearn.be.global.auth.dto.AuthRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

public class LoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static String LOGIN_PROCESS_URL = "/login";

    public LoginAuthenticationFilter(AuthenticationManager authenticationManager,
                                   AuthenticationSuccessHandler successHandler,
                                   AuthenticationFailureHandler failureHandler) {
        setAuthenticationManager(authenticationManager);
        setFilterProcessesUrl(LOGIN_PROCESS_URL);
        setAuthenticationSuccessHandler(successHandler);
        setAuthenticationFailureHandler(failureHandler);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            AuthRequest authRequest = mapper.readValue(request.getInputStream(), AuthRequest.class);

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(authRequest.getMemberId(), authRequest.getPassword());

            return getAuthenticationManager().authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
