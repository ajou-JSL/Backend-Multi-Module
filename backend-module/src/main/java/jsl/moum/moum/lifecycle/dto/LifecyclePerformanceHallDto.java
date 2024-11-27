package jsl.moum.moum.lifecycle.dto;

import jsl.moum.moum.lifecycle.domain.entity.LifecyclePerformanceHall;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LifecyclePerformanceHallDto {
    private int id;
    private int moumId;
    private Integer hallId;
    private String hallName;

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private int moumId;
        private Integer hallId;
        private String hallName;
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private int id;
        private int moumId;
        private Integer hallId;
        private String hallName;

        public Response(LifecyclePerformanceHall lifecyclePerformanceHall) {
            this.id = lifecyclePerformanceHall.getId();
            this.moumId = lifecyclePerformanceHall.getMoum().getId();
            this.hallId = lifecyclePerformanceHall.getHallId();
            this.hallName = lifecyclePerformanceHall.getPerformanceHall();
        }
    }

}
