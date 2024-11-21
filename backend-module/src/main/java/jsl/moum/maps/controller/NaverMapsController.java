package jsl.moum.maps.controller;

import jsl.moum.global.response.ResponseCode;
import jsl.moum.global.response.ResultResponse;
import jsl.moum.maps.dto.NaverMapsTestDto;
import jsl.moum.maps.service.NaverMapsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/maps")
@Slf4j
public class NaverMapsController {

    private final NaverMapsService naverMapsService;

    @PostMapping("/location-info")
    public ResponseEntity<ResultResponse> getLocationInfoTest(@RequestParam(name = "shortUrl") String shortUrl) {
        NaverMapsTestDto locationInfo = naverMapsService.getLocationInfoTest(shortUrl);

        ResultResponse resultResponse = ResultResponse.of(ResponseCode.GET_LOCATION_INFO_SUCCESS, locationInfo);
        return ResponseEntity.ok(resultResponse);
    }


}
