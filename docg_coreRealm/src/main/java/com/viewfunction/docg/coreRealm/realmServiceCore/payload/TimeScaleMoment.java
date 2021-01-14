package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

public class TimeScaleMoment {

    private int year=0;
    private int month=1;
    private int day=1;
    private int hour=0;
    private int minute=0;
    private int second=0;
    private int week=1;

    public TimeScaleMoment(){}

    public TimeScaleMoment(int year,int month){
        this.year = year;
        this.month = month;
    }

    public TimeScaleMoment(int year,int month,int day){
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public TimeScaleMoment(int year, int month,int day,int hour){
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
    }

    public TimeScaleMoment(int year, int month,int day,int hour,int minute){
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

    public TimeScaleMoment(int year, int month,int day,int hour,int minute,int second){
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }
}
