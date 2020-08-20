package org.hubson404.miniSocialNetwork.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hubson404.miniSocialNetwork.database.EntityDao;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Optional;
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

    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<ForwardBadge> forwardBadges;

    @ManyToMany
    private Set<Tag> includedTags;

    @OneToMany(mappedBy = "commentPost", fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<CommentInstance> comments;

    @OneToOne(mappedBy = "mainPost", fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private CommentInstance mainPost;

//    private int forwardedPostId;
//    private String forwardedPostContent;

    public Post(String content, PostType postType, ServiceUser originalPoster) {
        this.content = content;
        this.isEdited = false;
        this.postType = postType;
        this.originalPoster = originalPoster;
    }

    //todo: fix commenting on post
    public CommentInstance commentPost(Post comment) {
        CommentInstance ci = new CommentInstance(comment, this);
        this.getComments().add(ci);
        comment.setMainPost(ci);
        return ci;
    }

    public void addLikeBadge(ServiceUser serviceUser) {
        LikeBadge lb = new LikeBadge(serviceUser, this);
        new EntityDao<LikeBadge>().saveOrUpdate(lb);
        this.likeBadges.add(lb);
        serviceUser.getLikeBadges().add(lb);
    }

    public void removeLikeBadge(ServiceUser serviceUser) {
        Optional<LikeBadge> op = getLikeBadges()
                .stream()
                .filter(fi -> fi.getServiceUser().equals(serviceUser))
                .findFirst();
        LikeBadge lbBeingRemoved = op.get();
        this.likeBadges.remove(lbBeingRemoved);
        serviceUser.getLikeBadges().remove(lbBeingRemoved);
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

    public boolean isLiked(ServiceUser serviceUser) {
        boolean isPresent = this.getLikeBadges()
                .stream()
                .map(LikeBadge::getServiceUser)
                .anyMatch(u -> u.equals(serviceUser));
        return isPresent;
    }


}
