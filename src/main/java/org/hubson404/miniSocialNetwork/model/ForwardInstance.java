package org.hubson404.miniSocialNetwork.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ForwardInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private LocalDateTime createDate;

    @ManyToOne
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Post forwardPost;

    @OneToOne
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Post mainPost;

}
