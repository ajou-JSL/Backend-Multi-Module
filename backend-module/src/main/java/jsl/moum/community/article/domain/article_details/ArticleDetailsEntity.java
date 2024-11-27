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

    @Column(name = "file_url")
    private String fileUrl;

    @OneToMany(mappedBy = "articleDetails", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<CommentEntity> comments = new ArrayList<>();

    public void updateArticleImage(String newUrl){
        if(newUrl != null){
            this.fileUrl = newUrl;
        }
    }

    public void updateArticleDetails(String newContent){
        if(newContent != null){
            this.content = newContent;
        }
    }

}
