package com.prgms.devcourse.user;


import lombok.Getter;

import javax.persistence.*;


@Getter
@Entity
@Table(name = "group_permission")
public class GroupPermission {

    @Id
    private Long id;

    @ManyToOne(optional = false)   //column이 null이 될 수 없기에 optional=true
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne(optional = false)
    @JoinColumn(name = "permission_id")
    private Permission permission;


}
