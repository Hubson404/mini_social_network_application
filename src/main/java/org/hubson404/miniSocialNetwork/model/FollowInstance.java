package org.hubson404.miniSocialNetwork.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"followedUser_userId", "mainUser_userId"}) )
public class FollowInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long followId;

    @ManyToOne
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private ServiceUser followedUser;

    @ManyToOne
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private ServiceUser mainUser;

    @CreationTimestamp
    private LocalDateTime createDate;

    public FollowInstance(ServiceUser followedUser, ServiceUser mainUser) {
        this.followedUser = followedUser;
        this.mainUser = mainUser;
    }
}
