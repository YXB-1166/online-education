package com.edu.user;

import com.edu.common.filter.JwtAuthFilter;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@MapperScan("com.edu.user.mapper")
@SpringBootApplication(scanBasePackages = {"com.edu.user", "com.edu.common"})
public class UserApp {
    public static void main(String[] args) {
        SpringApplication.run(UserApp.class, args);
    }

    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtFilter() {
        FilterRegistrationBean<JwtAuthFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new JwtAuthFilter("/user/login"));
        bean.addUrlPatterns("/user/*");
        bean.setOrder(1);
        return bean;
    }
}
