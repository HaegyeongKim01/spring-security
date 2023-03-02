package com.prgms.devcourse.user;

import com.prgms.devcourse.configures.JwtConfigure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserRestControllerTest {

    private JwtConfigure jwtConfigure;

    private TestRestTemplate testTemplate;

    @Autowired
    public void setJwtTokenConfigure(JwtConfigure jwtConfigure) {
        this.jwtConfigure = jwtConfigure;
    }

    @Autowired
    public void setTestTemplate(TestRestTemplate testTemplate) {
        this.testTemplate = testTemplate;
    }

    @Test
    @DisplayName("JWT_토큰_Test")
    public void JwtTokenTest() {
        assertThat(tokenToName(getToken("user")), is("user"));
        assertThat(tokenToName(getToken("admin")), is("admin"));
    }

    private String getToken(String username) {
        return testTemplate.exchange(
                "/api/user/" + username +"/token",
                HttpMethod.GET,
                null,
                String.class
        ).getBody();
    }

    private String  tokenToName(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(jwtConfigure.getHeader(), token);
        return testTemplate.exchange(
                "/api/user/me",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        ).getBody();
    }
}