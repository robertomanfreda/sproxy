package com.robertoman.sproxy.aop.logging;

import com.robertoman.sproxy.util.Extractor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@Order(0)
@Slf4j
public class LoggingHandler {

    private final String LOGS_SEPARATOR = "---------- SProxy received a new request ----------";
    private HttpServletRequest httpServletRequest;

    @Pointcut("execution(@com.robertoman.sproxy.annotation.Logging * *.*(..))")
    public void annotatedMethods() {
    }

    @Before(value = "annotatedMethods()")
    public void logBefore(JoinPoint jp) {
        log.debug(LOGS_SEPARATOR);
        log.debug("The [{}] method has been called", jp.getSignature().getName());
        log.debug("Received [{}] request. Using [{}] protocol",
                httpServletRequest.getMethod(), httpServletRequest.getProtocol()
        );
        log.debug("The requested URL is [{}]", httpServletRequest.getRequestURL());
        log.debug("The requested URI is [{}]", Extractor.extractEntityUrl(httpServletRequest));
        log.debug("The received HTTP headers are [{}]", Extractor.extractHttpHeaders(httpServletRequest).toString());
        log.debug("The received HTTP query parameters are [{}]",
                Extractor.extractQueryParameters(httpServletRequest).toString()
        );
        log.debug("The received HTTP body parameters are [{}]",
                Extractor.extractFormParameters(httpServletRequest).toString()
        );
    }

    /*@After(value = "annotatedMethods()")
    public void logAfter(JoinPoint jp) {
        log.debug("Request has been processed.");
    }*/

    @Autowired
    public void setHttpServletRequest(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

}
