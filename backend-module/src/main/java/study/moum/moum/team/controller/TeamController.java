package study.moum.moum.team.controller;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import study.moum.auth.domain.CustomUserDetails;
import study.moum.auth.domain.entity.MemberEntity;
import study.moum.auth.dto.MemberDto;
import study.moum.global.error.exception.NeedLoginException;
import study.moum.global.response.ResponseCode;
import study.moum.global.response.ResultResponse;
import study.moum.moum.team.dto.TeamDto;
import study.moum.moum.team.service.TeamService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    /**
     * 팀 조회 API
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
                                                     @Valid @RequestBody TeamDto.Request teamRequestDto){

        String loginUserName = loginCheck(customUserDetails.getUsername());
        TeamDto.Response teamResponseDto = teamService.createTeam(teamRequestDto, loginUserName);
        ResultResponse response = ResultResponse.of(ResponseCode.CREATE_TEAM_SUCCESS, teamResponseDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * 팀 정보 수정 API
     */
    @PatchMapping("/api/teams/{teamId}")
    public ResponseEntity<ResultResponse> updateTeam(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                     @PathVariable int teamId,
                                                     @RequestBody TeamDto.UpdateRequest updateRequestDto){

        String loginUserName = loginCheck(customUserDetails.getUsername());
        TeamDto.UpdateResponse responseDto = teamService.updateTeamInfo(teamId,updateRequestDto,loginUserName);
        ResultResponse response = ResultResponse.of(ResponseCode.UPDATE_TEAM_SUCCESS, responseDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));

    }


    // todo : 멤버 초대하기 -> 알람발송, 팀 가입요청 수락 -> 가입성공 으로 로직 변경
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

    public String loginCheck(String username){
        if(username == null){
            throw new NeedLoginException();
        }

        return username;
    }

}
