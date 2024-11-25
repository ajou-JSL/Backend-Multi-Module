package jsl.moum.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PracticeRoomRepository extends JpaRepository<PracticeRoom, Integer>, PracticeRoomRepositoryCustom {

}
