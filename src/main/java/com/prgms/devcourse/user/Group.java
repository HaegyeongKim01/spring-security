package com.prgms.devcourse.user;


import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Getter
@Entity
@Table(name = "groups")
public class Group {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "group")
    private List<GroupPermission> groupPermissions = new ArrayList<>();   //@Todo 의문! groupPermission이 아닌가?

    /**
     * Permission의 getName을 가져와서 SimpleGrantedAuthority로 변환 후 List화
     * @return SpringSecurity 권한 목록을 반환
     */
    public List<GrantedAuthority> getAuthorities() {
        return groupPermissions.stream()
                .map(gp -> new SimpleGrantedAuthority(gp.getPermission().getName()))
                .collect(toList());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("name", name)
                .append("authorities", getAuthorities())
                .toString();
    }

}
