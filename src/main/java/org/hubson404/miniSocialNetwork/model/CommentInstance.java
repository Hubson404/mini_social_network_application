package org.hubson404.miniSocialNetwork.model;

import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "commentPost_postId"))
public class CommentInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Post commentPost;

    @OneToOne(cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Post mainPost;

    public CommentInstance(Post commentPost, Post mainPost) {
        this.commentPost = commentPost;
        this.mainPost = mainPost;
    }
}