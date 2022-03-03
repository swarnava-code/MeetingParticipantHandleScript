package com.zopsmart.meet.model;

import java.util.Date;

public class MeetSchedule {
    private String windowHandleCode;
    private String meetingCode;
    private Date meetingTime;
    private Boolean rightTimeStatus;
    private Boolean participantStatus;
    private Boolean meetStatus;

    public MeetSchedule() {
        this.rightTimeStatus = false;
        this.participantStatus = false;
        this.meetStatus = false;
    }

    public void setWindowHandleCode(String windowHandleCode) {
        this.windowHandleCode = windowHandleCode;
    }

    public void setMeetingCode(String meetingCode) {
        this.meetingCode = meetingCode;
    }

    public void setMeetingTime(Date meetingTime) {
        this.meetingTime = meetingTime;
    }

    public String getWindowHandleCode() {
        return windowHandleCode;
    }

    public String getMeetingCode() {
        return meetingCode;
    }

    public Date getMeetingTime() {
        return meetingTime;
    }

    public Boolean getRightTimeStatus() {
        return rightTimeStatus;
    }

    public void setRightTimeStatus(Boolean rightTimeStatus) {
        this.rightTimeStatus = rightTimeStatus;
    }

    public Boolean getParticipantStatus() {
        return participantStatus;
    }

    public void setParticipantStatus(Boolean participantStatus) {
        this.participantStatus = participantStatus;
    }

    public Boolean getMeetStatus() {
        return meetStatus;
    }

    public void setMeetStatus(Boolean meetStatus) {
        this.meetStatus = meetStatus;
    }

    @Override
    public String toString() {
        return "MeetSchedule{" +
                "windowHandleCode='" + windowHandleCode + '\'' +
                ", meetingCode='" + meetingCode + '\'' +
                ", meetingTime=" + meetingTime +
                ", rightTimeStatus=" + rightTimeStatus +
                ", participantStatus=" + participantStatus +
                ", meetStatus=" + meetStatus +
                '}';
    }
}
