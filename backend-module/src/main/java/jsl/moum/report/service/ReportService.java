package jsl.moum.report.service;

import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.domain.repository.MemberRepository;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;
import jsl.moum.moum.team.domain.TeamEntity;
import jsl.moum.moum.team.domain.TeamRepository;
import jsl.moum.report.domain.*;
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


    public MemberReportDto.Response reportMember(Integer memberId, MemberReportDto.Request request) {

        MemberReport memberReport = buildMemberReport(memberId, request);
        memberReport = memberReportRepository.save(memberReport);

        MemberReportDto.Response response = new MemberReportDto.Response(memberReport);
        return response;
    }

    public TeamReportDto.Response reportTeam(Integer teamId, TeamReportDto.Request request) {

        TeamReport teamReport = buildTeamReport(teamId, request);
        teamReport = teamReportRepository.save(teamReport);

        TeamReportDto.Response response = new TeamReportDto.Response(teamReport);
        return response;
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

        if(teamReportRepository.existsByReporterAndTeam(reporter.getId(), team.getId())){
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

    private MemberReport buildMemberReport(Integer memberId, MemberReportDto.Request request){
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPORT_MEMBER_FAIL));
        MemberEntity reporter = memberRepository.findById(request.getReporterId())
                .orElseThrow(() -> new CustomException(ErrorCode.REPORT_MEMBER_FAIL));

        if(memberReportRepository.existsByReporterAndMember(reporter.getId(), member.getId())){
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

}
