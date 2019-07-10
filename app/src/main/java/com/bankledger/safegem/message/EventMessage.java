package com.bankledger.safegem.message;

/**
 * Date：2018/9/8
 * Author: bankledger
 */
public class EventMessage {

    public int getMessage() {
        return message;
    }

    public void setMessage(int message) {
        this.message = message;
    }

    public EventMessage(int message) {
        this.message = message;
    }

    public EventMessage() {
    }

    private int message;


}
