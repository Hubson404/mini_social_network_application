package org.hubson404.miniSocialNetwork.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Set;

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
    @CreationTimestamp
    private LocalDateTime createDate;
    private PostType postType;

    @ManyToOne
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private ServiceUser originalPoster;

    @OneToMany(mappedBy = "post")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<LikeBadge> likeBadges;

    @OneToMany(mappedBy = "post")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<ForwardBadge> forwardBadges;

    @ManyToMany
    private Set<Tag> includedTags;

    @OneToMany (mappedBy = "comment")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Post> comments;

    @ManyToOne
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Post comment;

    private int forwardedPostId;
    private String forwardedPostContent;

    public Post(String content, ServiceUser originalPoster) {
        this.content = content;
        this.isEdited = false;
        this.originalPoster = originalPoster;
        this.postType = PostType.ORIGINAL;
    }

    public void addLikeBadge(LikeBadge b) {
        this.likeBadges.add(b);
        b.setPost(this);
    }

    public void removeLikeBadge(LikeBadge b) {
        this.likeBadges.remove(b);
    }

    public void addForwardBadge(ForwardBadge b) {
        this.forwardBadges.add(b);
        b.setPost(this);
    }

    public void removeForwardBadge(ForwardBadge b) {
        this.forwardBadges.remove(b);
    }

    public void addTag(Tag t) {
        this.includedTags.add(t);
        t.getTaggedPosts().add(this);
    }

    public void removeTag(Tag t) {
        this.includedTags.remove(t);
        t.getTaggedPosts().remove(this);
    }



}
