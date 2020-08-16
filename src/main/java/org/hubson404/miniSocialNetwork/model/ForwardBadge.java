package org.hubson404.miniSocialNetwork.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ForwardBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int forwardId;

    @CreationTimestamp
    LocalDateTime createDate;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    ServiceUser serviceUser;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    Post post;

    public ForwardBadge(ServiceUser serviceUser, Post post) {
        this.serviceUser = serviceUser;
        this.post = post;
    }
}
