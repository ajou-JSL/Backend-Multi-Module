package jsl.moum.report.dto;

import jsl.moum.report.domain.MemberReport;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberReportDto {
    private int id;
    private int memberId;
    private String memberUsername;
    private int reporterId;
    private String reporterUsername;
    private String type;
    private String details;
    private String reply;
    private boolean isResolved;

    public MemberReportDto(MemberReport memberReport) {
        this.id = memberReport.getId();
        this.memberId = memberReport.getMember().getId();
        this.memberUsername = memberReport.getMember().getUsername();
        this.reporterId = memberReport.getReporter().getId();
        this.reporterUsername = memberReport.getReporter().getUsername();
        this.type = memberReport.getType();
        this.details = memberReport.getDetails();
        this.reply = memberReport.getReply();
        this.isResolved = memberReport.isResolved();
    }

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private int id;
        private int memberId;
        private String memberUsername;
        private int reporterId;
        private String reporterUsername;
        private String type;
        private String details;
        private String reply;
        private boolean isResolved;

        public Response(MemberReport memberReport) {
            this.id = memberReport.getId();
            this.memberId = memberReport.getMember().getId();
            this.memberUsername = memberReport.getMember().getUsername();
            this.reporterId = memberReport.getReporter().getId();
            this.reporterUsername = memberReport.getReporter().getUsername();
            this.type = memberReport.getType();
            this.details = memberReport.getDetails();
            this.reply = memberReport.getReply();
            this.isResolved = memberReport.isResolved();
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
