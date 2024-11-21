package jsl.moum.record.domain.dto;

import jsl.moum.record.domain.entity.RecordEntity;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;


public class RecordDto {

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Request{
        private String recordName;
        private LocalDate startDate;
        private LocalDate endDate;


        public RecordEntity toEntity(){
            return RecordEntity.builder()
                    .recordName(recordName)
                    .startDate(startDate)
                    .endDate(endDate)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Response{
        private final int recordId;
        private final String recordName;
        private final LocalDate startDate;
        private final LocalDate endDate;


        public Response(RecordEntity recordEntity){
            this.recordId = recordEntity.getId();
            this.recordName = recordEntity.getRecordName();
            this.startDate = recordEntity.getStartDate();
            this.endDate = (recordEntity.getEndDate() != null) ? recordEntity.getEndDate() : null;
        }

    }
}
