package jsl.moum.email.service;

import jakarta.mail.Message;
import jakarta.mail.internet.MimeMessage;
import jsl.moum.auth.domain.repository.MemberRepository;
import jsl.moum.auth.dto.MemberDto;
import jsl.moum.config.redis.util.RedisUtil;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.AlreadyVerifiedEmailException;
import jsl.moum.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import jsl.moum.email.dto.EmailDto;
import jsl.moum.email.dto.VerifyDto;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final RedisUtil redisUtil;
    private final MemberRepository memberRepository;

    @Value("${spring.mail.username}")
    private String sender;

    private MimeMessage createMessage(EmailDto.Request emailDto, String code) throws Exception{
        MimeMessage message = javaMailSender.createMimeMessage();

        message.addRecipients(Message.RecipientType.TO, emailDto.getEmail());
        message.setSubject(" 모음(MOUM) 에서 발송된 인증 번호입니다.");
        message.setText("이메일 인증코드: "+code);
        message.setFrom(sender);

        return message;
    }

    public void sendMail(EmailDto.Request emailDto, String code) throws Exception{

        Boolean isExist = memberRepository.existsByEmail(emailDto.getEmail());
        if(isExist){
            throw new AlreadyVerifiedEmailException();
        }

        try{
            MimeMessage mimeMessage = createMessage(emailDto, code);
            javaMailSender.send(mimeMessage);
        } catch (MailException mailException) {
            throw new RuntimeException("메일 전송에 실패했습니다.");
        }
    }

    public String sendCertificationMail(EmailDto.Request emailDto) throws Exception {
        String code = UUID.randomUUID().toString().substring(0, 6); //랜덤 인증번호 : uuid
        sendMail(emailDto,code);

        redisUtil.setDataExpire(emailDto.getEmail(),code,60*60*60*1L); // {key,value} 1시간동안 저장.
        return code;
    }

    public VerifyDto.Response verifyCode(VerifyDto.Request verifyDto) {
        // Redis에서 해당 이메일에 매핑된 인증 코드 가져오기
        String storedCode = redisUtil.getData(verifyDto.getEmail());

        if (storedCode == null) {
            throw new CustomException(ErrorCode.ILLEGAL_ARGUMENT);
        }

        // 사용자가 입력한 코드와 Redis에 저장된 코드 비교
        boolean isValid = storedCode.equals(verifyDto.getVerifyCode());

        VerifyDto.Response result = new VerifyDto.Response();
        result.setVerifyCode(storedCode);

        if (isValid) {
            // 인증이 성공하면 해당 인증 코드를 삭제 (재사용 방지)
            //redisUtil.deleteData(verifyDto.getEmail());
            result.setResult(true);
        }else{
            result.setResult(false);
        }

        return result;
    }
}