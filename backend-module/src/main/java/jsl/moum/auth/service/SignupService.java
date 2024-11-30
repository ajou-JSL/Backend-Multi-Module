package jsl.moum.auth.service;

import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.domain.repository.MemberRepository;
import jsl.moum.common.CommonService;
import jsl.moum.record.domain.dto.RecordDto;
import jsl.moum.record.domain.entity.RecordEntity;
import jsl.moum.record.domain.repository.RecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import jsl.moum.auth.dto.MemberDto;
import jsl.moum.objectstorage.StorageService;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;
import jsl.moum.global.error.exception.DuplicateUsernameException;
import jsl.moum.config.redis.util.RedisUtil;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignupService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RedisUtil redisUtil;
    private final StorageService storageService;
    private final RecordRepository recordRepository;
    private final CommonService commonService;

    public String signupMember(MemberDto.Request memberRequestDto, MultipartFile file) throws IOException {

        MemberEntity member = memberRepository.findByUsername(memberRequestDto.getUsername());

        if (member != null) {
            if (member.getActiveStatus()) {
                throw new DuplicateUsernameException();
            } else if(member.getBanStatus()){
                return "밴 당한 계정입니다. 가입 이메일: " + member.getEmail() +
                        "\n가입 이름: " + member.getUsername();
            } else {
                return "회원 탈퇴한 계정입니다. 가입 이메일: " + member.getEmail() +
                        "\n가입 이름: " + member.getUsername();
            }
        }

        verifyEmailCode(memberRequestDto);
        String fileUrl = uploadMemberImage(memberRequestDto.getUsername(), file);

        MemberEntity newMember = buildMemberEntity(memberRequestDto, fileUrl);
        memberRepository.save(newMember);

        saveMemberRecords(memberRequestDto, newMember);

        return newMember.getUsername();
    }

    private void verifyEmailCode(MemberDto.Request memberRequestDto) {
        String verifyCode = redisUtil.getData(memberRequestDto.getEmail());
        if (verifyCode == null || !verifyCode.equals(memberRequestDto.getVerifyCode())) {
            throw new CustomException(ErrorCode.EMAIL_VERIFY_FAILED);
        }
    }

    private MemberEntity buildMemberEntity(MemberDto.Request memberRequestDto, String fileUrl) {
        return MemberDto.Request.builder()
                .name(memberRequestDto.getName())
                .username(memberRequestDto.getUsername())
                .email(memberRequestDto.getEmail())
                .password(bCryptPasswordEncoder.encode(memberRequestDto.getPassword()))
                .address(memberRequestDto.getAddress())
                .instrument(memberRequestDto.getInstrument())
                .proficiency(memberRequestDto.getProficiency())
                .profileDescription(memberRequestDto.getProfileDescription())
                .role("ROLE_USER")
                .profileImageUrl(fileUrl)
                .genres(memberRequestDto.getGenres())
                .videoUrl(memberRequestDto.getVideoUrl())
                .build()
                .toEntity();
    }

    private void saveMemberRecords(MemberDto.Request memberRequestDto, MemberEntity newMember) {
        if (memberRequestDto.getRecords() != null && !memberRequestDto.getRecords().isEmpty()) {
            List<RecordEntity> recordList = memberRequestDto.getRecords().stream()
                    .map(RecordDto.Request::toEntity)
                    .collect(Collectors.toList());

            newMember.assignRecord(recordList);
            recordRepository.saveAll(recordList);
        }
    }

    @Transactional
    public MemberDto.Response rejoinMember(String username){
        MemberEntity member = commonService.findMemberByUsername(username);
        member.changeActiveStatusTrue();
        return new MemberDto.Response(member);
    }


    private String uploadMemberImage(String memberName, MultipartFile file) throws IOException {
        if(file == null || file.isEmpty()){
            return null;
        }

        String originalFilename = file.getOriginalFilename();
        String key = "profiles/" + memberName + "/" + originalFilename;
        return storageService.uploadImage(key, file);
    }
}
