package jsl.moum.report.dto;

import jsl.moum.report.domain.ArticleReport;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ArticleReportDto {
    private int id;
    private int articleId;
    private String articleTitle;
    private int reporterId;
    private String reporterUsername;
    private String type;
    private String details;
    private String reply;
    private boolean isResolved;

    public ArticleReportDto(ArticleReport articleReport) {
        this.id = articleReport.getId();
        this.articleId = articleReport.getArticle().getId();
        this.articleTitle = articleReport.getArticle().getTitle();
        this.reporterId = articleReport.getReporter().getId();
        this.reporterUsername = articleReport.getReporter().getUsername();
        this.type = articleReport.getType();
        this.details = articleReport.getDetails();
        this.reply = articleReport.getReply();
        this.isResolved = articleReport.isResolved();
    }

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private int id;
        private int articleId;
        private String articleTitle;
        private int reporterId;
        private String reporterUsername;
        private String type;
        private String details;
        private String reply;
        private boolean isResolved;

        public Response(ArticleReport articleReport) {
            this.id = articleReport.getId();
            this.articleId = articleReport.getArticle().getId();
            this.articleTitle = articleReport.getArticle().getTitle();
            this.reporterId = articleReport.getReporter().getId();
            this.reporterUsername = articleReport.getReporter().getUsername();
            this.type = articleReport.getType();
            this.details = articleReport.getDetails();
            this.reply = articleReport.getReply();
            this.isResolved = articleReport.isResolved();
        }
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class Request {
        private int reporterId;
        private String type;
        private String details;
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class Reply {
        private String reply;
    }
}
