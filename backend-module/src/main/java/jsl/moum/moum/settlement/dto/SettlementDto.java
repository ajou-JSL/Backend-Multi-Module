package jsl.moum.moum.settlement.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jsl.moum.moum.settlement.domain.entity.SettlementEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SettlementDto {

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Request{
        @NotNull private String settlementName;
        @NotEmpty private int fee;
        @NotNull private Integer moumId;

        public SettlementEntity toEntity(){
            return SettlementEntity.builder()
                    .fee(fee)
                    .settlementName(settlementName)
                    .moumId(moumId)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Response{
        private final int settlementId;
        private final String settlementName;
        private final int fee;
        private final Integer moumId;


        public Response(SettlementEntity settlement){
            this.settlementId = settlement.getId();
            this.settlementName = settlement.getSettlementName();
            this.fee = settlement.getFee();
            this.moumId = settlement.getMoumId();
        }
    }
}
