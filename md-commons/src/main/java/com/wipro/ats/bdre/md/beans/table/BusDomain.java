/*
 * Copyright 2015 Wipro Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wipro.ats.bdre.md.beans.table;

import javax.validation.constraints.*;

/**
 * Created by kapil on 12-01-2015.
 */

/**
 * This class contains all the setter and getter methods for BusDomain fields.
 */
public class BusDomain {


    @Min(value = 1)
    @Digits(fraction = 0, integer = 11)
    private Integer busDomainId;
    private Integer pageSize;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Size(max = 256)
    @NotNull
    private String description;
    @NotNull
    @Pattern(regexp = "([0-z][' ']?)+")
    @Size(max = 45)
    private String busDomainName;
    @NotNull
    @Pattern(regexp = "([0-z][' ']?)+")
    @Size(max = 45)
    private String busDomainOwner;
    private Integer page;
    private Integer counter;

    @Override
    public String toString() {
        return " busDomainId:" + busDomainId + " page:" + page + " description:" + description.substring(0, Math.min(description.length(), 45)) + " busDomainName:" + busDomainName + " busDomainOwner:" + busDomainOwner;
    }

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }


    public String getBusDomainOwner() {
        return busDomainOwner;
    }

    public void setBusDomainOwner(String busDomainOwner) {
        this.busDomainOwner = busDomainOwner;
    }

    public String getBusDomainName() {
        return busDomainName;
    }

    public void setBusDomainName(String busDomainName) {
        this.busDomainName = busDomainName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getBusDomainId() {
        return busDomainId;
    }

    public void setBusDomainId(Integer busDomainId) {
        this.busDomainId = busDomainId;
    }
}
