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
    private Boolean recruitStatus = false;

    @Column(name = "chatroom_status")
    private Boolean chatroomStatus = false;

    @Column(name = "practiceroom_status")
    private Boolean practiceroomStatus = false;

    @Column(name = "perform_location_status")
    private Boolean performLocationStatus = false;

    @Column(name = "promote_status")
    private Boolean promoteStatus = false;

    @Column(name = "payment_status")
    private Boolean paymentStatus = false;

    @Column(name = "finish_status")
    private Boolean finishStatus = false;

    @Column(name = "process_percentage")
    private int processPercentage = 0;

    public void changeRecruitStatus() {
        this.recruitStatus = !this.recruitStatus;
    }

    public void changeChatroomStatus() {
        this.chatroomStatus = !this.chatroomStatus;
    }

    public void changePracticeroomStatus() {
        this.practiceroomStatus = !this.practiceroomStatus;
    }

    public void changePerformLocationStatus() {
        this.performLocationStatus = !this.performLocationStatus;
    }

    public void changePromoteStatus() {
        this.promoteStatus = !this.promoteStatus;
    }

    public void changePaymentStatus() {
        this.paymentStatus = !this.paymentStatus;
    }

    public void changeFinishStatus(Boolean status) {
        this.finishStatus = status;
    }


    public int updateAndGetProcessPercentage() {
        int completedSteps = 0;
        int totalSteps = 7;

        if (Boolean.TRUE.equals(recruitStatus)) completedSteps++;
        if (Boolean.TRUE.equals(chatroomStatus)) completedSteps++;
        if (Boolean.TRUE.equals(practiceroomStatus)) completedSteps++;
        if (Boolean.TRUE.equals(performLocationStatus)) completedSteps++;
        if (Boolean.TRUE.equals(promoteStatus)) completedSteps++;
        if (Boolean.TRUE.equals(paymentStatus)) completedSteps++;
        if (Boolean.TRUE.equals(finishStatus)) completedSteps++;

        this.processPercentage = (completedSteps * 100) / totalSteps;
        return this.processPercentage;
    }
}
