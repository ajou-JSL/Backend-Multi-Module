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
}
