package jsl.moum.report.service;

import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.domain.repository.MemberRepository;
import jsl.moum.community.article.domain.article.ArticleEntity;
import jsl.moum.community.article.domain.article.ArticleRepository;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;
import jsl.moum.moum.team.domain.TeamEntity;
import jsl.moum.moum.team.domain.TeamRepository;
import jsl.moum.report.domain.*;
import jsl.moum.report.dto.ArticleReportDto;
import jsl.moum.report.dto.MemberReportDto;
import jsl.moum.report.dto.TeamReportDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final TeamReportRepository teamReportRepository;
    private final MemberReportRepository memberReportRepository;
    private final ArticleReportRepository articleReportRepository;
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final ArticleRepository articleRepository;

    /**
     * Member Reports
     */

    public MemberReportDto.Response reportMember(Integer memberId, MemberReportDto.Request request) {

        MemberReport memberReport = buildMemberReport(memberId, request);
        memberReport = memberReportRepository.save(memberReport);

        MemberReportDto.Response response = new MemberReportDto.Response(memberReport);
        return response;
    }

    public MemberReportDto reportMemberReply(Integer reportId, MemberReportDto.Reply reply){
        MemberReport memberReport = memberReportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPORT_NOT_FOUND));

        memberReport.setReply(reply.getReply());
        memberReport.setResolved(true);
        memberReport = memberReportRepository.save(memberReport);

        return new MemberReportDto(memberReport);
    }

    public MemberReportDto.Response viewMemberReport(Integer reportId) {
        MemberReport memberReport = memberReportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPORT_NOT_FOUND));

        MemberReportDto.Response response = new MemberReportDto.Response(memberReport);
        return response;
    }

    public Page<MemberReportDto.Response> memberReportsSentPaged(Integer memberId, Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<MemberReport> memberReports = memberReportRepository.findAllByReporterId(memberId, pageRequest);

        return memberReports.map(MemberReportDto.Response::new);
    }

    public Page<MemberReportDto.Response> memberReportsReceivedPaged(Integer memberId, Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<MemberReport> memberReports = memberReportRepository.findAllByMemberId(memberId, pageRequest);

        return memberReports.map(MemberReportDto.Response::new);
    }

    public MemberReportDto.Response deleteMemberReport(Integer reportId) {
        MemberReport memberReport = memberReportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPORT_NOT_FOUND));
        memberReportRepository.delete(memberReport);
        return new MemberReportDto.Response(memberReport);
    }

    /**
     * Team Reports
     */

    public TeamReportDto.Response reportTeam(Integer teamId, TeamReportDto.Request request) {

        TeamReport teamReport = buildTeamReport(teamId, request);
        teamReport = teamReportRepository.save(teamReport);

        TeamReportDto.Response response = new TeamReportDto.Response(teamReport);
        return response;
    }

    public TeamReportDto reportTeamReply(Integer reportId, TeamReportDto.Reply reply){
        TeamReport teamReport = teamReportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPORT_NOT_FOUND));

        teamReport.setReply(reply.getReply());
        teamReport.setResolved(true);
        teamReport = teamReportRepository.save(teamReport);

        return new TeamReportDto(teamReport);
    }

    public TeamReportDto.Response viewTeamReport(Integer reportId) {
        TeamReport teamReport = teamReportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPORT_NOT_FOUND));

        TeamReportDto.Response response = new TeamReportDto.Response(teamReport);
        return response;
    }

    public Page<TeamReportDto.Response> teamReportsSentPaged(Integer memberId, Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<TeamReport> teamReports = teamReportRepository.findAllByReporterId(memberId, pageRequest);

        return teamReports.map(TeamReportDto.Response::new);
    }

    public Page<TeamReportDto.Response> teamReportsReceivedPaged(Integer teamId, Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<TeamReport> teamReports = teamReportRepository.findAllByTeamId(teamId, pageRequest);

        return teamReports.map(TeamReportDto.Response::new);
    }

    public TeamReportDto.Response deleteTeamReport(Integer reportId) {
        TeamReport teamReport = teamReportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPORT_NOT_FOUND));
        teamReportRepository.delete(teamReport);
        return new TeamReportDto.Response(teamReport);
    }

    /**
     * Article Reports
     */

    public ArticleReportDto.Response reportArticle(Integer articleId, ArticleReportDto.Request request) {
        ArticleReport articleReport = buildArticleReport(articleId, request);
        articleReport = articleReportRepository.save(articleReport);

        ArticleReportDto.Response response = new ArticleReportDto.Response(articleReport);
        return response;
    }

    public ArticleReportDto reportArticleReply(Integer reportId, ArticleReportDto.Reply reply){
        ArticleReport articleReport = articleReportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPORT_NOT_FOUND));

        articleReport.setReply(reply.getReply());
        articleReport.setResolved(true);
        articleReport = articleReportRepository.save(articleReport);

        return new ArticleReportDto(articleReport);
    }

    public ArticleReportDto.Response viewArticleReport(Integer reportId) {
        ArticleReport articleReport = articleReportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPORT_NOT_FOUND));

        ArticleReportDto.Response response = new ArticleReportDto.Response(articleReport);
        return response;
    }

    public Page<ArticleReportDto.Response> articleReportsSentPaged(Integer memberId, Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ArticleReport> articleReports = articleReportRepository.findAllByReporterId(memberId, pageRequest);

        return articleReports.map(ArticleReportDto.Response::new);
    }

    public Page<ArticleReportDto.Response> articleReportsReceivedPaged(Integer articleId, Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ArticleReport> articleReports = articleReportRepository.findAllByArticleId(articleId, pageRequest);

        return articleReports.map(ArticleReportDto.Response::new);
    }

    public ArticleReportDto.Response deleteArticleReport(Integer reportId) {
        ArticleReport articleReport = articleReportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPORT_NOT_FOUND));
        articleReportRepository.delete(articleReport);
        return new ArticleReportDto.Response(articleReport);
    }

    /**
     *
     * Private access methods
     *
     */

    private TeamReport buildTeamReport(Integer teamId, TeamReportDto.Request request) {
        TeamEntity team = teamRepository.findById(teamId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPORT_TEAM_FAIL));
        MemberEntity reporter = memberRepository.findById(request.getReporterId())
                .orElseThrow(() -> new CustomException(ErrorCode.REPORT_TEAM_FAIL));

        if (teamReportRepository.existsByReporterAndTeam(reporter.getId(), team.getId())) {
            throw new CustomException(ErrorCode.REPORT_TEAM_EXISTS);
        }

        TeamReport teamReport = TeamReport.builder()
                .team(team)
                .reporter(reporter)
                .type(request.getType())
                .details(request.getDetails())
                .reply(null)
                .isResolved(false)
                .build();
        return teamReport;
    }

    private MemberReport buildMemberReport(Integer memberId, MemberReportDto.Request request) {
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPORT_MEMBER_FAIL));
        MemberEntity reporter = memberRepository.findById(request.getReporterId())
                .orElseThrow(() -> new CustomException(ErrorCode.REPORT_MEMBER_FAIL));

        if (memberReportRepository.existsByReporterAndMember(reporter.getId(), member.getId())) {
            throw new CustomException(ErrorCode.REPORT_MEMBER_EXISTS);
        }

        MemberReport memberReport = MemberReport.builder()
                .member(member)
                .reporter(reporter)
                .type(request.getType())
                .details(request.getDetails())
                .reply(null)
                .isResolved(false)
                .build();
        return memberReport;
    }

    private ArticleReport buildArticleReport(Integer articleId, ArticleReportDto.Request request) {
        ArticleEntity article = articleRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPORT_ARTICLE_FAIL));
        MemberEntity reporter = memberRepository.findById(request.getReporterId())
                .orElseThrow(() -> new CustomException(ErrorCode.REPORT_ARTICLE_FAIL));

        if(articleReportRepository.existsByReporterIdAndArticleId(reporter.getId(), article.getId())) {
            throw new CustomException(ErrorCode.REPORT_ARTICLE_EXISTS);
        }

        ArticleReport articleReport = ArticleReport.builder()
                .article(article)
                .reporter(reporter)
                .type(request.getType())
                .details(request.getDetails())
                .reply(null)
                .isResolved(false)
                .build();
        return articleReport;
    }
}
