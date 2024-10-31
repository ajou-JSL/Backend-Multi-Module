package jsl.moum.backendmodule.community.record.service;

import jsl.moum.backendmodule.community.record.dto.RecordDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jsl.moum.backendmodule.auth.domain.entity.MemberEntity;
import jsl.moum.backendmodule.auth.domain.repository.MemberRepository;
import jsl.moum.backendmodule.community.record.domain.MemberRecordEntity;
import jsl.moum.backendmodule.community.record.domain.RecordEntity;
import jsl.moum.backendmodule.community.record.dto.RecordDto;
import jsl.moum.backendmodule.community.record.repository.MemberRecordRepository;
import jsl.moum.backendmodule.community.record.repository.RecordRepository;
import jsl.moum.backendmodule.global.error.ErrorCode;
import jsl.moum.backendmodule.global.error.exception.CustomException;

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
