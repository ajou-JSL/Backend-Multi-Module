package jsl.moum.maps.service;

import jsl.moum.maps.dto.NaverMapsDto;
import jsl.moum.maps.dto.NaverMapsTestDto;
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



    public NaverMapsTestDto getLocationInfoTest(String shortUrl) {
        try {
            // Expand the short URL
            String longUrl = expandUrl(shortUrl);

            // Get the coordinates from the URL
            NaverMapsDto.Coords coords = getCoords(longUrl);

            // Get the address from the coordinates
            String address = getAddressReverseGeocode(coords.getLatitude(), coords.getLongitude());

            return NaverMapsTestDto.builder()
                    .latitude(coords.getLatitude())
                    .longitude(coords.getLongitude())
                    .address(address)
                    .build();

        } catch (Exception e) {
            log.error("Failed to get location info from URL: {}, {}", shortUrl, e);
            return null;
        }
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

    private NaverMapsDto.Coords getCoords(String longUrl) {
        log.info("getCoords from URL: {}", longUrl);
        Pattern pattern = Pattern.compile("c=(\\d+\\.\\d+),(\\d+\\.\\d+)");
        Matcher matcher = pattern.matcher(longUrl);

        if (matcher.find()) {
            double latitude = Double.valueOf(matcher.group(2));
            double longitude = Double.valueOf(matcher.group(1));
            return new NaverMapsDto.Coords(latitude, longitude);
        }
        return null;
    }

    private String getAddressReverseGeocode(double latitude, double longitude) {
        log.info("getAddressReverseGeocode from coordinates: {}, {}", latitude, longitude);
        String url = apiUrl + "/map-reversegeocode/v2/gc?coords=" + longitude + "," + latitude + "&orders=addr&output=json";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-APIGW-API-KEY-ID", clientId);
        headers.set("X-NCP-APIGW-API-KEY", clientSecret);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, String.class);

        return response.getBody();
    }
}
