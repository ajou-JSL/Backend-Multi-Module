package jsl.moum.moum.lifecycle.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Process {

    @Column(name = "recruit_status")
    private Boolean recruitStatus;

    @Column(name = "chatroom_status")
    private Boolean chatroomStatus;

    @Column(name = "practiceroom_status")
    private Boolean practiceroomStatus;

    @Column(name = "perform_location_status")
    private Boolean performLocationStatus;

    @Column(name = "promote_status")
    private Boolean promoteStatus;

    @Column(name = "payment_status")
    private Boolean paymentStatus;

    @Column(name = "actice_status")
    private Boolean activeStatus;

    @Column(name = "process_percentage")
    private int processPercentage;

    public int updateProcessPercentage() {
        int completedSteps = 0;
        int totalSteps = 7;

        if (Boolean.TRUE.equals(recruitStatus)) completedSteps++;
        if (Boolean.TRUE.equals(chatroomStatus)) completedSteps++;
        if (Boolean.TRUE.equals(practiceroomStatus)) completedSteps++;
        if (Boolean.TRUE.equals(performLocationStatus)) completedSteps++;
        if (Boolean.TRUE.equals(promoteStatus)) completedSteps++;
        if (Boolean.TRUE.equals(paymentStatus)) completedSteps++;
        if (Boolean.TRUE.equals(activeStatus)) completedSteps++;

        this.processPercentage = (completedSteps * 100) / totalSteps;
        return this.processPercentage;
    }

    public boolean changeActiceStatus(){
        return !this.activeStatus;
    }

    public boolean getActiceStatus(){
        return this.activeStatus;
    }
}
