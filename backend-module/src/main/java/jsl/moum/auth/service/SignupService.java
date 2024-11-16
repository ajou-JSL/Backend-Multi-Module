package jsl.moum.auth.service;

import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.domain.repository.MemberRepository;
import jsl.moum.email.service.EmailService;
import jsl.moum.record.domain.entity.RecordEntity;
import jsl.moum.record.domain.repository.RecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jsl.moum.auth.dto.MemberDto;
import jsl.moum.objectstorage.StorageService;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;
import jsl.moum.global.error.exception.DuplicateUsernameException;
import jsl.moum.config.redis.util.RedisUtil;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SignupService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RedisUtil redisUtil;
    private final StorageService storageService;
    private final RecordRepository recordRepository;

    public void signupMember(MemberDto.Request memberRequestDto, MultipartFile file) throws IOException {

        Boolean isExist = memberRepository.existsByUsername(memberRequestDto.getUsername());
        if(isExist){
            throw new DuplicateUsernameException();
        }

        // 인증 코드 검증
        String verifyCode = redisUtil.getData(memberRequestDto.getEmail()); // Redis에서 이메일로 인증 코드 가져오기
        if (verifyCode == null || !verifyCode.equals(memberRequestDto.getVerifyCode())) {
            System.out.println("==============="+verifyCode);
            throw new CustomException(ErrorCode.EMAIL_VERIFY_FAILED);
        }

        // 파일 업로드 후 URL 획득
        // S3에 저장할 파일의 키를 설정 (예: "profiles/{username}/{originalFileName}")
        String fileUrl = uploadMemberImage(memberRequestDto.getUsername(), file);

        // dto -> entity
        MemberDto.Request joinRequestDto = MemberDto.Request.builder()
                .name(memberRequestDto.getName())
                .username(memberRequestDto.getUsername())
                .email(memberRequestDto.getEmail())
                .password(bCryptPasswordEncoder.encode(memberRequestDto.getPassword()))
                .address(memberRequestDto.getAddress())
                .instrument(memberRequestDto.getInstrument())
                .proficiency(memberRequestDto.getProficiency())
                .profileDescription(memberRequestDto.getProfileDescription())
                .records(memberRequestDto.getRecords())
                .profileImageUrl(fileUrl)
                .build();

        MemberEntity newMember = joinRequestDto.toEntity();

        List<RecordEntity> records = newMember.getRecords();
        if (records != null && !records.isEmpty()) {
            for (RecordEntity record : records) {
                record.setMember(newMember);
            }
            recordRepository.saveAll(records);
        }

        memberRepository.save(newMember);



    }

    private String uploadMemberImage(String memberName, MultipartFile file) throws IOException {
        if(file == null || file.isEmpty()){
            return null;
        }

        String originalFilename = file.getOriginalFilename();
        String key = "profiles/" + memberName + "/" + originalFilename; // 키 생성
        return storageService.uploadFile(key, file); // 업로드 메서드 호출
    }
}
