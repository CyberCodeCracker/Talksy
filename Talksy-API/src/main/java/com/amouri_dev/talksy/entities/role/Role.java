package com.amouri_dev.talksy.entities.role;


import com.amouri_dev.talksy.common.BaseAuditingEntity;
import com.amouri_dev.talksy.entities.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SuperBuilder
@Table(name = "ROLES")
public class Role extends BaseAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROLE_ID", columnDefinition = "BIGSERIAL")
    private Long id;

    private String name;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "roles")
    @JsonIgnore
    private Set<User> users;
}
