package com.prgms.devcourse.configures;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component  //EventHandler가 Bean으로 등록될 수 있도록 Component 어노테이션 사용하여 자동으로 등록되게 한다.
public class CustomerAuthenticationEventHandler {
    //이벤트 리스너 //EventLinstenr 어노테이션 생성

    @EventListener
    public void handleAuthenticationSuccess(AuthenticationSuccessEvent event) {
        //event를 통해서 Authentication객체를 얻어올 수 있다. 인증이 된 Authentication이 넘어올 것이다.
        Authentication authentication= event.getAuthentication();
        log.info("Sucessful authentication result : {}", authentication.getPrincipal());
    }

    @EventListener
    public void handleFailureEvent(AbstractAuthenticationFailureEvent event) {
        //Exception class를 가져온다.
        Exception e = event.getException();
        Authentication authentication = event.getAuthentication();
        log.warn("Unsuccessful authentication result: {}", authentication, e);
    }


}
