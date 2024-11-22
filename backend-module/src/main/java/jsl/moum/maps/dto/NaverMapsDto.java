package jsl.moum.maps.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NaverMapsDto {

    private String name;
    private String address;
    private double latitude;
    private double longitude;


    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class Coords {
        private double latitude;
        private double longitude;

        public Coords(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    @Builder
    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class GeoInfo {
        private String address;
        private double latitude;
        private double longitude;

        public GeoInfo(String address, double latitude, double longitude) {
            this.address = address;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    @Builder
    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class ClientInfo {
        private String clientId;
        private String clientSecret;
        private String apiUrl;

        public ClientInfo(String clientId, String clientSecret, String apiUrl) {
            this.clientId = clientId;
            this.clientSecret = clientSecret;
            this.apiUrl = apiUrl;
        }
    }


}
