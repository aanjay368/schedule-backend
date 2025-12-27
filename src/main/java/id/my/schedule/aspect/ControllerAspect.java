package id.my.schedule.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class ControllerAspect {

//    @Around("execution(* id.my.schedule.controller..*(..))")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {

        Object result = null;
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getDeclaringType().getSimpleName() + "." + signature.getName();
        Object[] args = joinPoint.getArgs();

        try {

            if (!className.equalsIgnoreCase("CustomErrorController")) {
                log.info("\ntrying to {}({})",methodName, Arrays.toString(args));
            }

            result = joinPoint.proceed();

        } catch (Exception e) {

            e.printStackTrace();
            throw e;

        } finally {

            if (!className.equalsIgnoreCase("CustomErrorController")) {
//                log.info("\nsuccess {} result : {}", methodName, result);
            }

        }

        return result;
    }
}