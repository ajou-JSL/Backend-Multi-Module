package jsl.moum.report.controller;

import jsl.moum.global.response.ResponseCode;
import jsl.moum.global.response.ResultResponse;
import jsl.moum.report.dto.ArticleReportDto;
import jsl.moum.report.dto.MemberReportDto;
import jsl.moum.report.dto.TeamReportDto;
import jsl.moum.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/report")
@Slf4j
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/member/{id}")
    public ResponseEntity<ResultResponse> reportMember(@PathVariable(name = "id") Integer id,
                                                       @RequestBody MemberReportDto.Request request) {

        MemberReportDto.Response report = reportService.reportMember(id, request);

        ResultResponse resultResponse = ResultResponse.of(ResponseCode.REPORT_MEMBER_SUCCESS, report);
        return ResponseEntity.ok(resultResponse);
    }

    @GetMapping("/member/view/{id}")
    public ResponseEntity<ResultResponse> viewMemberReport(@PathVariable(name = "id") Integer id) {
        MemberReportDto.Response report = reportService.viewMemberReport(id);
        ResultResponse resultResponse = ResultResponse.of(ResponseCode.VIEW_REPORT_MEMBER_SUCCESS, report);
        return ResponseEntity.ok(resultResponse);
    }

    @PostMapping("/team/{id}")
    public ResponseEntity<ResultResponse> reportTeam(@PathVariable(name = "id") Integer id,
                                                     @RequestBody TeamReportDto.Request request) {

        TeamReportDto.Response report = reportService.reportTeam(id, request);

        ResultResponse resultResponse = ResultResponse.of(ResponseCode.REPORT_TEAM_SUCCESS, report);
        return ResponseEntity.ok(resultResponse);
    }

    @GetMapping("/team/view/{id}")
    public ResponseEntity<ResultResponse> viewTeamReport(@PathVariable(name = "id") Integer id) {
        TeamReportDto.Response report = reportService.viewTeamReport(id);
        ResultResponse resultResponse = ResultResponse.of(ResponseCode.VIEW_REPORT_TEAM_SUCCESS, report);
        return ResponseEntity.ok(resultResponse);
    }

    @PostMapping("/article/{id}")
    public ResponseEntity<ResultResponse> reportArticle(@PathVariable(name = "id") Integer id,
                                                        @RequestBody ArticleReportDto.Request request) {

        ArticleReportDto.Response report = reportService.reportArticle(id, request);

        ResultResponse resultResponse = ResultResponse.of(ResponseCode.REPORT_MEMBER_REPLY_SUCCESS, report);
        return ResponseEntity.ok(resultResponse);
    }

    @GetMapping("/article/view/{id}")
    public ResponseEntity<ResultResponse> viewArticleReport(@PathVariable(name = "id") Integer id) {
        ArticleReportDto.Response report = reportService.viewArticleReport(id);
        ResultResponse resultResponse = ResultResponse.of(ResponseCode.VIEW_REPORT_ARTICLE_SUCCESS, report);
        return ResponseEntity.ok(resultResponse);
    }
}
