package org.example.expert.config;

import lombok.RequiredArgsConstructor;
import org.example.expert.interceptor.AdminApiLoggingInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AdminApiLoggingInterceptor adminApiLoggingInterceptor;

    // ArgumentResolver 등록
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AuthUserArgumentResolver());
    }

    //어드민 관리자만 접근 가능한 API 경로에 인터셉터 추가
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(adminApiLoggingInterceptor)
                .addPathPatterns("/admin/**");
    }
}
