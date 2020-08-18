package org.hubson404.miniSocialNetwork.model;

import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ServiceUser implements UserNameSearchable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true)
    private String login;
    private String password;

    @Column(unique = true)
    private String accountName;
    private String userName;

    @CreationTimestamp
    private LocalDateTime createDate;
    private String avatar;
    private AccountStatus accountStatus;
    private boolean privateAccount;
    private boolean isDeleted;

    @OneToMany(mappedBy = "originalPoster", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
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

    @OneToMany(mappedBy = "mainUser", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<FollowInstance> followedUsers;

    @OneToMany(mappedBy = "followedUser", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<FollowInstance> followers;

    public ServiceUser(String login, String password, String accountName,
                       String userName, String avatar, boolean privateAccount) {
        this.login = login;
        this.password = password;
        this.accountName = accountName;
        this.userName = userName;
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

    public FollowInstance followUser(ServiceUser followedUser) {
        FollowInstance fi = new FollowInstance(followedUser, this);
        this.followedUsers.add(fi);
        followedUser.getFollowers().add(fi);
        return fi;
    }

    public FollowInstance unfollowUser(ServiceUser followedUser) {
        Optional<FollowInstance> op = getFollowedUsers()
                .stream()
                .filter(fi -> fi.getFollowedUser().equals(followedUser))
                .findFirst();
        FollowInstance fiBeingRemoved = op.get();
        fiBeingRemoved.setFollowedUser(null);
        fiBeingRemoved.setMainUser(null);
        this.followedUsers.remove(fiBeingRemoved);
        followedUser.getFollowers().remove(fiBeingRemoved);
        return fiBeingRemoved;
    }

    public void showFollowedUsers() {
        System.out.println("  -> Users followed by <" + this.getUserName() + ">: ");
        if (followedUsers.isEmpty()) {
            System.out.println("  -> <" + this.getUserName() + "> has not followed anyone yet.");
        }
        for (FollowInstance followedUser : followedUsers) {
            ServiceUser followedUserEntity = followedUser.getFollowedUser();
            System.out.println("{userName: " + followedUserEntity.getUserName()
                    + "; user ID: " + followedUserEntity.getUserId()
                    + "; following since: " + followedUser.getCreateDate().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"))
                    + "}");
        }
        System.out.println("  -> ***");
    }

    public void showFollowers() {
        System.out.println("  -> Users following <" + this.getUserName() + ">: ");
        if (followers.isEmpty()) {
            System.out.println("  -> Nobody has followed <" + this.getUserName() + "> yet.");
        }
        for (FollowInstance follower : followers) {
            ServiceUser followerEntity = follower.getMainUser();
            System.out.println("{userName: " + followerEntity.getUserName()
                    + "; user ID: " + followerEntity.getUserId()
                    + "; following since: " + follower.getCreateDate().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"))
                    + "}");
        }
        System.out.println("  -> ***");
    }

    public boolean isFollowed(ServiceUser serviceUser) {
        Set<ServiceUser> collect = this.getFollowedUsers()
                .stream()
                .map(FollowInstance::getFollowedUser)
                .collect(Collectors.toSet());
        return collect.contains(serviceUser);
    }

}
