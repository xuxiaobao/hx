package com.miaosu.service.locks;

import java.util.Date;

/**
 * lock
 */
public class Lock {
    private String name;

    private Boolean locked;

    private Date lockedTime;

    private String lockedBy;

    public Lock(String name, Boolean locked, Date lockedTime, String lockedBy) {
        this.name = name;
        this.locked = locked;
        this.lockedTime = lockedTime;
        this.lockedBy = lockedBy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public Date getLockedTime() {
        return lockedTime;
    }

    public void setLockedTime(Date lockedTime) {
        this.lockedTime = lockedTime;
    }

    public String getLockedBy() {
        return lockedBy;
    }

    public void setLockedBy(String lockedBy) {
        this.lockedBy = lockedBy;
    }
}
