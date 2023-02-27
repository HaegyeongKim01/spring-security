package com.prgms.devcourse.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    //Patch Join 이용한 쿼리
    @Query("select u from User u join fetch u.group g left join fetch g.groupPermissions gp join fetch gp.permission where u.loginId = :loginId")
    Optional<User> findByLoginId(String loginId);

}
