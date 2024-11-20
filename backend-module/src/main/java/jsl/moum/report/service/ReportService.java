package jsl.moum.report.service;

import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.domain.repository.MemberRepository;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;
import jsl.moum.report.domain.ArticleReportRepository;
import jsl.moum.report.domain.MemberReport;
import jsl.moum.report.domain.MemberReportRepository;
import jsl.moum.report.domain.TeamReportRepository;
import jsl.moum.report.dto.MemberReportDto;
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


    public MemberReportDto.Response reportMember(Integer memberId, MemberReportDto.Request request) {

        MemberReport memberReport = buildMemberReport(memberId, request);
        memberReport = memberReportRepository.save(memberReport);

        MemberReportDto.Response response = new MemberReportDto.Response(memberReport);
        return response;
    }


    /**
     *
     * Private access methods
     *
     */

    private MemberReport buildMemberReport(Integer memberId, MemberReportDto.Request request){
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPORT_MEMBER_FAIL));
        MemberEntity reporter = memberRepository.findById(request.getReporterId())
                .orElseThrow(() -> new CustomException(ErrorCode.REPORT_MEMBER_FAIL));

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
