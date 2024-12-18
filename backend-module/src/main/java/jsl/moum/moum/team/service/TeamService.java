package jsl.moum.moum.team.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.domain.repository.MemberRepository;
import jsl.moum.auth.dto.MemberDto;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;
import jsl.moum.moum.lifecycle.domain.entity.LifecycleEntity;
import jsl.moum.moum.lifecycle.domain.repository.LifecycleRepository;
import jsl.moum.moum.lifecycle.domain.entity.LifecycleRepositoryCustom;
import jsl.moum.moum.team.domain.*;
import jsl.moum.objectstorage.StorageService;
import jsl.moum.record.domain.dto.RecordDto;
import jsl.moum.record.domain.entity.RecordEntity;
import jsl.moum.record.domain.repository.RecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jsl.moum.moum.team.dto.TeamDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamService {

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamMemberRepositoryCustom teamMemberRepositoryCustom;
    private final StorageService storageService;
    private final RecordRepository recordRepository;
    private final LifecycleRepository lifecycleRepository;
    private final LifecycleRepositoryCustom lifecycleRepositoryCustom;
    private final TeamRepositoryCustom teamRepositoryCustom;
    private final ObjectMapper objectMapper;

    @Value("${ncp.object-storage.bucket}")
    private String bucket;

    /**
     * 팀 정보 조회
     **/
    @Transactional(readOnly = true)
    public TeamDto.Response getTeamById(int teamId) {

        TeamEntity team = findTeam(teamId);
        return new TeamDto.Response(team);
    }

    /**
     * 팀 정보 조회
     **/
    @Transactional(readOnly = true)
    public TeamDto.Response getTeamByTeamName(String teamName) {

        TeamEntity team = teamRepository.findByTeamName(teamName);
        return new TeamDto.Response(team);
    }

    /**
     * 팀 리스트 조회
     **/
    @Transactional(readOnly = true)
    public List<TeamDto.Response> getTeamList(int page, int size) {

//        List<TeamEntity> teams = teamRepository.findAll(PageRequest.of(page, size, Sort.sort(c))).getContent();
        List<TeamEntity> teams = teamRepository.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        ).getContent();


        List<TeamDto.Response> teamsList = teams.stream()
                .map(TeamDto.Response::new)
                .collect(Collectors.toList());

        return teamsList;
    }

    /**
     * 팀 생성
     */
    @Transactional
    public TeamDto.Response createTeam(TeamDto.Request teamRequestDto, String username, MultipartFile file) throws IOException {

        MemberEntity loginUser = memberRepository.findByUsername(username);

        long existingTeamCount = teamRepositoryCustom.countCreatedTeamByMemberId(loginUser.getId());
        log.info("============ 팀 개 : {}",existingTeamCount);
        if (existingTeamCount >= 3) {
            throw new CustomException(ErrorCode.MAX_TEAM_LIMIT_EXCEEDED);

        }

        String fileUrl = null;
        if(file!=null && !file.isEmpty()){
            // "teams/{teamName}/{originalFileName}"
            String originalFilename = file.getOriginalFilename();
            String key = "teams/" + teamRequestDto.getTeamName() + "/" + originalFilename;
            fileUrl = storageService.uploadImage(key, file);
        }

        TeamDto.Request request = TeamDto.Request.builder()
                .members(new ArrayList<>())
                .teamName(teamRequestDto.getTeamName())
                .description(teamRequestDto.getDescription())
                .genre(teamRequestDto.getGenre())
                .location(teamRequestDto.getLocation())
                .leaderId(loginUser.getId())
                .fileUrl(fileUrl)
                .videoUrl(teamRequestDto.getVideoUrl())
                .records(teamRequestDto.getRecords())
                .build();

        TeamEntity newTeam = request.toEntity();

        List<RecordEntity> records = newTeam.getRecords();
        if (records != null && !records.isEmpty()) {
            for (RecordEntity record : records) {
                record.setTeam(newTeam);
            }
            recordRepository.saveAll(records);
        }

        teamRepository.save(newTeam);

        newTeam.updateTeamExpAndRank(1);

        TeamMemberEntity teamMember = TeamMemberEntity.builder()
                .team(newTeam)
                .member(loginUser)
                .leaderId(loginUser.getId())
                .build();

        teamMemberRepository.save(teamMember);
        return new TeamDto.Response(newTeam);
    }


    /**
     * 팀에 멤버 초대
     */
    @Transactional
    public MemberDto.Response inviteMember(int teamId, int targetMemberId, String username) {

        MemberEntity loginUser = memberRepository.findByUsername(username);
        TeamEntity team = findTeam(teamId);

        // 팀의 리더인지 확인
        if (!checkLeader(team, loginUser)) {
            throw new CustomException(ErrorCode.NO_AUTHORITY);
        }

        // 멤버 찾기
        MemberEntity targetMember = memberRepository.findById(targetMemberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_EXIST));

        // 이미 팀 멤버면 에러
        if (isTeamMember(teamId, targetMemberId)) {
            throw new CustomException(ErrorCode.MEMBER_ALREADY_INVITED);
        }

        // 팀 멤버 초대 로직
        TeamMemberEntity teamMember = TeamMemberEntity.builder()
                .member(targetMember)
                .leaderId(loginUser.getId())
                .team(team)
                .build();

        // 팀 멤버 저장
        teamMemberRepository.save(teamMember);

        team.updateTeamExpAndRank(1);
        updateAllMembersExp(teamId,1);

        return new MemberDto.Response(targetMember); // 팀 정보 반환
    }


    /**
     * 팀 정보 수정 메소드
     */
    @Transactional
    public TeamDto.UpdateResponse updateTeamInfo(int teamId, TeamDto.UpdateRequest teamUpdateRequestDto, String username, MultipartFile file) throws IOException {

        MemberEntity leader = memberRepository.findByUsername(username);
        TeamEntity team = findTeam(teamId);

        if (!checkLeader(team, leader)) {
            throw new CustomException(ErrorCode.NO_AUTHORITY);
        }

        String existingFileUrl = team.getFileUrl();

        if (file != null && !file.isEmpty()) {
            if (existingFileUrl != null && !existingFileUrl.isEmpty()) {
                String existingFileName = existingFileUrl.replace("https://kr.object.ncloudstorage.com/" + bucket + "/", "");
                storageService.deleteFile(existingFileName);
            }

            // 새로운 파일 업로드
            String newFileName = "teams/" + team.getTeamName() + "/" + file.getOriginalFilename();
            String newFileUrl = storageService.uploadImage(newFileName, file);
            team.updateProfileImage(newFileUrl);
        }


        if (teamUpdateRequestDto.getRecords() != null) {
            List<RecordEntity> updatedRecords = teamUpdateRequestDto.getRecords().stream()
                    .map(RecordDto.Request::toEntity)
                    .collect(Collectors.toList());
            team.updateRecords(updatedRecords);
        }

        List<RecordEntity> records = team.getRecords();
        if (records != null && !records.isEmpty()) {
            for (RecordEntity record : records) {
                record.setTeam(team);
            }
            recordRepository.saveAll(records);
        }

        team.updateTeamInfo(teamUpdateRequestDto);

        return new TeamDto.UpdateResponse(team);

    }

    /**
     * 팀 해체(삭제) 메소드
     */
    @Transactional
    public TeamDto.Response deleteTeamById(int teamId, String username) {
        MemberEntity leader = memberRepository.findByUsername(username);
        TeamEntity targetTeam = findTeam(teamId);

        if (!checkLeader(targetTeam, leader)) {
            throw new CustomException(ErrorCode.NO_AUTHORITY);
        }

        String fileUrl = targetTeam.getFileUrl();
        if (fileUrl != null && !fileUrl.isEmpty()) {
            String fileName = fileUrl.replace("https://kr.object.ncloudstorage.com/" + bucket + "/", "");
            fileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8);
            storageService.deleteFile(fileName);
        }

        List<LifecycleEntity> moumList = lifecycleRepositoryCustom.findLifecyclesByTeamId(teamId);
        if (moumList != null && !moumList.isEmpty()) {
            lifecycleRepository.deleteAll(moumList);
            for(LifecycleEntity moum : moumList){
                moum.removeTeam(targetTeam);
            }
        }

        teamMemberRepositoryCustom.deleteTeamMemberTable(teamId);
        teamRepository.deleteById(teamId);

        targetTeam.updateTeamExpAndRank(-1);
        updateAllMembersExp(teamId,-1);

        return new TeamDto.Response(targetTeam);

    }


    /**
     * 팀에서 멤버 강퇴 메소드
     */
    @Transactional
    public TeamDto.Response kickMemberById(int targetMemberId, int teamId, String username) {

        // 타겟멤버
        MemberEntity targetMember = memberRepository.findById(targetMemberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_EXIST));
        TeamEntity team = findTeam(teamId);

        MemberEntity leader = memberRepository.findByUsername(username);
        // 로그인 유저가 리더 아니면 에러. 대상이 리더가 아니라.
        if (!checkLeader(team, leader)) {
            throw new CustomException(ErrorCode.NO_AUTHORITY);
        }

        // 강퇴 대상 멤버 찾기
        if (!isMemberExist(targetMemberId)) {
            throw new CustomException(ErrorCode.MEMBER_NOT_EXIST);
        }

        // 팀 멤버가 아니면 에러
        if (!isTeamMember(teamId, targetMemberId)) {
            throw new CustomException(ErrorCode.NOT_TEAM_MEMBER);
        }

        // 강퇴 대상 멤버가 팀의 멤버인지 확인
        teamMemberRepositoryCustom.deleteMemberFromTeamById(teamId, targetMemberId);

        team.updateTeamExpAndRank(-1);
        updateAllMembersExp(teamId,-1);

        return new TeamDto.Response(team);
    }

    /**
     * 팀에서 탈퇴 메소드
     */
    @Transactional
    public TeamDto.Response leaveTeam(int teamId, String username) {
        MemberEntity member = memberRepository.findByUsername(username);
        TeamEntity team = findTeam(teamId);

        if (checkLeader(team, member)) {
            throw new CustomException(ErrorCode.LEADER_CANNOT_LEAVE);
        }

        if (!isTeamMember(teamId, member.getId())) {
            throw new CustomException(ErrorCode.NOT_TEAM_MEMBER);
        }

        team.updateTeamExpAndRank(-1);
        updateAllMembersExp(teamId,-1);

        teamMemberRepositoryCustom.deleteMemberFromTeamById(teamId, member.getId());
        return new TeamDto.Response(team);
    }

    /**
     * 멤버의 팀 리스트 조회
     */
    @Transactional
    public List<TeamDto.Response> getTeamsByMemberId(int memberId) {

        if (!isMemberExist(memberId)) {
            throw new CustomException(ErrorCode.MEMBER_NOT_EXIST);
        }

        return teamMemberRepositoryCustom.findAllTeamsByMemberId(memberId);
    }

    /**
     * 필터링으로 팀 목록 조회
     */
    @Transactional
    public Page<TeamDto.Response> getTeamsWithFiltering(TeamDto.SearchDto searchDto, int page, int size) {

//        TeamDto.SearchDto searchDto = null;
//        log.info("encodedString : {}", encodedString);
//        if (encodedString != null) {
//            try {
//                log.info("try 진입");
//                String decodedString = new String(Base64.getDecoder().decode(encodedString));
//                log.info("decodedString : {}", decodedString);
//                searchDto = objectMapper.readValue(decodedString, TeamDto.SearchDto.class);
//                log.info("searchDto : {}", searchDto);
//            } catch (IllegalArgumentException | JsonProcessingException e) {
//                log.error(e.getMessage());
//                throw new CustomException(ErrorCode.BASE64_PROCESS_FAIL);
//            }
//        }
        Pageable pageable = PageRequest.of(page, size);
        Page<TeamEntity> teams = teamRepositoryCustom.searchTeamsWithFiltering(searchDto, pageable);

        Page<TeamDto.Response> teamsResponseList = teams
                .map(TeamDto.Response::new);

        return teamsResponseList;
    }


    /**
     * 유저로부터 온 초대 요청 수락 API
     *
     * @param customUserDetails 현재 인증된 사용자 정보 (CustomUserDetails 객체에서 사용자 정보 추출)
     * @param
     *
     */
    @Transactional
    public void 초대요청수락(){
    }


    /**
     * 유저로부터 온 초대 요청 거절 API
     *
     * @param customUserDetails 현재 인증된 사용자 정보 (CustomUserDetails 객체에서 사용자 정보 추출)
     * @param
     *
     */
    @Transactional
    public void 초대요청거절(){
    }

    public TeamEntity findTeam(int teamId){
        TeamEntity team = teamRepository.findById(teamId)
                .orElseThrow(()-> new CustomException(ErrorCode.TEAM_NOT_FOUND));

        return team;
    }

    public Boolean checkLeader(TeamEntity team, MemberEntity loginUser){
        if (team.getLeaderId() != loginUser.getId()) {
            return false;
        }

        return true;
    }

    public Boolean isTeamMember(int teamId, int memberId){
        boolean isAlreadyMember = teamMemberRepositoryCustom.existsByTeamAndMember(teamId, memberId);
        if (isAlreadyMember) {
            return true;
        }
        return false;
    }

    public Boolean isMemberExist(String username) {
        MemberEntity member = memberRepository.findByUsername(username);
        if(member == null){
            return false;
        }
        return true;
    }

    public Boolean isMemberExist(int memberId) {
        Boolean member = memberRepository.findById(memberId).isPresent();
        if(!member){
            return false;
        }
        return true;
    }

    public void updateAllMembersExp(int teamId, int exp){
        List<MemberEntity> members = teamMemberRepositoryCustom.findAllMembersByTeamId(teamId);
        if(members != null && !members.isEmpty()){
            for(MemberEntity m: members){
                m.updateMemberExpAndRank(exp);
            }
        }
    }
}
