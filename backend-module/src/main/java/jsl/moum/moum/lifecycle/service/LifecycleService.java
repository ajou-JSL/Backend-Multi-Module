package jsl.moum.moum.lifecycle.service;

import jsl.moum.moum.lifecycle.domain.LifecycleRepository;
import jsl.moum.moum.lifecycle.domain.LifecycleTeamRepository;
import jsl.moum.moum.lifecycle.dto.LifecycleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LifecycleService {

    private final LifecycleRepository lifecycleRepository;
    private final LifecycleTeamRepository lifecycleTeamRepository;


    /**
     * 모음 단건 조회
     */
    public LifecycleDto.Response getMoumById(String username, int moumId){
        return null;
    }

    /**
     * 나의 모음 리스트 조회
     */
    public List<LifecycleDto.Response> getMyMoumList(String username){
        return null;
    }


    /**
     * 모음 생성
     */
    public LifecycleDto.Response addMoum(String username, LifecycleDto.Request requestDto, MultipartFile file){
        return null;
    }

    /**
     * 모음 정보 수정
     */
    public LifecycleDto.Response updateMoum(String username, LifecycleDto.Request requestDto, MultipartFile file, int moumId){
        return null;
    }

    /**
     * 모음 삭제
     */
    public LifecycleDto.Response deleteMoum(String username, int moumId){
        return null;
    }


    /**
     * 모음 마감하기
     */
    public LifecycleDto.Response finishMoum(String username, int moumId){
        return null;
    }

    /**
     * 모음 되살리기
     */
    public LifecycleDto.Response reopenMoum(String username, int moumId){
        return null;
    }
}
