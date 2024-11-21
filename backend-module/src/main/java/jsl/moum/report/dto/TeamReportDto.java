package jsl.moum.report.dto;

import jsl.moum.report.domain.TeamReport;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeamReportDto {
    private int id;
    private int teamId;
    private String teamName;
    private int reporterId;
    private String reporterUsername;
    private String type;
    private String details;
    private String reply;
    private boolean isResolved;

    public TeamReportDto(TeamReport teamReport) {
        this.id = teamReport.getId();
        this.teamId = teamReport.getTeam().getId();
        this.teamName = teamReport.getTeam().getTeamName();
        this.reporterId = teamReport.getReporter().getId();
        this.reporterUsername = teamReport.getReporter().getUsername();
        this.type = teamReport.getType();
        this.details = teamReport.getDetails();
        this.reply = teamReport.getReply();
        this.isResolved = teamReport.isResolved();
    }

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private int id;
        private int teamId;
        private String teamName;
        private int reporterId;
        private String reporterUsername;
        private String type;
        private String details;
        private String reply;
        private boolean isResolved;

        public Response(TeamReport teamReport) {
            this.id = teamReport.getId();
            this.teamId = teamReport.getTeam().getId();
            this.teamName = teamReport.getTeam().getTeamName();
            this.reporterId = teamReport.getReporter().getId();
            this.reporterUsername = teamReport.getReporter().getUsername();
            this.type = teamReport.getType();
            this.details = teamReport.getDetails();
            this.reply = teamReport.getReply();
            this.isResolved = teamReport.isResolved();
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
