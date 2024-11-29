package jsl.moum.moum.team.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<TeamEntity, Integer> {

    TeamEntity findByTeamName(String teamName);
}
