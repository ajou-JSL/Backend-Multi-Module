package jsl.moum.moum.team.controller;

import jakarta.validation.Valid;
import jsl.moum.auth.domain.CustomUserDetails;
import jsl.moum.auth.dto.MemberDto;
import jsl.moum.auth.dto.MusicGenre;
import jsl.moum.global.error.exception.NeedLoginException;
import jsl.moum.global.response.ResponseCode;
import jsl.moum.global.response.ResultResponse;
import jsl.moum.moum.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jsl.moum.moum.team.dto.TeamDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    /**
     * 팀 조회(ID로) API
     */
    @GetMapping("/api/teams/{teamId}")
    public ResponseEntity<ResultResponse> getTeamById(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                      @PathVariable int teamId){

        loginCheck(customUserDetails.getUsername());
        TeamDto.Response teamResponseDto = teamService.getTeamById(teamId);
        ResultResponse response = ResultResponse.of(ResponseCode.GET_TEAM_SUCCESS, teamResponseDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * 팀 조회(이름으로) API
     */
    @GetMapping("/api/teams/name/{teamName}")
    public ResponseEntity<ResultResponse> getTeamByTeamName(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                      @PathVariable String teamName){

        loginCheck(customUserDetails.getUsername());
        TeamDto.Response teamResponseDto = teamService.getTeamByTeamName(teamName);
        ResultResponse response = ResultResponse.of(ResponseCode.GET_TEAM_SUCCESS, teamResponseDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }


    /**
     * 팀 목록 조회 API
     */
    @GetMapping("/api/teams-all")
    public ResponseEntity<ResultResponse> getTeamList(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size){

        loginCheck(customUserDetails.getUsername());
        List<TeamDto.Response> teamListResponseDto = teamService.getTeamList(page, size);
        ResultResponse response = ResultResponse.of(ResponseCode.GET_TEAM_LIST_SUCCESS, teamListResponseDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * 팀 생성 API
     */
    @PostMapping("/api/teams")
    public ResponseEntity<ResultResponse> createTeam(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                         @Valid @RequestPart TeamDto.Request teamRequestDto,
                                                     @RequestPart(value = "file", required = false)MultipartFile file) throws IOException {

        String loginUserName = loginCheck(customUserDetails.getUsername());
        TeamDto.Response teamResponseDto = teamService.createTeam(teamRequestDto, loginUserName, file);
        ResultResponse response = ResultResponse.of(ResponseCode.CREATE_TEAM_SUCCESS, teamResponseDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * 팀 정보 수정 API
     */
    @PatchMapping("/api/teams/{teamId}")
    public ResponseEntity<ResultResponse> updateTeam(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                     @PathVariable int teamId,
                                                     @Valid @RequestPart TeamDto.UpdateRequest updateRequestDto,
                                                     @PathVariable(value = "file", required = false)MultipartFile file) throws IOException {

        String loginUserName = loginCheck(customUserDetails.getUsername());
        TeamDto.UpdateResponse responseDto = teamService.updateTeamInfo(teamId,updateRequestDto,loginUserName, file);
        ResultResponse response = ResultResponse.of(ResponseCode.UPDATE_TEAM_SUCCESS, responseDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));

    }

    /**
     * 팀에 멤버 초대 API
     */
    // 초대 요청 보내기가 아니고 초대 하기임. 이거 요청보내면 타겟멤버가 팀이 되는거임
    @PostMapping("/api/teams/{teamId}/invite/{memberId}")
    public ResponseEntity<ResultResponse> inviteMember(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                       @PathVariable int teamId,
                                                       @PathVariable int memberId) {

        String loginUserName = loginCheck(customUserDetails.getUsername());
        MemberDto.Response memberResponseDto = teamService.inviteMember(teamId, memberId, loginUserName);
        ResultResponse response = ResultResponse.of(ResponseCode.INVITE_MEMBER_SUCCESS, memberResponseDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * 팀 해체 API
     */
    @DeleteMapping("/api/teams/{teamId}")
    public ResponseEntity<ResultResponse> deleteTeamById(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                               @PathVariable int teamId){

        String loginUserName = loginCheck(customUserDetails.getUsername());
        TeamDto.Response teamResponseDto = teamService.deleteTeamById(teamId, loginUserName);
        ResultResponse response = ResultResponse.of(ResponseCode.DELETE_TEAM_SUCCESS, teamResponseDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * 유저로부터 온 초대 요청 수락 API
     */
    @PostMapping("/api/teams/{teamId}/accept/{memberId}")
    public void 초대요청수락(){
    }

    /**
     * 유저로부터 온 초대 요청 거절 API
     *
     * @param customUserDetails 현재 인증된 사용자 정보 (CustomUserDetails 객체에서 사용자 정보 추출)
     * @param
     *
     */
    @PostMapping("/api/teams/{teamId}/reject/{memberId}")
    public void 초대요청거절(){
    }

    /**
     * 팀에서 멤버 강퇴 API
     */
    @DeleteMapping("/api/teams/{teamId}/kick/{memberId}")
    public ResponseEntity<ResultResponse> kickMemberFromTeam(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                   @PathVariable int teamId,
                                   @PathVariable int memberId){

        String loginUserName = loginCheck(customUserDetails.getUsername());
        TeamDto.Response responseDto = teamService.kickMemberById(memberId, teamId,loginUserName);
        ResultResponse response = ResultResponse.of(ResponseCode.KICK_MEMBER_SUCCESS, responseDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * 팀에서 탈퇴 API
     */
    @DeleteMapping("/api/teams/leave/{teamId}")
    public ResponseEntity<ResultResponse> leaveTeam(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                          @PathVariable int teamId){

        String loginUserName = loginCheck(customUserDetails.getUsername());
        TeamDto.Response responseDto = teamService.leaveTeam(teamId, loginUserName);
        ResultResponse response = ResultResponse.of(ResponseCode.LEAVE_TEAM_SUCCESS, responseDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * 팀 리더 양도 API
     */
    @PatchMapping("/api/teams/change-leader/{memberId}")
    public void 팀리더넘기기(){
    }


    /**
     * 멤버의 팀 리스트 찾기 API
     */
    @GetMapping("/api/teams-all/{memberId}")
    public ResponseEntity<ResultResponse> getMemberTeamList(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                            @PathVariable int memberId)
    {
        loginCheck(customUserDetails.getUsername());
        List<TeamDto.Response> responseDto = teamService.getTeamsByMemberId(memberId);
        ResultResponse response = ResultResponse.of(ResponseCode.GET_TEAM_LIST_SUCCESS,responseDto);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
    }

    /**
     * 필터링으로 팀 리스트 조회
     */
    @GetMapping("/api/teams/search")
    public ResponseEntity<ResultResponse> getTeamsWithFiltering(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                            @RequestParam(required = false) String keyword, @RequestParam(required = false) MusicGenre genre,
                                                            @RequestParam(required = false) String location, @RequestParam(required = false) Boolean filterByExp,
                                                                @RequestParam(required = false) Boolean filterByMembersCount,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size)
    {
        loginCheck(customUserDetails.getUsername());
        TeamDto.SearchDto searchDto = TeamDto.SearchDto.builder()
                .keyword(keyword)
                .location(location)
                .genre(genre)
                .filterByExp(filterByExp)
                .filterByMembersCount(filterByMembersCount)
                .build();
        Page<TeamDto.Response> responseDto = teamService.getTeamsWithFiltering(searchDto, page, size);
        ResultResponse response = ResultResponse.of(ResponseCode.GET_TEAM_LIST_SUCCESS,responseDto);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
    }


    public String loginCheck(String username){
        if(username == null){
            throw new NeedLoginException();
        }

        return username;
    }

}
