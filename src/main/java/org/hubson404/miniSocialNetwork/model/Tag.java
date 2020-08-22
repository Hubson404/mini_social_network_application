package org.hubson404.miniSocialNetwork.model;

import lombok.*;
import org.hubson404.miniSocialNetwork.model.utils.TagNameSearchable;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "tagName"))
public class Tag implements TagNameSearchable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tagId;

    @Column(nullable = false)
    private String tagName;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "post_tag",
            joinColumns = {@JoinColumn(name = "fk_tag")},
            inverseJoinColumns = {@JoinColumn(name = "fk_post")})
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Post> taggedPosts = new HashSet<>();

    public Tag(String tagName) {
        this.tagName = tagName;
    }
}
