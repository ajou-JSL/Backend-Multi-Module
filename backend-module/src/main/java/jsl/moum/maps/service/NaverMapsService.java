package jsl.moum.maps.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;
import jsl.moum.maps.dto.NaverMapsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class NaverMapsService {

    @Value("${naver.X-NCP-APIGW-API-KEY-ID}")
    private String clientId;

    @Value("${naver.X-NCP-APIGW-API-KEY}")
    private String clientSecret;

    @Value("${naver.maps-api-url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();


    public NaverMapsDto.GeoInfo getGeoInfoByShortUrl(String shortUrl) {
        try {
            // Expand the short URL
            String longUrl = expandUrl(shortUrl);

            // Get response from Naver Maps API
            ResponseEntity<String> response = fetchMapsFullUrlResponse(longUrl);

            // Get the coordinates from the response body
            NaverMapsDto.Coords coords = extractCoordsFromFullUrlResponse(response);

            // Get the address from the coordinates
            ResponseEntity<String> reverseGeocodeResponse = fetchReverseGeocodeResponse(coords.getLatitude(), coords.getLongitude());
            String address = extractAddressFromReverseGeocodeResponse(reverseGeocodeResponse);

            return NaverMapsDto.GeoInfo.builder()
                    .address(address)
//                    .address(json.path("results").get(0).path("region").path("area2").path("name").asText())
                    .latitude(coords.getLatitude())
                    .longitude(coords.getLongitude())
                    .build();

        } catch (Exception e) {
            log.error("Failed to get location info from URL: {}, {}", shortUrl, e);
            return null;
        }
    }

    private String extractAddressFromReverseGeocodeResponse(ResponseEntity<String> response) throws JsonProcessingException {

        // Navigate to the first result in the "results" array
        JsonNode json = new ObjectMapper().readTree(response.getBody());
        JsonNode resultNode = json.path("results").get(0);

        // Extract address components
        String area1 = resultNode.path("region").path("area1").path("name").asText(); // 경기도
        String area2 = resultNode.path("region").path("area2").path("name").asText(); // 수원시 영통구
        String area3 = resultNode.path("region").path("area3").path("name").asText(); // 이의동
        String number1 = resultNode.path("land").path("number1").asText(); // 839
        String number2 = resultNode.path("land").path("number2").asText(); // 3

        // Construct the address
        if(number2 == null || number2.isEmpty()){
            return String.format("%s %s %s %s", area1, area2, area3, number1);
        } else {
            return String.format("%s %s %s %s-%s", area1, area2, area3, number1, number2);
        }
    }

    public NaverMapsDto.GeoInfo getGeoInfoByQuery(String query) {
        log.info("getGeoInfoByQuery from query: {}", query);
        try {
            // Get response from Naver Maps API
            ResponseEntity<String> geocodeResponse = fetchGeocodeResponse(query);
            NaverMapsDto.GeoInfo geoInfoDto = extractGeoInfoFromResponse(geocodeResponse, query);
            return geoInfoDto;

        } catch (JsonProcessingException e) {
            log.error("Failed to get location info from URL: {}, {}", query, e);
            return null;
        }
    }

    public List<NaverMapsDto.GeoInfo> getGeoInfoListByQuery(String query){
        log.info("getGeoInfoListByQuery from query: {}", query);
        try {
            // Get response from Naver Maps API
            ResponseEntity<String> geocodeResponse = fetchGeocodeResponse(query);
            List<NaverMapsDto.GeoInfo> geoInfoList = extractGeoInfoListFromResponse(geocodeResponse, query);

            return geoInfoList;

        } catch (JsonProcessingException e) {
            log.error("Failed to get location info from URL: {}, {}", query, e);
            return null;
        }
    }

    public NaverMapsDto.Coords getPlaceDetailsCoords(String fullUrl) {
        log.info("getPlaceDetails from URL: {}", fullUrl);

        ResponseEntity<String> response = fetchMapsFullUrlResponse(fullUrl);

        // Extract the coordinates from the response
        NaverMapsDto.Coords coords = extractCoordsFromFullUrlResponse(response);
        return coords;
    }

    public NaverMapsDto.ClientInfo getClientInfo() {
        return NaverMapsDto.ClientInfo.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .apiUrl(apiUrl)
                .build();
    }

    /**
     *
     * Private access methods
     *
     */

    private String expandUrl(String shortUrl) throws Exception {
        log.info("expandUrl URL: {}", shortUrl);
        // Open a connection to the short URL
        HttpURLConnection connection = (HttpURLConnection) new URL(shortUrl).openConnection();

        // Do not automatically follow redirects
        connection.setInstanceFollowRedirects(false);

        // Connect to the URL
        connection.connect();

        // Check if the response code is a redirection
        int responseCode = connection.getResponseCode();
        if (responseCode >= 300 && responseCode < 400) {
            // Return the value of the Location header
            return connection.getHeaderField("Location");
        } else {
            throw new Exception("The URL did not return a redirection. Response code: " + responseCode);
        }
    }

    private NaverMapsDto.GeoInfo extractGeoInfoFromResponse(ResponseEntity<String> response, String query) throws JsonProcessingException {
        log.info("extractGeoInfoFromResponse from query: {}", query);

        // Parse JSON response into JsonNode
        JsonNode json = new ObjectMapper().readTree(response.getBody());
//        log.info("JsonNode: {}", json);

        // Navigate to "addresses" array and extract the first object
        JsonNode addressNode = json.path("addresses").get(0);

        // Extract required fields
        String roadAddress = addressNode.path("roadAddress").asText();
        String longitudeStr = addressNode.path("x").asText();
        String latitudeStr = addressNode.path("y").asText();

        Double longitude = Double.parseDouble(longitudeStr);
        Double latitude = Double.parseDouble(latitudeStr);

        return NaverMapsDto.GeoInfo.builder()
                .address(roadAddress)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }

    private List<NaverMapsDto.GeoInfo> extractGeoInfoListFromResponse(ResponseEntity<String> response, String query) throws JsonProcessingException {
        log.info("extractGeoInfoFromResponse from query: {}", query);

        // Parse JSON response into JsonNode
        JsonNode json = new ObjectMapper().readTree(response.getBody());
        log.info("extractGeoInfoListFromResponse parse JSON response: {}", json);

        // Navigate to "addresses" array
        JsonNode addressesNode = json.path("addresses");

        if (addressesNode == null || !addressesNode.isArray()) {
            log.warn("No addresses found for query: {}", query);
            return Collections.emptyList();
        }

        // Iterate through the array and build a list of GeoInfo
        List<NaverMapsDto.GeoInfo> geoInfoList = new ArrayList<>();
        for (JsonNode addressNode : addressesNode) {
            // Extract required fields
            String roadAddress = addressNode.path("roadAddress").asText("");
            String longitudeStr = addressNode.path("x").asText("");
            String latitudeStr = addressNode.path("y").asText("");

            // Convert to Double if not empty
            Double longitude = longitudeStr.isEmpty() ? null : Double.parseDouble(longitudeStr);
            Double latitude = latitudeStr.isEmpty() ? null : Double.parseDouble(latitudeStr);

            // Add GeoInfo to the list
            geoInfoList.add(NaverMapsDto.GeoInfo.builder()
                    .address(roadAddress)
                    .latitude(latitude)
                    .longitude(longitude)
                    .build());
        }

        return geoInfoList;
    }


    private NaverMapsDto.Coords extractCoordsFromFullUrlResponse(ResponseEntity<String> response) {
        log.info("extractCoordsFromFullUrlResponse from body");

        String body = response.getBody();
        // Pattern to match "latitude%5E" followed by numbers
        Pattern latitudePattern = Pattern.compile("latitude%5E([0-9.]+)");
        // Pattern to match "longitude%5E" followed by numbers
        Pattern longitudePattern = Pattern.compile("longitude%5E([0-9.]+)");

        // Pattern Matchers
        Matcher latitudeMatcher = latitudePattern.matcher(body);
        Matcher longitudeMatcher = longitudePattern.matcher(body);

        String latitudeString = null;
        String longitudeString = null;

        if (latitudeMatcher.find()) {
            latitudeString = latitudeMatcher.group(1);
        } else {
            log.error("No match found for latitude in the body.");
        }
        if (longitudeMatcher.find()) {
            longitudeString = longitudeMatcher.group(1);
        } else {
            log.error("No match found for longitude in the body.");
        }

        try {
            Double latitude = Double.parseDouble(latitudeString);
            Double longitude = Double.parseDouble(longitudeString);
            return new NaverMapsDto.Coords(latitude, longitude);
        } catch (Exception e){
            log.error("Failed to parse coordinates: {}", e.getMessage());
            throw new CustomException(ErrorCode.GET_LOCATION_INFO_FAIL);
        }
    }


    private ResponseEntity<String> fetchMapsFullUrlResponse(String fullUrl){
        log.info("fetchMapsFullUrlResponse from URL: {}", fullUrl);
        // Set headers for authentication
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-APIGW-API-KEY-ID", clientId);
        headers.set("X-NCP-APIGW-API-KEY", clientSecret);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Make the API call
        ResponseEntity<String> response = restTemplate.exchange(fullUrl, org.springframework.http.HttpMethod.GET, entity, String.class);
        return response;
    }


    private ResponseEntity<String> fetchReverseGeocodeResponse(double latitude, double longitude) {
        log.info("fetchReverseGeocodeResponse from coordinates: {}, {}", latitude, longitude);
        String url = apiUrl + "/map-reversegeocode/v2/gc?coords=" + longitude + "," + latitude + "&orders=addr&output=json";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-APIGW-API-KEY-ID", clientId);
        headers.set("X-NCP-APIGW-API-KEY", clientSecret);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, String.class);
        return response;
    }

    private ResponseEntity<String> fetchGeocodeResponse(String query) {
        log.info("fetchGeocodeResponse from query : {}", query);
        String url = apiUrl + "/map-geocode/v2/geocode?query=" + query;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-APIGW-API-KEY-ID", clientId);
        headers.set("X-NCP-APIGW-API-KEY", clientSecret);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, String.class);
        return response;
    }
}
