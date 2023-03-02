package com.prgms.devcourse.jwt;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

//SpringSecurity filter chain에 넣어야한다. =>  webSecurityConfigure 에 confiture 메소드에 설정함
@Log4j2
public class JwtAuthenticationFilter extends GenericFilterBean {

    //Http 헤더에서 jwt 토큰을 꺼내올 떄 사용
    private final String headerKey;

    //Jwt 토큰 디코딩할 때 사용
    private final Jwt jwt;

    public JwtAuthenticationFilter(String headerKey, Jwt jwt) {
        this.headerKey = headerKey;
        this.jwt = jwt;
    }

    /**
     * HTTP 요청 헤더에 JWT 토큰이 있는지 확인
     * JWT 토큰이 있다면, 주어진 토큰 디코딩,
     * username, roles 데이터 추출 후 UsernamePasswordQuthnticationTocken생성
     * 이렇게 만들어진 UsernamePasswordAthenticationToken 참조를 SecurityContext에 넣어줌
     * */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)  throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = getToken(request);   //직접 정의한 getToken()호출하여 token가져온다.
            if (token != null) {
                try {
                    Jwt.Claims claims = verify(token);  //Token을 Decoding
                    log.debug("Jwt parse result: {}", claims);

                    String username = claims.username;
                    List<GrantedAuthority> authorities = getAuthorities(claims);

                    if (isNotEmpty(username) && authorities.size() > 0) {  //JwtAuthenticationToken 생성
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(username, null, authorities);
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } catch (Exception e) {
                    log.warn("Jwt processing failed: {}", e.getMessage());
                }
            }
        } else { //Security Context에 참조가 들어있는 경우
            log.debug("SecurityContextHolder not populated with security token, as it already contained: '{}'",
                    SecurityContextHolder.getContext().getAuthentication());
        }

        chain.doFilter(request, response);
    }

    /**
     * Http Header에서 jwt token 꺼내오는 method
     * @param request HttpServletRequest
     * @return String - token
     */
    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(headerKey);
        if (isNotEmpty(token)) {
            log.debug("Jwt authorization api detected: {}", token);
            try {
                return URLDecoder.decode(token, "UTF-8");   //URL Decode
            } catch (UnsupportedEncodingException e) {
                log.error(e.getMessage(), e);
            }
        }
        return null;  //token 찾을 수 없는 경우
    }

    /**
     * token을 Decoding 하는 method
     * @param token String
     * @return Claims
     */
    private Jwt.Claims verify(String token) {
        return jwt.verify(token);
    }

    private List<GrantedAuthority> getAuthorities(Jwt.Claims claims) {
        String[] roles = claims.roles;
        return roles == null || roles.length == 0 ?
                emptyList() :
                Arrays.stream(roles).map(SimpleGrantedAuthority::new).collect(toList()); //값이 있다면 stream으로 감싸고 SimpleGrantedAuthority type으로 변환
    }

}
