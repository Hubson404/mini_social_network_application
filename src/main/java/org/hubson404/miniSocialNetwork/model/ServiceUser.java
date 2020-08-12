package org.hubson404.miniSocialNetwork.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ServiceUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true)
    private String login;
    private String password;

    @Column(unique = true)
    private String accountName;
    private String userName;
    private LocalDateTime createDate;
    private String avatar;
    private AccountStatus accountStatus;
    private boolean privateAccount;
    private boolean isDeleted;

    @OneToMany(mappedBy = "originalPoster")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Post> posts;

    @OneToMany(mappedBy = "serviceUser")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<LikeBadge> likeBadges;

    @OneToMany(mappedBy = "serviceUser")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<ForwardBadge> forwardBadges;

//    @EqualsAndHashCode.Exclude
//    @ToString.Exclude
//    private Set<ServiceUser> followedUsers;
//
//    @EqualsAndHashCode.Exclude
//    @ToString.Exclude
//    private Set<ServiceUser> followers;

    public ServiceUser(String login, String password, String accountName,
                       String userName, String avatar, boolean privateAccount) {
        this.login = login;
        this.password = password;
        this.accountName = accountName;
        this.userName = userName;
        this.createDate = LocalDateTime.now();
        this.avatar = avatar;
        this.accountStatus = AccountStatus.ACTIVE;
        this.privateAccount = privateAccount;
        this.isDeleted = false;

    }

    public void addPost(Post p) {
        this.posts.add(p);
        p.setOriginalPoster(this);
    }

    public void deletePost(Post p) {
        this.posts.remove(p);
    }

}
