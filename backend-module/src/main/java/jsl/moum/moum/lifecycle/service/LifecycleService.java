package jsl.moum.moum.lifecycle.service;

import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.domain.repository.MemberRepository;
import jsl.moum.auth.dto.MemberDto;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;
import jsl.moum.moum.lifecycle.domain.*;
import jsl.moum.moum.lifecycle.domain.Process;
import jsl.moum.moum.lifecycle.dto.LifecycleDto;
import jsl.moum.moum.team.domain.*;
import jsl.moum.moum.team.dto.TeamDto;
import jsl.moum.objectstorage.StorageService;
import jsl.moum.record.domain.dto.RecordDto;
import jsl.moum.record.domain.entity.RecordEntity;
import jsl.moum.record.domain.repository.RecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LifecycleService {

    private final LifecycleRepository lifecycleRepository;
    private final LifecycleRepositoryCustom lifecycleRepositoryCustom;
    private final MemberRepository memberRepository;
    private final TeamMemberRepositoryCustom teamMemberRepositoryCustom;
    private final StorageService storageService;
    private final TeamRepository teamRepository;
    private final RecordRepository recordRepository;

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
    public LifecycleDto.Response addMoum(String username, LifecycleDto.Request requestDto, MultipartFile file) throws IOException {

        MemberEntity loginUser = findLoginUser(username);
        TeamEntity team = findTeam(requestDto.getTeamId());

        if(!hasTeam(username)){
            throw new CustomException(ErrorCode.NEED_TEAM);
        }

        if(!isTeamLeader(username)){
            throw new CustomException(ErrorCode.NO_AUTHORITY);
        }

        // "moums/{moumName}/{originalFileName}"
        String originalFilename = file.getOriginalFilename();
        String key = "moums/" + requestDto.getMoumName() + "/" + originalFilename;
        String fileUrl = storageService.uploadFile(key, file);

        LifecycleEntity newMoum = LifecycleDto.Request.builder()
                .moumName(requestDto.getMoumName())
                .price(requestDto.getPrice())
                .moumDescription(requestDto.getMoumDescription())
                .imageUrl(fileUrl)
                .performLocation(requestDto.getPerformLocation())
                .startDate(requestDto.getStartDate())
                .leaderId(loginUser.getId())
                .records(requestDto.getRecords())
                .build().toEntity();

        newMoum.assignTeam(team);

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
    public LifecycleDto.Response updateMoum(String username, LifecycleDto.Request requestDto, MultipartFile file, int moumId) throws IOException {

        MemberEntity loginUser = memberRepository.findByUsername(username);
        TeamEntity team = findTeam(requestDto.getTeamId());
        LifecycleEntity lifecycle = findMoum(moumId);

        if (!isTeamLeader(loginUser.getUsername())) {
            throw new CustomException(ErrorCode.NO_AUTHORITY);
        }

        String existingFileUrl = team.getFileUrl();

        if (file != null && !file.isEmpty()) {
            if (existingFileUrl != null && !existingFileUrl.isEmpty()) {
                String existingFileName = existingFileUrl.replace("https://kr.object.ncloudstorage.com/" + bucket + "/", "");
                storageService.deleteFile(existingFileName);
            }

            // 새로운 파일 업로드
            String newFileName = "moums/" + lifecycle.getLifecycleName() + "/" + file.getOriginalFilename();
            String newFileUrl = storageService.uploadFile(newFileName, file);
            lifecycle.updateProfileImage(newFileUrl);
        }


        if (requestDto.getRecords() != null) {
            List<RecordEntity> updatedRecords = requestDto.getRecords().stream()
                    .map(RecordDto.Request::toEntity)
                    .collect(Collectors.toList());
            lifecycle.updateRecords(updatedRecords);
        }

        List<RecordEntity> records = team.getRecords();
        if (records != null && !records.isEmpty()) {
            for (RecordEntity record : records) {
                record.setTeam(team);
            }
            recordRepository.saveAll(records);
        }

        lifecycle.updateLifecycleInfo(requestDto);

        return new LifecycleDto.Response(lifecycle);

    }

    /**
     * 모음 삭제
     */
    @Transactional
    public LifecycleDto.Response deleteMoum(String username, int moumId){

        if(!isTeamLeader(username)){
            throw new CustomException(ErrorCode.NO_AUTHORITY);
        }

        LifecycleEntity targetMoum = findMoum(moumId);
        findTeam(targetMoum.getTeam().getId());

        String fileUrl = targetMoum.getImageUrl();
        if (fileUrl != null && !fileUrl.isEmpty()) {
            String fileName = fileUrl.replace("https://kr.object.ncloudstorage.com/" + bucket + "/", "");
            fileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8);
            storageService.deleteFile(fileName);
        }

        lifecycleRepository.deleteById(moumId);
        return new LifecycleDto.Response(targetMoum);
    }


    /**
     * 모음 마감하기, 되살리기
     * 상태변경 true <-> false
     */
    @Transactional
    public LifecycleDto.Response finishMoum(String username, int moumId){

        if(!isTeamLeader(username)){
            throw new CustomException(ErrorCode.NO_AUTHORITY);
        }
        LifecycleEntity moum = findMoum(moumId);

        moum.getProcess().changeFinishStatus();
        int percentage = moum.getProcess().updateAndGetProcessPercentage();
        System.out.println("============" + percentage);

        return new LifecycleDto.Response(moum);
    }

    /**
     * 모음 되살리기
     */
    @Transactional
    public LifecycleDto.Response reopenMoum(String username, int moumId){
        return null;
    }

    public MemberEntity findLoginUser(String username){
        return memberRepository.findByUsername(username);
    }

    public TeamEntity findTeam(int teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));
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

}