package com.zopsmart.meet.model;

import java.util.Date;

public class MeetSchedule implements Comparable<MeetSchedule> {
    private String windowHandleCode;
    private String meetingCode;
    private Date meetingTime;
    private Boolean oldAlreadyStatus;
    private Boolean rightTimeStatus;
    private Boolean participantStatus;
    private Boolean meetStatus; // meetStatus is like main switch/ (done)
    private Boolean recordingStatus;
    private Boolean joinedAlreadyStatus;
    private int retry = 0;

    public MeetSchedule() {
        this.rightTimeStatus = false;
        this.participantStatus = false;
        this.meetStatus = false;
        this.oldAlreadyStatus = false;
        this.joinedAlreadyStatus = false;
        this.recordingStatus = false;
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


    public Boolean getRecordingStatus() {
        return recordingStatus;
    }

    public void setRecordingStatus(Boolean recordingStatus) {
        this.recordingStatus = recordingStatus;
    }

    public Boolean getOldAlreadyStatus() {
        return oldAlreadyStatus;
    }

    public void setOldAlreadyStatus(Boolean oldAlreadyStatus) {
        this.oldAlreadyStatus = oldAlreadyStatus;
    }

    public Boolean getJoinedAlreadyStatus() {
        return joinedAlreadyStatus;
    }

    public void setJoinedAlreadyStatus(Boolean joinedAlreadyStatus) {
        this.joinedAlreadyStatus = joinedAlreadyStatus;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }

    @Override
    public int compareTo(MeetSchedule o) {
        return this.meetingTime.compareTo(o.meetingTime);
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
