package jsl.moum.backendmodule.moum.moum.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class MoumTeamEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

}