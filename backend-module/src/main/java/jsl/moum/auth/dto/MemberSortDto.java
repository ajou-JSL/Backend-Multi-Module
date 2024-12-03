package jsl.moum.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class MemberSortDto {

    @Getter
    @AllArgsConstructor
    public static class RecordsCountResponse {
        private Integer memberId;
        private String memberName;
        private String memberUsername;
        private int totalRecordCount;
    }

    @Getter
    @AllArgsConstructor
    public static class ExpResponse {
        private Integer memberId;
        private String memberName;
        private String memberUsername;
        private String fileUrl;
        private int exp;
        private String tier;
    }
}
