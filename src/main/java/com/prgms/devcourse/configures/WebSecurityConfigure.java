package com.prgms.devcourse.configures;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfigure extends WebSecurityConfigurerAdapter {

    private final Logger log = LoggerFactory.getLogger(getClass());

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
                .withUser("admin01").password("{noop}admin123").roles("ADMIN")
                .and()
                .withUser("admin02").password("{noop}admin123").roles("ADMIN")
                ;
    }

    @Bean
    public AccessDecisionManager accessDecisionManager() {
        List<AccessDecisionVoter<?>> voters = new ArrayList<>();
        voters.add(new WebExpressionVoter());
        voters.add(new OddAdminVoter(new AntPathRequestMatcher("/admin")));
        return new UnanimousBased(voters);
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
                .antMatchers("/admin").access("hasRole('ADMIN') and isFullyAuthenticated()")  //ADMIN권한이 있어야 이 page를 호출할 수 있도록
                .anyRequest().permitAll()//위의 경우를 제외하고는 모두 permit
                .accessDecisionManager(accessDecisionManager())  // accessDecisionManager()를 통해서 생성되는 DecisionManage를 넘긴다.
                .and()
            .formLogin()
                .defaultSuccessUrl("/")  //로그인 성공 경우의 path지정
                .permitAll()
                .and()
            /**
             * 로그아웃 설정
             */
            .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))   //default로 /logout 이 설정되어있다. 밑의 코드들 역시 마찬가지
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
               .and()
            /**
            * remember me 설정
             */
            .rememberMe()  //cookie기반의 자동로그인
                .rememberMeParameter("remember-me")   //html checkBox태그 name에 일치
                .tokenValiditySeconds(300)
                .and()
            /**
            * AccessDenied 예외처리 핸들러
             */
            .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler())
                .and()
            /**
            * 세션 설정
            */
            /*
            .sessionManagement()
                .sessionFixation().changeSessionId()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) //세션 전략 설정
                .invalidSessionUrl("/") //유효하지 않은 세션이 감지된 경우 이동할 url 설정
                .maximumSessions(1)  //동시 로그인 가능한 최대 세션 개수
                    .maxSessionsPreventsLogin(false) //최대 세션 개수가 된 경우 false -> 막는다.
                    .and()
                .and()

             */

//             굳이 설정을 따로 하진 않고 이런 경우가 있다 정도만 알 것
//            .anonymous() //anonymous : 로그인이 되지 않은 경우
//                .principal("thisIsAnnoymousUser")  //default: Annoymous 대신 넣을 이름
//                .authorities("ROLE_ANNOYMOUS", "ROLE_UNKNOWN");
        ;
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (httpServletRequest, httpServletResponse, e) -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Object principal = authentication != null ? authentication.getPrincipal() : null;
            log.warn("{} us denied", principal, e);
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN); //403 Status
            httpServletResponse.setContentType("text/plain");
            httpServletResponse.getWriter().write("## ACCESS DENIED ##");
            httpServletResponse.getWriter().flush();
            httpServletResponse.getWriter().close();
        };


    }

}
