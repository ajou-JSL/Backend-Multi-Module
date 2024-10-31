package study.moum.member_profile.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import study.moum.auth.domain.entity.MemberEntity;
import study.moum.auth.domain.repository.MemberRepository;
import study.moum.member_profile.dto.ProfileDto;
import study.moum.global.error.ErrorCode;
import study.moum.global.error.exception.CustomException;
import study.moum.objectstorage.StorageService;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final MemberRepository memberRepository;
    private final StorageService storageService;

    @Value("${ncp.object-storage.bucket}")
    private String bucket;

    /**
     프로필 조회
     */
    @Transactional
    public ProfileDto.Response getProfile(int targetMemberId) {

        MemberEntity targetMemberEntity = findMember(targetMemberId);

        return new ProfileDto.Response(targetMemberEntity);
    }

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
            String fileUrl = storageService.uploadFile(key, file); // 업로드 메서드 호출
            memberEntity.updateProfileImage(fileUrl);
        }

        memberEntity.updateMemberInfo(request.getProficiency(), request.getInstrument(), request.getProfileDescription(), request.getEmail(),
                request.getName(), request.getUsername(), request.getAddress());

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
