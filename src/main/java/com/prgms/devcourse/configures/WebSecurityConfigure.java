package com.prgms.devcourse.configures;

import com.prgms.devcourse.jwt.Jwt;
import com.prgms.devcourse.jwt.JwtAuthenticationFilter;
import com.prgms.devcourse.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class WebSecurityConfigure extends WebSecurityConfigurerAdapter {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private JwtConfigure jwtConfigure;  //Jwt를 Bean으로 등록하려면 JwtConfigure 필요하다

    private UserService userService;

    @Autowired
    public void setJwtConfigure(JwtConfigure jwtConfigure) {
        this.jwtConfigure = jwtConfigure;
    }

    /**
     * setter를 통해서 DI 받는다.
     * @param userService UserService
     */
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * JPA사용하는 경우 method안에서 쿼리를 제공할 필요없다.
     * @param auth AuthenticationManagerBuilder
     * @throws Exception Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);   //JPA 사용하여 JdbcDaoImpl 구현
    }

    /**
     * Override 하여 우리가 원하는대로 security customizing 가능
     * @param web  WebSecurity 클래스는 필터 체인 관련 전역 설정을 처리할 수 있는 API 제공
     * @ignoring() : 지정된 path에 mapping되는 요청은 spring security filter chain을 태우지 않겠다.
     */
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/assets/**", "/h2-console/**");
    }

    /**
     * Jwt 객체 Bean으로 등록
     * @return Jwt
     */
    @Bean
    public Jwt jwt() {
        return new Jwt(
                jwtConfigure.getIssuer(),
                jwtConfigure.getClientSecret(),
                jwtConfigure.getExpirySeconds()
        );
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

    /**
     * password Encoder 명시적으로 설정
     * @return BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        Jwt jwt = getApplicationContext().getBean(Jwt.class);
        return new JwtAuthenticationFilter(jwtConfigure.getHeader(), jwt);
    }

    /**
     *
     * @param http HttpSecurity:  세부적인 웹 보안기능 설정을 처리할 수 있는 APi를 제공
     * @throws Exception -
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()//공개(인증영역) 리소스 혹은 보호받는 리소스(익명영역)에 대한 세부 설정
                .antMatchers("/api/user/me").hasAnyRole("USER", "ADMIN")
                .anyRequest().permitAll()//위의 경우를 제외하고는 모두 permit
                .and()
            .csrf()   //page기반 서비스가 아니기에 disable로
                .disable()
            .headers()
                .disable()
            .formLogin()
                .disable()
            .httpBasic()   //BasicAuthenticationFilter 적용
                .disable()
            .rememberMe()
                .disable()
            .logout()
                .disable()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  //Stateless!! HTTP session 사용하지 않겠다
                .and()
                /*
                  예외처리 핸들러
                 */
            .exceptionHandling()   //AccessDenied 예외처리 핸들러
                .accessDeniedHandler(accessDeniedHandler())
                .and()
            .addFilterAfter(jwtAuthenticationFilter(), SecurityContextPersistenceFilter.class) //SecurityContextPersistenceFilter 다음으로 jwtAuthenticationFilter가 들어가게 된다
        ;
    }



}
