package jsl.moum.maps.controller;

import jsl.moum.global.response.ResponseCode;
import jsl.moum.global.response.ResultResponse;
import jsl.moum.maps.dto.NaverMapsDto;
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


    @PostMapping("/search/geo-info")
    public ResponseEntity<ResultResponse> getGeoInfoByQuery(@RequestParam(name = "query") String query) {
        NaverMapsDto.GeoInfo locationInfo = naverMapsService.getGeoInfoByQuery(query);

        ResultResponse resultResponse = ResultResponse.of(ResponseCode.GET_LOCATION_INFO_SUCCESS, locationInfo);
        return ResponseEntity.ok(resultResponse);
    }

    @PostMapping("/search/short-url")
    public ResponseEntity<ResultResponse> getGeoInfoByShortUrl(@RequestParam(name = "shortUrl") String shortUrl) {
        NaverMapsDto.GeoInfo locationInfo = naverMapsService.getGeoInfoByShortUrl(shortUrl);

        ResultResponse resultResponse = ResultResponse.of(ResponseCode.GET_LOCATION_INFO_SUCCESS, locationInfo);
        return ResponseEntity.ok(resultResponse);
    }


    /**
     *
     * Test APIs
     *
     */


    @PostMapping("/location-info/coords")
    public ResponseEntity<ResultResponse> getPlaceDetailsCoords(@RequestParam(name = "fullUrl") String fullUrl) {
        NaverMapsDto.Coords placeDetails = naverMapsService.getPlaceDetailsCoords(fullUrl);

        ResultResponse resultResponse = ResultResponse.of(ResponseCode.GET_LOCATION_INFO_SUCCESS, placeDetails);
        return ResponseEntity.ok(resultResponse);
    }


}
