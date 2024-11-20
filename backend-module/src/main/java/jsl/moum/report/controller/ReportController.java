package jsl.moum.report.controller;

import jsl.moum.global.response.ResponseCode;
import jsl.moum.global.response.ResultResponse;
import jsl.moum.report.dto.MemberReportDto;
import jsl.moum.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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

}
