package jsl.moum.business.domain;

import jsl.moum.business.dto.PracticeRoomDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface PracticeRoomRepositoryCustom {

    Page<PracticeRoom> findAllBySearchParams(Pageable pageable, PracticeRoomDto.Search searchParams);

}
