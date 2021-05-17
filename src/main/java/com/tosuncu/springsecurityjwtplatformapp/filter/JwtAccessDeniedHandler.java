package com.tosuncu.springsecurityjwtplatformapp.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tosuncu.springsecurityjwtplatformapp.domain.HttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

import static com.tosuncu.springsecurityjwtplatformapp.constant.SecurityConstant.ACCES_DENIED_MESSAGE;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        HttpResponse httpResponse = new HttpResponse(UNAUTHORIZED.value(), UNAUTHORIZED, UNAUTHORIZED.getReasonPhrase().toUpperCase(), ACCES_DENIED_MESSAGE);
        httpServletResponse.setContentType(APPLICATION_JSON_VALUE);
        httpServletResponse.setStatus(UNAUTHORIZED.value());
        OutputStream outputStream =httpServletResponse.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(outputStream,httpResponse);
        outputStream.flush();
    }
}
