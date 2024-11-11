package jsl.moum.record.domain.dto;

import jsl.moum.record.domain.entity.RecordEntity;
import lombok.*;
import java.util.Date;


public class RecordDto {

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Request{
        private String recordName;
        private Date startDate;
        private Date endDate;


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
        private final Date startDate;
        private final Date endDate;


        public Response(RecordEntity recordEntity){
            this.recordId = recordEntity.getId();
            this.recordName = recordEntity.getRecordName();
            this.startDate = recordEntity.getStartDate();
            this.endDate = recordEntity.getEndDate();
        }

    }
}
