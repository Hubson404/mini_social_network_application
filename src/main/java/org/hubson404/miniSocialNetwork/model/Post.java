package org.hubson404.miniSocialNetwork.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hubson404.miniSocialNetwork.model.utils.PostType;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(length = 160)
    private String content;
    private boolean isEdited;
    @CreationTimestamp
    private LocalDateTime createDate;
    private PostType postType;

    @ManyToOne(cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private ServiceUser originalPoster;

    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<LikeBadge> likeBadges;

    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<ForwardBadge> forwardBadges;

    @ManyToMany(mappedBy = "taggedPosts",fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Tag> includedTags = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Post> comments;

    @ManyToOne
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Post mainPost;

    public Post(String content, PostType postType, ServiceUser originalPoster) {
        this.content = content;
        this.isEdited = false;
        this.postType = postType;
        this.originalPoster = originalPoster;
    }
}
