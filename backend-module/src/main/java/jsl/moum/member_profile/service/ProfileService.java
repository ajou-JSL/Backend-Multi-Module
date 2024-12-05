package jsl.moum.member_profile.service;

import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.domain.repository.MemberRepositoryCustom;
import jsl.moum.auth.dto.MemberSortDto;
import jsl.moum.record.domain.dto.RecordDto;
import jsl.moum.record.domain.entity.RecordEntity;
import jsl.moum.record.domain.repository.RecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import jsl.moum.auth.domain.repository.MemberRepository;
import jsl.moum.member_profile.dto.ProfileDto;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;
import jsl.moum.objectstorage.StorageService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final MemberRepository memberRepository;
    private final StorageService storageService;
    private final RecordRepository recordRepository;
    private final MemberRepositoryCustom memberRepositoryCustom;

    @Value("${ncp.object-storage.bucket}")
    private String bucket;

    /**
     프로필 조회
     */
    @Transactional
    public ProfileDto.Response getProfile(int targetMemberId) {

        MemberEntity targetMemberEntity = findMember(targetMemberId);
        if(!targetMemberEntity.getActiveStatus()){
            throw new CustomException(ErrorCode.SIGN_OUT_MEMBER);
        }
        if(targetMemberEntity.getBanStatus()){
            throw new CustomException(ErrorCode.BANNED_MEMBER);
        }

        return new ProfileDto.Response(targetMemberEntity);
    }

    /**
     프로필 리스트 조회(필터링 - 랭킹 순)
     */
    @Transactional
    public Page<MemberSortDto.ExpResponse> getProfilesSortByExp(int page, int size) {
        Page<MemberEntity> memberEntities = memberRepositoryCustom.getMembersSortByExp(page, size);

        return memberEntities.map(member ->
                new MemberSortDto.ExpResponse(
                        member.getId(),
                        member.getName(),
                        member.getUsername(),
                        member.getProfileImageUrl(),
                        member.getExp(),
                        member.getTier().toString()
                )
        );
    }
    /**
     프로필 리스트 조회(필터링 - 이력 개수 순)
     */
    @Transactional
    public List<MemberSortDto.RecordsCountResponse> getProfilesSortByRecordsCount(int page, int size) {

        return memberRepositoryCustom.getMembersSortByRecordsCount(page, size);
    }
//
//    /**
//     프로필 리스트 조회(필터링 - 검색 keyword : 악기 + 랭킹 순)
//     */
//    @Transactional
//    public ProfileDto.Response getProfile(int targetMemberId) {
//
////        MemberEntity targetMemberEntity = findMember(targetMemberId);
////
////        return new ProfileDto.Response(targetMemberEntity);
//    }

    /**
     프로필 수정
     */
    @Transactional
    public ProfileDto.Response updateProfile(String loginUserName, int targetMemberId,
                                             ProfileDto.UpdateRequest request, MultipartFile file) throws IOException {

        MemberEntity memberEntity = findMember(loginUserName);
        if(!isOwner(memberEntity, targetMemberId)){
            throw new CustomException(ErrorCode.NO_AUTHORITY);
        }

        // 기존 파일 URL
        String existingFileUrl = memberEntity.getProfileImageUrl();

        // 새로운 파일이 있을 경우 처리
        if (file != null && !file.isEmpty()) {
            // S3에서 기존 파일 삭제
            if (existingFileUrl != null && !existingFileUrl.isEmpty()) {
                String existingFileName = existingFileUrl.replace("https://kr.object.ncloudstorage.com/" + bucket + "/", "");
                storageService.deleteFile(existingFileName); // S3에서 기존 파일 삭제
            }

            String originalFilename = file.getOriginalFilename();
            String key = "profiles/" + request.getUsername() + "/" + originalFilename; // 키 생성
            String fileUrl = storageService.uploadImage(key, file); // 업로드 메서드 호출
            memberEntity.updateProfileImage(fileUrl);
        }

        if (request.getRecords() != null) {
            List<RecordEntity> updatedRecords = request.getRecords().stream()
                    .map(RecordDto.Request::toEntity)
                    .collect(Collectors.toList());
            memberEntity.updateRecords(updatedRecords);
        }

        memberEntity.updateMemberInfo(request);

        List<RecordEntity> records = memberEntity.getRecords();
        if (records != null && !records.isEmpty()) {
            for (RecordEntity record : records) {
                record.setMember(memberEntity);
            }
            recordRepository.saveAll(records);
        }

        memberRepository.save(memberEntity);
        return new ProfileDto.Response(memberEntity);

    }

    public MemberEntity findMember(String username) {
        MemberEntity member = memberRepository.findByUsername(username);
        if(member == null){
            throw new CustomException(ErrorCode.MEMBER_NOT_EXIST);
        }
        return member;
    }

    public MemberEntity findMember(int memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(()->new CustomException(ErrorCode.MEMBER_NOT_EXIST));
    }

    public boolean isOwner(MemberEntity member,int memberId){
        if(member.getId() == memberId){
            return true;
        }
        return false;
    }
}
