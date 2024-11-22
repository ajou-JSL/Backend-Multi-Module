package jsl.moum.maps.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NaverMapsTestDto {


    private String name;
    private String address;
    private JsonNode json;
    private double latitude;
    private double longitude;


    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class PracticeRoom{

    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class PerformanceHall{

    }

}
