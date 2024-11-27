package jsl.moum.moum.lifecycle.dto;

import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.NotNull;
import jsl.moum.auth.dto.MemberDto;
import jsl.moum.auth.dto.MusicGenre;
import jsl.moum.moum.lifecycle.domain.entity.LifecycleEntity;
import jsl.moum.moum.lifecycle.domain.Music;
import jsl.moum.moum.lifecycle.domain.Process;
import jsl.moum.moum.team.domain.TeamMemberEntity;
import jsl.moum.record.domain.dto.RecordDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LifecycleDto {

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request{
        private String moumName;
        private String moumDescription;
        private String performLocation;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer price;
        private List<String> imageUrls = new ArrayList<>();

        @NotNull
        private int leaderId;

        @NotNull
        private int teamId; // 어느 팀의 라이프사이클인지 알아야하니까

        private List<Integer> members;

        @Nullable
        private List<RecordDto.Request> records;
        private Process process;
        private List<Music> music;
        private MusicGenre genre;


        public LifecycleEntity toEntity(){
            LifecycleEntity lifecycleEntity = LifecycleEntity.builder()
                    .lifecycleName(moumName)
                    .lifecycleDescription(moumDescription)
                    .performLocation(performLocation)
                    .startDate(startDate)
                    .endDate(endDate)
                    .price(price)
                    .imageUrls(imageUrls)
                    .leaderId(leaderId)
                    .process(process)
                    .music(music)
                    .genre(genre)
                    .build();
            if(records != null && !records.isEmpty()){
                lifecycleEntity.setRecords(records.stream()
                        .map(RecordDto.Request::toEntity)
                        .collect(Collectors.toList()));
            }
            return lifecycleEntity;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Response{
        private final int moumId;
        private final String moumName;
        private final String moumDescription;
        private final String performLocation;
        private final LocalDate startDate;
        private final LocalDate endDate;
        private final Integer price;
        private final List<String> imageUrls;
        private final int leaderId;
        private final String leaderName;
        private final int teamId;
        private final Process process;
        private final List<Music> music;
        private final List<MemberDto.Response> members;
        private final MusicGenre genre;

        public Response(LifecycleEntity lifecycle){
            this.moumId = lifecycle.getId();
            this.moumName = lifecycle.getLifecycleName();
            this.moumDescription = lifecycle.getLifecycleDescription();
            this.genre = lifecycle.getGenre();
            this.performLocation = lifecycle.getPerformLocation();
            this.startDate = lifecycle.getStartDate();
            this.endDate = lifecycle.getEndDate();
            this.price = lifecycle.getPrice();
            this.imageUrls = lifecycle.getImageUrls();
            this.leaderId = lifecycle.getLeaderId();
            this.leaderName = lifecycle.getLeaderName();
            this.teamId = lifecycle.getTeam().getId();
            this.process = lifecycle.getProcess();
            this.music = lifecycle.getMusic();

            this.members = lifecycle.getTeam().getMembers().stream()
                    .map(TeamMemberEntity::getMember)
                    .map(MemberDto.Response::new)
                    .collect(Collectors.toList());
        }
    }
}
