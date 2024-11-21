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


    /**
     * Private access methods
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
