package id.my.schedule.configuration;

import id.my.schedule.interceptor.DeveloperInterceptor;
import id.my.schedule.interceptor.UserInterceptor;
import id.my.schedule.resolver.UserArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;
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
                .addPathPatterns("/api/v1/divisions")
                .addPathPatterns("/api/v1/employees")
                .addPathPatterns("/api/v1/employees/**")
                .addPathPatterns("/api/v1/shifts")
                .addPathPatterns("/api/v1/users")   
                .addPathPatterns("/api/v1/divisions")
                .addPathPatterns("/api/v1/schedules")
                .addPathPatterns("/api/v1/submissions")
                .addPathPatterns("/api/v1/submissions/**")
                .addPathPatterns("/api/v1/backups/**")
                .addPathPatterns("/api/v1/auth/logout");
        registry.addInterceptor(developerInterceptor)
                .addPathPatterns("/api/v1/employees/**")
                .addPathPatterns("/api/v1/employees")
                .addPathPatterns("/api/v1/schedules")
                .addPathPatterns("/api/v1/schedules/**")
                .addPathPatterns("/actuator/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userArgumentResolver);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**") // Tangkap semua request
                .addResourceLocations("classpath:/static/") // Di folder static
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource requestedResource = location.createRelative(resourcePath);
                        return requestedResource.exists() && requestedResource.isReadable()
                                ? requestedResource
                                : location.createRelative("index.html");
                    }
                });
    }
}
