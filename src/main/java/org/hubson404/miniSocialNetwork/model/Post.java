package org.hubson404.miniSocialNetwork.model;

import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int postId;

    @Column(length = 160)
    private String content;
    private boolean isEdited;
    private PostType postType;

    @ManyToOne//(fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private ServiceUser serviceUser;

    public Post(String content, ServiceUser serviceUser) {
        this.content = content;
        this.isEdited = false;
        this.serviceUser = serviceUser;
        this.postType = PostType.ORIGINAL;
    }
}
