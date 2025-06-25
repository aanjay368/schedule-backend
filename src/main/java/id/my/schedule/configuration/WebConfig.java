package id.my.schedule.configuration;

import id.my.schedule.interceptor.DeveloperInterceptor;
import id.my.schedule.interceptor.UserInterceptor;
import id.my.schedule.resolver.UserArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private UserInterceptor userInterceptor;

    @Autowired
    private DeveloperInterceptor developerInterceptor;

    @Autowired
    private UserArgumentResolver userArgumentResolver;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/api/v1/users/**")
                .addPathPatterns("/api/v1/employees/**")
                .addPathPatterns("/api/v1/employees")
                .addPathPatterns("/api/v1/schedules")
                .addPathPatterns("/api/v1/schedules/**")
                .addPathPatterns("/api/v1/auth/logout");
        registry.addInterceptor(developerInterceptor)
                .addPathPatterns("/api/v1/employees")
                .addPathPatterns("/api/v1/employees/**")
                .addPathPatterns("/api/v1/schedules")
                .addPathPatterns("/api/v1/schedules/**")
                .addPathPatterns("/api/v1/actuator/**")
                .addPathPatterns("/api/v1/actuator/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600 * 24) // seconds (1 jam)
                .resourceChain(true);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userArgumentResolver);
    }

}
