package jsl.moum.moum.settlement.service;

import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;
import jsl.moum.moum.lifecycle.domain.entity.LifecycleEntity;
import jsl.moum.moum.lifecycle.domain.repository.LifecycleRepository;
import jsl.moum.moum.lifecycle.domain.entity.LifecycleRepositoryCustom;
import jsl.moum.moum.settlement.domain.entity.SettlementEntity;
import jsl.moum.moum.settlement.domain.repository.SettlementRepository;
import jsl.moum.moum.settlement.dto.SettlementDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final LifecycleRepositoryCustom lifecycleRepositoryCustom;
    private final LifecycleRepository lifecycleRepository;

    /**
        정산 생성
     */
    public SettlementDto.Response createSettlement(String username, int moumId, SettlementDto.Request settlementRequestDto){

        LifecycleEntity moum = findMoum(moumId);

        if(!lifecycleRepositoryCustom.isTeamLeaderByUsername(username)){
            throw new CustomException(ErrorCode.NO_AUTHORITY);
        }

        SettlementEntity newSettlement = SettlementDto.Request.builder()
                .settlementName(settlementRequestDto.getSettlementName())
                .fee(settlementRequestDto.getFee())
                .moumId(settlementRequestDto.getMoumId())
                .build().toEntity();

        settlementRepository.save(newSettlement);

        moum.setSettlementId(newSettlement.getId());
        return new SettlementDto.Response(newSettlement);
    }

    /**
        정산 목록 조회
     */
    public List<SettlementDto.Response> getSettlementList(int moumId){

        findMoum(moumId);
        return settlementRepository.findAll()
                .stream()
                .map(SettlementDto.Response::new)
                .toList();
    }

    /**
        정산 삭제
     */
    public SettlementDto.Response deleteSettlement(String username, int moumId, int settlementId){

        LifecycleEntity moum = findMoum(moumId);

        if(!lifecycleRepositoryCustom.isTeamLeaderByUsername(username)){
            throw new CustomException(ErrorCode.NO_AUTHORITY);
        }

        SettlementEntity targetSettlement = settlementRepository.findById(settlementId)
                        .orElseThrow(() -> new CustomException(ErrorCode.ILLEGAL_ARGUMENT));

        settlementRepository.deleteById(settlementId);
        moum.setSettlementId(null);
        return new SettlementDto.Response(targetSettlement);
    }

    public LifecycleEntity findMoum(int moumId){
        return lifecycleRepository.findById(moumId)
                .orElseThrow(()->new CustomException(ErrorCode.MOUM_NOT_FOUND));
    }
}
