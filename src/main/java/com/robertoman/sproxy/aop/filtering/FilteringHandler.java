package com.robertoman.sproxy.aop.filtering;

import com.robertoman.sproxy.exceptions.IllegalUrlException;
import com.robertoman.sproxy.utils.Extractor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import static com.robertoman.sproxy.utils.Constants.REG_KEY_CLOSE;
import static com.robertoman.sproxy.utils.Constants.REG_KEY_START;

@Aspect
@ConditionalOnExpression("${config.filtering.enabled:true}")
@Component
@Order(2)
@Slf4j
public class FilteringHandler {

    @Value("${config.filtering.rules}")
    private List<String> filteringRules;
    private HttpServletRequest httpServletRequest;

    private List<Pattern> legalUrls = new ArrayList<>();

    @PostConstruct
    public void doConfig() {
        log.debug("STARTED Filtering urls configuration...");

        filteringRules.forEach(r -> {
            int index = r.indexOf(REG_KEY_CLOSE) + REG_KEY_CLOSE.length();

            String regex = r.substring(0, index)
                    .replace(REG_KEY_START, "")
                    .replace(REG_KEY_CLOSE, "");

            legalUrls.add(Pattern.compile(regex));
        });

        log.debug("Added the following filtering rules:");
        AtomicInteger rulesCounter = new AtomicInteger(1);

        legalUrls.forEach(rule -> {
            log.debug("Rule {}: Regex [{}]", rulesCounter.get(), rule.pattern());
            rulesCounter.getAndIncrement();
        });

        log.debug("COMPLETED Filtering urls configuration.");
    }

    @Pointcut("execution(@com.robertoman.sproxy.annotations.Filtered * *.*(..))")
    public void annotatedMethods() {
    }

    @Before(value = "annotatedMethods()")
    public void filterUrls(JoinPoint jp) throws IllegalUrlException {
        String requestedUrl = Extractor.extractEntityUrl(httpServletRequest);

        log.debug("Checking legality of requested url [{}]", requestedUrl);

        boolean match = false;
        String matchedRule = null;

        for (Pattern pattern : legalUrls) {
            if (pattern.matcher(requestedUrl).find()) {
                match = true;
                matchedRule = pattern.pattern();
                break;
            }
        }

        if (match) {
            log.debug("OK, the requested url [{}] matches the filter rule [{}]", requestedUrl, matchedRule);
            log.debug("All pre-execution configuration was executed. Processing request...");
        } else {
            log.warn("The requested url [{}}] doesn't match any filter rule.\n Stopping now...", requestedUrl);
            throw new IllegalUrlException("The requested url [" + requestedUrl + "] doesn't match any filter rule.");
        }
    }

    @Autowired
    public void setHttpServletRequest(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

}
