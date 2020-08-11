package org.hubson404.miniSocialNetwork.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int likeId;

    LocalDateTime createDate;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    ServiceUser serviceUser;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    Post post;

    public Like(ServiceUser serviceUser, Post post) {
        this.createDate = LocalDateTime.now();
        this.serviceUser = serviceUser;
        this.post = post;
    }
}
