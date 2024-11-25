package jsl.moum.moum.lifecycle.service;

import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.domain.repository.MemberRepository;
import jsl.moum.auth.dto.MemberDto;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;
import jsl.moum.moum.lifecycle.domain.*;
import jsl.moum.moum.lifecycle.domain.Process;
import jsl.moum.moum.lifecycle.dto.LifecycleDto;
import jsl.moum.moum.lifecycle.dto.LifecyclePerformanceHallDto;
import jsl.moum.moum.lifecycle.dto.LifecyclePracticeRoomDto;
import jsl.moum.moum.lifecycle.dto.ProcessDto;
import jsl.moum.moum.team.domain.*;
import jsl.moum.moum.team.dto.TeamDto;
import jsl.moum.objectstorage.StorageService;
import jsl.moum.record.domain.dto.RecordDto;
import jsl.moum.record.domain.entity.MoumMemberRecordEntity;
import jsl.moum.record.domain.entity.RecordEntity;
import jsl.moum.record.domain.repository.MoumMemberRecordRepository;
import jsl.moum.record.domain.repository.MoumMemberRecordRepositoryCustom;
import jsl.moum.record.domain.repository.RecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LifecycleService {

    private final LifecycleRepository lifecycleRepository;
    private final LifecycleRepositoryCustom lifecycleRepositoryCustom;
    private final MemberRepository memberRepository;
    private final TeamMemberRepositoryCustom teamMemberRepositoryCustom;
    private final StorageService storageService;
    private final TeamRepository teamRepository;
    private final RecordRepository recordRepository;
    private final MoumMemberRecordRepository moumMemberRecordRepository;
    private final MoumMemberRecordRepositoryCustom moumMemberRecordRepositoryCustom;
    private final LifecyclePracticeRoomRepository lifecyclePracticeRoomRepository;
    private final LifecyclePerformanceHallRepository lifecyclePerformanceHallRepository;

    @Value("${ncp.object-storage.bucket}")
    private String bucket;

    /**
     * 모음 단건 조회
     */
    @Transactional(readOnly = true)
    public LifecycleDto.Response getMoumById(String username, int moumId){

        LifecycleEntity moumEntity = findMoum(moumId);

        if(!hasTeam(username)){
            throw new CustomException(ErrorCode.NEED_TEAM);
        }

        return new LifecycleDto.Response(moumEntity);
    }

    /**
     * 나의 모음 리스트 조회
     */
    @Transactional(readOnly = true)
    public List<LifecycleDto.Response> getMyMoumList(String username){

        List<LifecycleEntity> lifecycles = lifecycleRepositoryCustom.findLifecyclesByUsername(username);

        return lifecycles.stream()
                .map(LifecycleDto.Response::new)
                .collect(Collectors.toList());
    }

    /**
     * 팀의 모음 리스트 조회
     */
    @Transactional(readOnly = true)
    public List<LifecycleDto.Response> getTeamMoumList(int teamId){

        findTeam(teamId);
        List<LifecycleEntity> lifecycles = lifecycleRepositoryCustom.findLifecyclesByTeamId(teamId);

        return lifecycles.stream()
                .map(LifecycleDto.Response::new)
                .collect(Collectors.toList());
    }

    /**
     * 모음 생성
     */
    @Transactional
    public LifecycleDto.Response addMoum(String username, LifecycleDto.Request requestDto, List<MultipartFile> files) throws IOException {

        MemberEntity loginUser = findLoginUser(username);
        TeamEntity team = findTeam(requestDto.getTeamId());

        if(!hasTeam(username)){
            throw new CustomException(ErrorCode.NEED_TEAM);
        }

        if(!isTeamLeader(username)){
            throw new CustomException(ErrorCode.NO_AUTHORITY);
        }

        long existingLifecycleCount = lifecycleRepositoryCustom.countCreatedLifecycleByTeamId(team.getId());
        if (existingLifecycleCount >= 3) {
            throw new CustomException(ErrorCode.MAX_MOUM_LIMIT_EXCEEDED);
        }

        // "moums/{moumName}/{originalFileName}"
        List<String> fileUrls = uploadFiles(files, requestDto.getMoumName());

        LifecycleEntity newMoum = LifecycleDto.Request.builder()
                .moumName(requestDto.getMoumName())
                .price(requestDto.getPrice())
                .moumDescription(requestDto.getMoumDescription())
                .imageUrls(fileUrls)
                .performLocation(requestDto.getPerformLocation())
                .startDate(requestDto.getStartDate())
                .endDate(requestDto.getEndDate())
                .leaderId(loginUser.getId())
                .records(requestDto.getRecords())
                //.music(requestDto.getMusic())
                .genre(requestDto.getGenre())
                .build().toEntity();

        newMoum.assignTeam(team);
        newMoum.assignProcess(requestDto.getProcess());

        List<RecordEntity> records = newMoum.getRecords();
        if (records != null && !records.isEmpty()) {
            for (RecordEntity record : records) {
                record.setLifecycle(newMoum);
            }
            recordRepository.saveAll(records);
        }

       lifecycleRepository.save(newMoum);


       return new LifecycleDto.Response(newMoum);
    }

    /**
     * 모음 정보 수정
     */
    @Transactional
    public LifecycleDto.Response updateMoum(String username, LifecycleDto.Request requestDto, List<MultipartFile> files, int moumId) throws IOException {

        log.info("updateMoum() 진입");

        MemberEntity loginUser = memberRepository.findByUsername(username);
        TeamEntity team = findTeam(requestDto.getTeamId());
        LifecycleEntity lifecycle = findMoum(moumId);

        if (!isTeamLeader(loginUser.getUsername())) {
            throw new CustomException(ErrorCode.NO_AUTHORITY);
        }

        List<String> existingFileUrls = lifecycle.getImageUrls();
        if(files == null){
            throw new CustomException(ErrorCode.FILE_UPDATE_FAIL);
        } else if(files.get(0).getSize() != 0){  // files.get(0).getSize() != 0
            log.info("if(files.get(0).getSize() != 0 || files != null)");
            deleteExistingFiles(existingFileUrls);
            // "moums/{moumName}/{originalFileName}"
            List<String> newFileUrls = uploadFiles(files, requestDto.getMoumName());
            lifecycle.updateProfileImages(newFileUrls);
        }


        updateLifecycleRecords(requestDto, lifecycle);

        lifecycle.updateLifecycleInfo(requestDto);

        return new LifecycleDto.Response(lifecycle);

    }

    /**
     * 모음 삭제
     */
    @Transactional
    public LifecycleDto.Response deleteMoum(String username, int moumId) {

        if (!isTeamLeader(username)) {
            throw new CustomException(ErrorCode.NO_AUTHORITY);
        }

        LifecycleEntity targetMoum = findMoum(moumId);
        findTeam(targetMoum.getTeam().getId());

        // "moums/{moumName}/{originalFileName}"
        List<String> existingFileUrls = targetMoum.getImageUrls();
        deleteExistingFiles(existingFileUrls);


        lifecycleRepository.deleteById(moumId);
        return new LifecycleDto.Response(targetMoum);
    }

    /**
     * 모음 마감하기
     */
    @Transactional
    public LifecycleDto.Response finishMoum(String username, int moumId){

        if(!isTeamLeader(username)){
            throw new CustomException(ErrorCode.NO_AUTHORITY);
        }

        if(isFinished(moumId)){
           throw new CustomException(ErrorCode.ALREADY_FINISHED_MOUM);
        }

        LifecycleEntity moum = findMoum(moumId);
        TeamEntity team = findTeam(moum.getTeam().getId());

        RecordEntity newRecord = RecordEntity.builder()
                .recordName(moum.getLifecycleName())
                .lifecycle(moum)
                .team(team)
                .startDate(moum.getStartDate())
                .endDate(LocalDate.now())
                .build();
        recordRepository.save(newRecord);

        team.assignRecord(newRecord);

        List<MemberEntity> members = teamMemberRepositoryCustom.findAllMembersByTeamId(team.getId());
        for (MemberEntity member : members) {
//            member.assignRecord(newRecord);
            MoumMemberRecordEntity moumMemberRecord = MoumMemberRecordEntity.builder()
                    .member(member)
                    .record(newRecord)
                    .lifecycle(moum)
                    .build();
            moumMemberRecordRepository.save(moumMemberRecord);
            member.updateMemberExpAndRank(1);
        }

        team.updateTeamExpAndRank(1);

        moum.getProcess().changeFinishStatus(true);
        moum.getProcess().updateAndGetProcessPercentage();

        return new LifecycleDto.Response(moum);
    }

    /**
     * 모음 되살리기
     */
    @Transactional
    public LifecycleDto.Response reopenMoum(String username, int moumId){

        if(!isTeamLeader(username)){
            throw new CustomException(ErrorCode.NO_AUTHORITY);
        }

        if(!isFinished(moumId)){
            throw new CustomException(ErrorCode.NOT_FINISHED_MOUM);
        }

        LifecycleEntity moum = findMoum(moumId);
        TeamEntity team = findTeam(moum.getTeam().getId());
        RecordEntity record = lifecycleRepositoryCustom.findLatestRecordByMoumId(moumId);

        // 모음에 속한 모든 멤버들의 이력 삭제
        List<MemberEntity> members = teamMemberRepositoryCustom.findAllMembersByTeamId(team.getId());
        for (MemberEntity member : members) {
            // 모음 이력 삭제
            moumMemberRecordRepositoryCustom.deleteMoumMemberRecordByLifecycleAndMember(moum.getId(), member.getId());

            // 멤버의 경험치 및 랭킹 업데이트 (삭제된 이력에 맞게 감소 처리)
            member.updateMemberExpAndRank(-1);
        }

        // 팀의 기록 삭제 및 경험치 및 랭킹 업데이트
        team.removeRecord(record);
        team.updateTeamExpAndRank(-1);

        // 모음 상태를 다시 되살리기
        moum.getProcess().changeFinishStatus(false);
        moum.getProcess().updateAndGetProcessPercentage();

        return new LifecycleDto.Response(moum);
    }



    /**
     * 모음 진척도 수정하기
     */
    public LifecycleDto.Response updateProcessStatus(String username, int moumId, ProcessDto processDto){
        if(!isTeamLeader(username)){
            throw new CustomException(ErrorCode.NO_AUTHORITY);
        }
        LifecycleEntity moum = findMoum(moumId);

        moum.getProcess().updateProcessStatus(processDto);
        moum.getProcess().updateAndGetProcessPercentage();
        lifecycleRepository.save(moum);

        return new LifecycleDto.Response(moum);

    }


    public MemberEntity findLoginUser(String username){
        return memberRepository.findByUsername(username);
    }

    public TeamEntity findTeam(int teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));
    }

    /**
     * 모음 연습실, 공연장 API
     */

    public LifecyclePracticeRoomDto.Response addPracticeRoom(LifecyclePracticeRoomDto.Request requestDto){
        LifecycleEntity lifecycleEntity = lifecycleRepository.findById(requestDto.getMoumId())
                .orElseThrow(() -> new CustomException(ErrorCode.MOUM_NOT_FOUND));
        LifecyclePracticeRoom practiceRoom = LifecyclePracticeRoom.builder()
                .moum(lifecycleEntity)
                .practiceRoom(requestDto.getPracticeRoom())
                .build();
        practiceRoom = lifecyclePracticeRoomRepository.save(practiceRoom);
        return new LifecyclePracticeRoomDto.Response(practiceRoom);
    }

    public LifecyclePerformanceHallDto.Response addPerformanceHall(LifecyclePerformanceHallDto.Request requestDto){
        LifecycleEntity lifecycleEntity = lifecycleRepository.findById(requestDto.getMoumId())
                .orElseThrow(() -> new CustomException(ErrorCode.MOUM_NOT_FOUND));
        LifecyclePerformanceHall performanceHall = LifecyclePerformanceHall.builder()
                .moum(lifecycleEntity)
                .performanceHall(requestDto.getPerformanceHall())
                .build();
        performanceHall = lifecyclePerformanceHallRepository.save(performanceHall);
        return new LifecyclePerformanceHallDto.Response(performanceHall);
    }

    public List<LifecyclePracticeRoomDto.Response> getPracticeRooms(Integer id){
        List<LifecyclePracticeRoom> practiceRooms = lifecyclePracticeRoomRepository.findAllByMoumId(id);
        return practiceRooms.stream()
                .map(LifecyclePracticeRoomDto.Response::new)
                .collect(Collectors.toList());
    }

    public List<LifecyclePerformanceHallDto.Response> getPerformanceHalls(Integer id){
        List<LifecyclePerformanceHall> performanceHalls = lifecyclePerformanceHallRepository.findAllByMoumId(id);
        return performanceHalls.stream()
                .map(LifecyclePerformanceHallDto.Response::new)
                .collect(Collectors.toList());
    }



    private List<String> uploadFiles(List<MultipartFile> files, String moumName) throws IOException {
        List<String> newFileUrls = new ArrayList<>();

        if (files != null && !files.isEmpty() && files.size() != 0) {
            for (MultipartFile file : files) {
                String originalFilename = file.getOriginalFilename();
                String key = "moums/" + moumName + "/" + originalFilename;
                String fileUrl = storageService.uploadFile(key, file);
                log.info("================= 파일 리스트 업로드");
                newFileUrls.add(fileUrl);
            }
        }
        return newFileUrls;
    }

    private void deleteExistingFiles(List<String> existingFileUrls) {
        if(existingFileUrls == null || existingFileUrls.isEmpty()){
            existingFileUrls = new ArrayList<>();
        }
        for (String existingFileUrl : existingFileUrls) {
            String existingFileName = existingFileUrl.replace("https://kr.object.ncloudstorage.com/" + bucket + "/", "");
            log.info("================= 기존 파일 리스트 삭제");
            storageService.deleteFile(existingFileName);
        }
    }

    private void updateLifecycleRecords(LifecycleDto.Request requestDto, LifecycleEntity lifecycle) {
        if (requestDto.getRecords() != null) {
            List<RecordEntity> updatedRecords = requestDto.getRecords().stream()
                    .map(RecordDto.Request::toEntity)
                    .collect(Collectors.toList());
            lifecycle.updateRecords(updatedRecords);
        }

        List<RecordEntity> records = lifecycle.getRecords();
        if (records != null && !records.isEmpty()) {
            for (RecordEntity record : records) {
                record.setLifecycle(lifecycle);
            }
            recordRepository.saveAll(records);
        }
    }


    public boolean hasTeam(String username){
        int memberId = memberRepository.findByUsername(username).getId();
        return teamMemberRepositoryCustom.hasTeam(memberId);
    }

    public LifecycleEntity findMoum(int moumId){
        return lifecycleRepository.findById(moumId)
                .orElseThrow(() -> new CustomException(ErrorCode.ILLEGAL_ARGUMENT));

    }

    public boolean isTeamLeader(String username){
        return lifecycleRepositoryCustom.isTeamLeaderByUsername(username);
    }

    public boolean isFinished(int moumId){
        return lifecycleRepositoryCustom.findFinishStatusByMoumId(moumId);
    }

}
