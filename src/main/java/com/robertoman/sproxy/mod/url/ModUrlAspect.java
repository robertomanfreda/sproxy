package com.robertoman.sproxy.mod.url;

import com.robertoman.sproxy.exception.IllegalUrlException;
import com.robertoman.sproxy.util.Extractor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Aspect
@Component
@ConditionalOnProperty(value = "config.mod.url.enabled", havingValue = "true")
@ConfigurationProperties(prefix = "config.mod.url")
// @Order(0) is for com/robertoman/sproxy/aop/logging/LoggingHandler.java
// @Order(1) is for com/robertoman/sproxy/mod.security.aop/ModSecurityAspect.java
@NoArgsConstructor
@Order(2)
@Slf4j
public class ModUrlAspect {

    private final List<Pattern> whitePatterns = new ArrayList<>();
    private final List<Pattern> blackPatterns = new ArrayList<>();

    private HttpServletRequest httpServletRequest;

    @Setter
    private List<String> whitelist;

    @Setter
    private List<String> blacklist;


    private enum TypeList {
        WHITELIST, BLACKLIST
    }

    @PostConstruct
    private void configureLists() {
        log.debug("---------- MOD URL ----------");
        makeList(whitelist, whitePatterns, TypeList.WHITELIST);
        makeList(blacklist, blackPatterns, TypeList.BLACKLIST);
    }

    @Pointcut("execution(@com.robertoman.sproxy.annotation.ModUrl * *.*(..))")
    private void annotatedMethods() {
    }

    @Before(value = "annotatedMethods()")
    public void filterUrls(JoinPoint jp) throws IllegalUrlException {
        String matchedRule;
        String requestedUrl = Extractor.extractEntityUrl(httpServletRequest);

        log.debug("Checking legality of requested url [{}] through the whitelist", requestedUrl);
        for (Pattern pattern : whitePatterns) {
            if (pattern.matcher(requestedUrl).find()) {
                matchedRule = pattern.pattern();
                log.debug("The requested url [{}] matches the whitelist rule [{}]", requestedUrl, matchedRule);
                return;
            }
        }

        log.debug("Checking legality of requested url [{}] through the blacklist", requestedUrl);
        for (Pattern pattern : blackPatterns) {
            if (pattern.matcher(requestedUrl).find()) {
                matchedRule = pattern.pattern();
                log.debug("The requested url [{}] matches the blacklist rule [{}]", requestedUrl, matchedRule);
                throw new IllegalUrlException("MOD URL is active and the requested url [" + requestedUrl + "] " +
                        "was blacklisted using the regex [" + matchedRule + "].");
            }
        }

        throw new IllegalUrlException("The requested url [" + requestedUrl + "] doesn't match any rule.");
    }

    @Autowired
    public void setHttpServletRequest(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    private void makeList(List<String> regexes, List<Pattern> patterns, TypeList typeList) {
        if (null != regexes && regexes.size() > 0) {
            log.debug("{} is enabled. Doing configuration...", typeList);
            regexes.forEach(regex -> patterns.add(Pattern.compile(regex)));
            log.debug("Urls in {} are [{}]", typeList, regexes);
        } else {
            log.debug("{} is disabled.", typeList);
        }
    }
}