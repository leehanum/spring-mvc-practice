package com.nhnacademy.springmvcday03.config;

import com.nhnacademy.springmvcday03.exception.LoginCheckInterceptor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public LocaleResolver localeResolver() {
        return new CookieLocaleResolver();
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setBasename("message");
//        messageSource.setBasenames("message", "error");

        return messageSource;
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // LocaleChangeInterceptor 적용
        registry.addInterceptor(new LocaleChangeInterceptor());
        // LoginCheckInterceptor 적용
        registry.addInterceptor(new LoginCheckInterceptor())
                // /student/로 시작하는 모든 요청에 대해 인터셉터 적용
                .addPathPatterns("/student/**")
                .excludePathPatterns(
                        "/login",
                        "/"
                );
    }
}
