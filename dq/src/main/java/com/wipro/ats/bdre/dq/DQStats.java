/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 *
 * This code is protected by copyright and distributed under
 * licenses restricting copying, distribution and decompilation.
 */

package com.wipro.ats.bdre.dq;

/**
 * Created by arijit on 3/6/15.
 */
public class DQStats {
    private long numGood;
    private long numBad;
    private float goodPercent;
    private float threshold;

    public long getNumGood() {
        return numGood;
    }

    public void setNumGood(long numGood) {
        this.numGood = numGood;
    }

    public long getNumBad() {
        return numBad;
    }

    public void setNumBad(long numBad) {
        this.numBad = numBad;
    }

    public float getGoodPercent() {
        return goodPercent;
    }

    public void setGoodPercent(float goodPercent) {
        this.goodPercent = goodPercent;
    }

    public float getThreshold() {
        return threshold;
    }

    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }

    @Override
    public String toString() {
        return "Data quality check result " +
                "Bad records="+this.getNumBad()+"; Good records="+this.getNumGood()+"; " +
                "%Passed="+this.getGoodPercent()+"; %Minimum="+this.getThreshold();
    }
}
