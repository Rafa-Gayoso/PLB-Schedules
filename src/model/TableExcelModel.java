package model;

import javafx.beans.property.SimpleStringProperty;

public class TableExcelModel {
    private SimpleStringProperty day;
    private SimpleStringProperty entryHour;
    private SimpleStringProperty exitHour;
    private SimpleStringProperty journalTime;
    private double doubleEntryHour;
    private double doubleExitHour;
    private double doubleJournalTime;

    public TableExcelModel(String day, String entryHour, String exitHour, String journalTime) {
        this.day = new SimpleStringProperty(day);
        this.entryHour = new SimpleStringProperty(entryHour);
        this.exitHour = new SimpleStringProperty(exitHour);
        this.journalTime = new SimpleStringProperty(journalTime);
        this.setEntryHour(entryHour);;
        this.setExitHour(exitHour);;
        this.setJournalTime(journalTime);;
    }

    public String getDay() {
        return day.get();
    }

    public SimpleStringProperty dayProperty() {
        return day;
    }

    public void setDay(String day) {
        this.day.set(day);
    }

    public String getEntryHour() {
        return entryHour.get();
    }

    public SimpleStringProperty entryHourProperty() {
        return entryHour;
    }

    public void setEntryHour(String entryHour) {
        this.entryHour.set(entryHour);
        try{
            this.doubleEntryHour = Double.parseDouble(formatTime(this.entryHour.get()));
        }catch(Exception e){
            this.doubleEntryHour = 0.0;
        }
    }

    public String getExitHour() {
        return exitHour.get();
    }

    public SimpleStringProperty exitHourProperty() {
        return exitHour;
    }

    public void setExitHour(String exitHour) {
        this.exitHour.set(exitHour);
        try{
            this.doubleExitHour = Double.parseDouble(formatTime(this.exitHour.get()));
        }catch(Exception e){
            this.doubleExitHour = 0;
        }
    }

    public String getJournalTime() {
        return journalTime.get();
    }

    public SimpleStringProperty journalTimeProperty() {
        return journalTime;
    }

    public void setJournalTime(String journalTime) {
        this.journalTime.set(journalTime);
        try{
            this.doubleJournalTime = Double.parseDouble(formatTime(this.journalTime.get()));
        }catch(Exception e){
            this.doubleJournalTime = 0;
        }
    }

    public double getDoubleEntryHour() {
        return doubleEntryHour;
    }

    public double getDoubleExitHour() {
        return doubleExitHour;
    }

    public double getIntegerJournalTime() {
        return doubleJournalTime;
    }
    
    private String formatTime(String time){
        return String.join(".",time.split(":"));
    }

    @Override
    public String toString() {
        return "TableExcelModel{" +
                "day=" + day.get() +
                ", entryHour=" + entryHour.get() +
                ", exitHour=" + exitHour.get() +
                ", journalTime=" + journalTime.get() +
                ", doubleEntryHour=" + doubleEntryHour +
                ", doubleExitHour=" + doubleExitHour +
                ", doubleJournalTime=" + doubleJournalTime +
                '}';
    }
}
