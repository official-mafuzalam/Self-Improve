package com.octosync.selfimprove;

public class Habit {
    public enum Status {
        PENDING, SUCCESS, FAILED
    }

    private int id;
    private int nameResId;
    private Status status;

    public Habit(int id, int nameResId) {
        this.id = id;
        this.nameResId = nameResId;
        this.status = Status.PENDING;
    }

    public int getId() {
        return id;
    }

    public int getNameResId() {
        return nameResId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}