package org.hubson404.miniSocialNetwork.model;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tagId;

    private String tagName;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(mappedBy = "includedTags",fetch = FetchType.EAGER)
    private Set<Post> taggedPosts;

    public Tag(String tagName) {
        this.tagName = tagName;
    }
}
