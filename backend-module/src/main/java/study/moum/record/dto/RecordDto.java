package study.moum.record.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import study.moum.record.domain.RecordEntity;

import java.time.LocalDate;

public class RecordDto {

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Request{
        private String recordName;
//        private int memberId;
//        private int recordId;
        private LocalDate startDate;
        private LocalDate endDate;
//        private List<MemberRecordEntity> members;

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
        private String recordName;
        private LocalDate startDate;
        private LocalDate endDate;
        private int recordId;

        public Response(RecordEntity record){
            this.recordId = record.getId();
            this.recordName = record.getRecordName();
            this.startDate = record.getStartDate();
            this.endDate = record.getEndDate();
        }

    }
}
