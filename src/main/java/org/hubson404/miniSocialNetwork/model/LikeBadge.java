package org.hubson404.miniSocialNetwork.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class LikeBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    @CreationTimestamp
    private LocalDateTime createDate;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ServiceUser serviceUser;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Post post;

    public LikeBadge(ServiceUser serviceUser, Post post) {
        this.serviceUser = serviceUser;
        this.post = post;
    }

}
