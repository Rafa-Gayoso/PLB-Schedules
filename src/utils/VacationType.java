package utils;

public class VacationType {
    private String vacationDay;
    private double dayWorked;

    public VacationType(String vacationDay, double dayWorked) {
        this.vacationDay = vacationDay;
        this.dayWorked = dayWorked;
    }

    public String getVacationDay() {
        return vacationDay;
    }

    public void setVacationDay(String vacationDay) {
        this.vacationDay = vacationDay;
    }

    public double getDayWorked() {
        return dayWorked;
    }

    public void setDayWorked(double dayWorked) {
        this.dayWorked = dayWorked;
    }
}
