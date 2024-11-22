package jsl.moum.maps.controller;

import jsl.moum.global.response.ResponseCode;
import jsl.moum.global.response.ResultResponse;
import jsl.moum.maps.dto.NaverMapsDto;
import jsl.moum.maps.service.NaverMapsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/maps")
@Slf4j
public class NaverMapsController {

    private final NaverMapsService naverMapsService;


    @PostMapping("/search/geo-info")
    public ResponseEntity<ResultResponse> getGeoInfoByAddressQuery(@RequestParam(name = "query") String query) {
        NaverMapsDto.GeoInfo locationInfo = naverMapsService.getGeoInfoByQuery(query);

        ResultResponse resultResponse = ResultResponse.of(ResponseCode.GET_LOCATION_INFO_SUCCESS, locationInfo);
        return ResponseEntity.ok(resultResponse);
    }

    @PostMapping("/search/geo-info/list")
    public ResponseEntity<ResultResponse> getGeoInfoListByAddressQuery(@RequestParam(name = "query") String query) {
        List<NaverMapsDto.GeoInfo> locationInfo = naverMapsService.getGeoInfoListByQuery(query);

        ResultResponse resultResponse = ResultResponse.of(ResponseCode.GET_LOCATION_INFO_SUCCESS, locationInfo);
        return ResponseEntity.ok(resultResponse);
    }

    @PostMapping("/search/short-url")
    public ResponseEntity<ResultResponse> getGeoInfoByShortUrl(@RequestParam(name = "shortUrl") String shortUrl) {
        NaverMapsDto.GeoInfo locationInfo = naverMapsService.getGeoInfoByShortUrl(shortUrl);

        ResultResponse resultResponse = ResultResponse.of(ResponseCode.GET_LOCATION_INFO_SUCCESS, locationInfo);
        return ResponseEntity.ok(resultResponse);
    }

    @GetMapping("/search/model-view")
    public String searchModelView(Model model) {

        NaverMapsDto.ClientInfo clientInfo = naverMapsService.getClientInfo();
        String clientId = clientInfo.getClientId();
        String clientSecret = clientInfo.getClientSecret();

        model.addAttribute("clientId", clientId);
        model.addAttribute("clientSecret", clientSecret);

        return "naverMapsSearchView";
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
