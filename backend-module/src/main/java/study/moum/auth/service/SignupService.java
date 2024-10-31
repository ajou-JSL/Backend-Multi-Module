package study.moum.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import study.moum.auth.domain.entity.MemberEntity;
import study.moum.auth.domain.repository.MemberRepository;
import study.moum.auth.dto.MemberDto;
import study.moum.objectstorage.StorageService;
import study.moum.global.error.ErrorCode;
import study.moum.global.error.exception.CustomException;
import study.moum.global.error.exception.DuplicateUsernameException;
import study.moum.config.redis.util.RedisUtil;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SignupService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RedisUtil redisUtil;
    private final StorageService storageService;

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
        String originalFilename = file.getOriginalFilename();
        String key = "profiles/" + memberRequestDto.getUsername() + "/" + originalFilename; // 키 생성
        String fileUrl = storageService.uploadFile(key, file); // 업로드 메서드 호출

        // dto -> entity
        MemberDto.Request joinRequestDto = MemberDto.Request.builder()
                .username(memberRequestDto.getUsername())
                .email(memberRequestDto.getEmail())
                .password(bCryptPasswordEncoder.encode(memberRequestDto.getPassword()))
                .address(memberRequestDto.getAddress())
                .profileImageUrl(fileUrl)
                .build();


        MemberEntity newMember = joinRequestDto.toEntity();
        memberRepository.save(newMember);

    }

}
