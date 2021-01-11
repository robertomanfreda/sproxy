package com.robertoman.sproxy.aop.delay;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@ConditionalOnProperty(value = "config.mod.delayer.enabled", havingValue = "true")
@ConfigurationProperties("config.mod.delayer")
@Order(1)
@Slf4j
public class DelayerAspect {

    @Setter
    private Integer delay;

    @Pointcut("execution(@com.robertoman.sproxy.annotation.Delayed * *.*(..))")
    public void annotatedMethods() {
    }

    @Before(value = "annotatedMethods()")
    public void delayBefore(JoinPoint jp) throws Throwable {
        Thread.sleep(delay);
    }

}
