/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
