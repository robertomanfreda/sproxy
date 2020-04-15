package com.robertoman.sproxy.aop.security;

import com.robertoman.sproxy.exceptions.AuthorizationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Aspect
@Component
@ConditionalOnExpression("${config.security.enabled:true}")
@Slf4j
public class AuthorizationHandler {

    private HttpServletRequest httpServletRequest;

    @Pointcut("execution(@com.robertoman.sproxy.annotations.Authorized * *.*(..))")
    public void annotatedMethods() {
    }

    @Before(value = "annotatedMethods()")
    public void authorize(JoinPoint jp) throws Exception {
        String bearer = Optional.ofNullable(httpServletRequest.getHeader("SProxy-Bearer")).orElse(Strings.EMPTY);

        if (Strings.EMPTY.equals(bearer) || !bearer.equalsIgnoreCase("1234")) {
            throw new AuthorizationException("Authorization exception - missing header: \"SProxy-Bearer\"");
        }


        // TODO validate the "session"
    }

    @Autowired
    public void setHttpServletRequest(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }
}
