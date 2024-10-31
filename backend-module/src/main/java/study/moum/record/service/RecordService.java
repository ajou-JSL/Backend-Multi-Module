package study.moum.record.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.moum.auth.domain.entity.MemberEntity;
import study.moum.auth.domain.repository.MemberRepository;
import study.moum.record.domain.MemberRecordEntity;
import study.moum.global.error.ErrorCode;
import study.moum.global.error.exception.CustomException;
import study.moum.record.domain.RecordEntity;
import study.moum.record.dto.RecordDto;
import study.moum.record.repository.MemberRecordRepository;
import study.moum.record.repository.RecordRepository;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final RecordRepository recordRepository;
    private final MemberRecordRepository memberRecordRepository;
    private final MemberRepository memberRepository;

    /**
     이력 추가(생성)
     */
    @Transactional
    public RecordDto.Response addRecord(int memberId, RecordDto.Request requestDto) {

        MemberEntity member = findMember(memberId);

        RecordEntity recordEntity = RecordDto.Request.builder()
                .startDate(requestDto.getStartDate())
                .endDate(requestDto.getEndDate())
                .recordName(requestDto.getRecordName())
                .build().toEntity();

        recordRepository.save(recordEntity);

        MemberRecordEntity memberRecordEntity = MemberRecordEntity.builder()
                .record(recordEntity)
                .member(member)
                .build();

        memberRecordRepository.save(memberRecordEntity);

        return new RecordDto.Response(recordEntity);
    }

    /**
     todo : 이력 삭제
     */

    public MemberEntity findMember(int memberId){
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_EXIST));
    }





}
