package jsl.moum.maps.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NaverMapsTestDto {


    private String name;
    private String address;
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
