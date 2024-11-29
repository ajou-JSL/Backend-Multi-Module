package jsl.moum.moum.settlement.controller;

import jsl.moum.auth.domain.CustomUserDetails;
import jsl.moum.common.CommonService;
import jsl.moum.global.response.ResponseCode;
import jsl.moum.global.response.ResultResponse;
import jsl.moum.moum.settlement.dto.SettlementDto;
import jsl.moum.moum.settlement.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SettlementController {

    private final CommonService commonService;
    private final SettlementService settlementService;

    /*
        정산 생성
     */
    @PostMapping("/api/moums/{moumId}/settlements")
    public ResponseEntity<ResultResponse> createSettlement(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                           @PathVariable int moumId,
                                                           @RequestBody SettlementDto.Request settlementDto){

        String username = commonService.loginCheck(customUserDetails.getUsername());
        SettlementDto.Response responseDto = settlementService.createSettlement(username, moumId, settlementDto);
        ResultResponse response = ResultResponse.of(ResponseCode.CREATE_SETTLEMENT_SUCCESS,responseDto);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
    }

    /*
        정산 목록 조회
     */
    @GetMapping("/api/moums/{moumId}/settlements-all")
    public ResponseEntity<ResultResponse> getAllSettlement(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                           @PathVariable int moumId){

        commonService.loginCheck(customUserDetails.getUsername());
        List<SettlementDto.Response> responseDto = settlementService.getSettlementList(moumId);
        ResultResponse response = ResultResponse.of(ResponseCode.GET_SETTLEMENT_LIST_SUCCESS,responseDto);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
    }

    /*
        정산 삭제
     */
    @DeleteMapping("/api/moums/{moumId}/settlements/{settlementId}")
    public ResponseEntity<ResultResponse> deleteSettlement(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                           @PathVariable int moumId,
                                                           @PathVariable int settlementId){

        String username = commonService.loginCheck(customUserDetails.getUsername());
        SettlementDto.Response responseDto = settlementService.deleteSettlement(username, moumId, settlementId);
        ResultResponse response = ResultResponse.of(ResponseCode.DELETE_SETTLEMENT_SUCCESS,responseDto);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
    }
}
