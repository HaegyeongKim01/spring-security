package com.prgms.devcourse.configures;

import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfigure extends WebSecurityConfigurerAdapter {

    /**
     * 로그인 사용자 추가
     * @param auth
     * @throws Exception 예외처리
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user").password("{noop}user123").roles("USER")
                .and()
                .withUser("admin").password("{noop}admin123").roles("ADMIN")
                ;
    }

    /**
     * Override 하여 우리가 원하는대로 security customizing 가능
     * @param web  WebSecurity 클래스는 필터 체인 관련 전역 설정을 처리할 수 있는 API 제공
     * @ignoring() : 지정된 path에 mapping되는 요청은 spring security filter chain을 태우지 않겠다.
     */
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/assets/**");
    }

    /**
     *
     * @param http HttpSecurity:  세부적인 웹 보안기능 설정을 처리할 수 있는 APi를 제공
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()//공개(인증영역) 리소스 혹은 보호받는 리소스(익명영역)에 대한 세부 설정
                .antMatchers("/me").hasAnyRole("USER", "ADMIN") //path: me인 경우 요청하는 사용자가 USER 혹은 ADMIN권한을 가지고 있어야 한다.
                .anyRequest().permitAll()//위의 경우를 제외하고는 모두 permit
                .and()
            .formLogin()
                .defaultSuccessUrl("/")  //로그인 성공 경우의 path지정
                .permitAll()
                .and()
            .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))   //default로 /logout 이 설정되어있다. 밑의 코드들 역시 마찬가지
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
               .and()
            .rememberMe()  //cookie기반의 자동로그인
                .rememberMeParameter("remember-me")   //html checkBox태그 name에 일치
                .tokenValiditySeconds(300)
        ;
    }


}
