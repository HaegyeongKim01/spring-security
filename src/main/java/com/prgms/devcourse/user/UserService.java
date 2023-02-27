package com.prgms.devcourse.user;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 생성자를 통해서 DI 받는다.
     * @param userRepository
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * username을 받아서 조회하고 map으로 type을 UserDetails로 만들어준다.
     * @ 이때 security에서 제공하는 User를 builder를 통해 생성할 것
     * @param username String springsecurity에서 제공하는 html에 id form 을 username이라 지정하여 username이다.
     * @return UserDetails
     * @throws UsernameNotFoundException "Could not found user for " + username)
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByLoginId(username)
                .map(user ->  //user있다면 조회되는 것을 UserDetails의 type에 일치하도록 처리
                    User.builder()                          //User import할 때 import org.springframework.security.core.userdetails.User; 임을 확인할 것!!
                            .username(user.getLoginId())
                            .password(user.getPasswd())
                            .authorities(user.getGroup().getAuthorities())
                            .build()
                )
                .orElseThrow(() -> new UsernameNotFoundException("Could not found user for " + username));
    }
}
