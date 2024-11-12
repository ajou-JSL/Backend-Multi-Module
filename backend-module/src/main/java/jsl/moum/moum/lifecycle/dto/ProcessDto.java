package jsl.moum.moum.lifecycle.dto;

import jsl.moum.moum.lifecycle.domain.Process;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ProcessDto {

    private Boolean recruitStatus = false;
    private Boolean chatroomStatus = false;
    private Boolean practiceroomStatus = false;
    private Boolean performLocationStatus = false;
    private Boolean promoteStatus = false;
    private Boolean paymentStatus = false;

    public Process toProcess(){
        return Process.builder()
                .recruitStatus(recruitStatus)
                .chatroomStatus(chatroomStatus)
                .practiceroomStatus(practiceroomStatus)
                .performLocationStatus(performLocationStatus)
                .promoteStatus(promoteStatus)
                .paymentStatus(paymentStatus)
                .build();
    }
}
