package jsl.moum.community.article.domain.article_details;

import jakarta.persistence.*;
import jsl.moum.community.comment.domain.CommentEntity;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "article_details")
public class ArticleDetailsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "article_id")
    private int articleId;

    @Column(name = "content")
    private String content;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "article_details_images", joinColumns = @JoinColumn(name = "article_details_id"))
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();

    @OneToMany(mappedBy = "articleDetails", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<CommentEntity> comments = new ArrayList<>();

    public void updateFileUrls(List<String> newUrls) {
        if(this.imageUrls == null){
            this.imageUrls = new ArrayList<>();
        }
        this.imageUrls.clear();
        this.imageUrls.addAll(newUrls);
    }

    public void updateArticleDetails(String newContent){
        if(newContent != null){
            this.content = newContent;
        }
    }

}
