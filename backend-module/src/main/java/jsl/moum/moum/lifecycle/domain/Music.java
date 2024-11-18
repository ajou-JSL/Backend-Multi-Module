package jsl.moum.moum.lifecycle.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class Music {
    private String musicName;
    private String artistName;
}
