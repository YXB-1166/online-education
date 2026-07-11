package com.edu.exam;

import com.edu.common.filter.JwtAuthFilter;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@MapperScan("com.edu.exam.mapper")
@SpringBootApplication(scanBasePackages = {"com.edu.exam", "com.edu.common"})
public class ExamApp {
    public static void main(String[] args) {
        SpringApplication.run(ExamApp.class, args);
    }

    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtFilter() {
        FilterRegistrationBean<JwtAuthFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new JwtAuthFilter());
        bean.addUrlPatterns("/assignment/*", "/submission/*", "/exam/*");
        bean.setOrder(1);
        return bean;
    }
}
