package com.bank.DigitalBank.Utils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Aspect
public class LoggingAspect{

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerMethods(){
    }

    @Before("controllerMethods()")
    public void logBefore(JoinPoint joinPoint){
        log.info("➡️ Entering method: {} with args: {}",
                joinPoint.getSignature().toShortString(),
                Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("✅ Exiting method: {} with result: {}",
                joinPoint.getSignature().toShortString(),
                result);
    }

    @Around("controllerMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object returnValue = joinPoint.proceed(); // calls the actual controller

        long timeTaken = System.currentTimeMillis() - start;
        log.info("⏱ Executed method: {} in {} ms",
                joinPoint.getSignature().toShortString(),
                timeTaken);

        return returnValue;
    }
    }



