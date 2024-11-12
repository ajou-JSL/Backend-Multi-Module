package jsl.moum.moum.lifecycle.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.PrePersist;
import jsl.moum.moum.lifecycle.dto.ProcessDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Builder
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

    public void updateProcessStatus(ProcessDto processDto){
        this.recruitStatus = processDto.getRecruitStatus() != null ? processDto.getRecruitStatus() : false;
        this.chatroomStatus = processDto.getChatroomStatus() != null ? processDto.getChatroomStatus() : false;
        this.practiceroomStatus = processDto.getPracticeroomStatus() != null ? processDto.getPracticeroomStatus() : false;
        this.performLocationStatus = processDto.getPerformLocationStatus() != null ? processDto.getPerformLocationStatus() : false;
        this.promoteStatus = processDto.getPromoteStatus() != null ? processDto.getPromoteStatus() : false;
        this.paymentStatus = processDto.getPaymentStatus() != null ? processDto.getPaymentStatus() : false;
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
