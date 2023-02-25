package com.prgms.devcourse.configures;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.WebSecurityExpressionRoot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.math.NumberUtils.toInt;

public class CustomWebSecurityExpressionRoot extends WebSecurityExpressionRoot {

    //정규식 [0-9]가 끝 부분에 있다! 는 정규식
    static final Pattern PATTERN = Pattern.compile("[0-9]+$");

    /**
     * Constructor
     * @param a Authenticataion
     * @param fi FilterInvocation
     */
    public CustomWebSecurityExpressionRoot(Authentication a, FilterInvocation fi) {
        super(a, fi);
    }

    //홀수인지 판별
    public boolean isOddAdmin() {
        //현재 사용자의 principal을 가져온다.
        User user = (User) getAuthentication().getPrincipal();
        String name = user.getUsername();
        Matcher matcher = PATTERN.matcher(name);
        if (matcher.find()) {
            int number = toInt(matcher.group(), 0);
            return number % 2 == 1;
        }
        return false;
    }
}
