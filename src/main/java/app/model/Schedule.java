package app.model;

import java.io.Serializable;

public class Schedule implements Serializable{

    private String scheduleType;
    private String scheduleTime;

    public String getScheduleType(){
        return scheduleType;
    }
    
    public String getScheduleTime(){
        return scheduleTime;
    }

    public void setScheduleType(String scheduleType){
        this.scheduleType=scheduleType;
    }

    public void setScheduleTime(String scheduleTime){
        this.scheduleTime=scheduleTime;
    }
}
