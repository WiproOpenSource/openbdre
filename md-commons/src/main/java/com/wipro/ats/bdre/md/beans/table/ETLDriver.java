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

package com.wipro.ats.bdre.md.beans.table;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by kapil on 12-01-2015.
 */

/**
 * This class contains all the setter and getter methods for ETLDriver fields.
 */
public class ETLDriver {

    @Min(value = 1)
    @Digits(fraction = 0, integer = 11)
    private Integer eTLProcessId;
    @NotNull
    @Min(value = 1)
    @Digits(fraction = 0, integer = 11)
    private Integer rawTableId;
    private Integer pageSize;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @NotNull
    @Min(value = 1)
    @Digits(fraction = 0, integer = 11)
    private Integer baseTableId;
    @NotNull
    private Integer insertType;
    @NotNull
    private Integer dropRaw;
    @NotNull
    @Min(value = 1)
    @Digits(fraction = 0, integer = 11)
    private Integer rawViewId;

    @Override
    public String toString() {
        return " eTLProcessId:" + eTLProcessId + " rawTableId:" + rawTableId + " baseTableId:" + baseTableId + " insertType:" + insertType +
                " dropRaw:" + dropRaw + " rawViewId:" + rawViewId + " page:" + page;
    }

    public Integer getDropRaw() {
        return dropRaw;
    }

    public void setDropRaw(Integer dropRaw) {
        this.dropRaw = dropRaw;
    }

    public Integer getRawViewId() {
        return rawViewId;
    }

    public void setRawViewId(Integer rawViewId) {
        this.rawViewId = rawViewId;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }

    private Integer page;
    private Integer counter;

    public Integer geteTLProcessId() {
        return eTLProcessId;
    }

    public void seteTLProcessId(Integer eTLProcessId) {
        this.eTLProcessId = eTLProcessId;
    }

    public Integer getRawTableId() {
        return rawTableId;
    }

    public void setRawTableId(Integer rawTableId) {
        this.rawTableId = rawTableId;
    }

    public Integer getBaseTableId() {
        return baseTableId;
    }

    public void setBaseTableId(Integer baseTableId) {
        this.baseTableId = baseTableId;
    }

    public Integer getInsertType() {
        return insertType;
    }

    public void setInsertType(Integer insertType) {
        this.insertType = insertType;
    }

}
