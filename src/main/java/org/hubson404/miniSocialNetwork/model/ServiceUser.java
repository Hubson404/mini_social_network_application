package org.hubson404.miniSocialNetwork.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "USERS")
public class ServiceUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

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


    @OneToMany(mappedBy = "serviceUser")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Post> posts;

    @OneToMany(mappedBy = "serviceUser")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Like> likes;

    @OneToMany(mappedBy = "serviceUser")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Forward> forwards;

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
}
