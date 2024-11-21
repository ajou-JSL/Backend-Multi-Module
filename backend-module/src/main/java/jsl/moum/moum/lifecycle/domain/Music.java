package jsl.moum.moum.lifecycle.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
public class Music {
    private String musicName;
    private String artistName;
}
