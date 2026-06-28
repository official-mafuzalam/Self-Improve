package com.octosync.selfimprove;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "habits")
public class Habit {
    public enum Status {
        PENDING, SUCCESS, FAILED
    }

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private Status status;

    public Habit(String name) {
        this.name = name;
        this.status = Status.PENDING;
    }

    // Room needs this
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}