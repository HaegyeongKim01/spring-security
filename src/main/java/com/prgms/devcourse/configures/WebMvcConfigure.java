package com.prgms.devcourse.configures;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableJdbcHttpSession
public class WebMvcConfigure implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        //root path로 온 경우 template/index로 lendering 하라는 SpringMvc 설정
        registry.addViewController("/").setViewName("index");
        // /me path로 온 경우 template/me.html로 lendering 해라
        registry.addViewController("/me").setViewName("me");

        registry.addViewController("/admin").setViewName("admin");
        //send url 요청 처리할 수 있도록 설정
        registry.addViewController("/send").setViewName("send");
    }
}
