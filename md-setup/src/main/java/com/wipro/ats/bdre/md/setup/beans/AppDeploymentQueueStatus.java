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

package com.wipro.ats.bdre.md.setup.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by SU324335 on 3/8/2016.
 */
@Entity
@Table(name="APP_DEPLOYMENT_QUEUE_STATUS"

)
public class AppDeploymentQueueStatus  implements java.io.Serializable {

    private Integer appDeploymentStatusId;
    private String description;

        public AppDeploymentQueueStatus() {
    }

    public AppDeploymentQueueStatus(Integer appDeploymentStatusId, String description) {
        this.appDeploymentStatusId = appDeploymentStatusId;
        this.description = description;
    }

    @Id
    @Column(name="APP_DEPLOYMENT_STATUS_ID", unique=true, nullable=false)
    public Integer getAppDeploymentStatusId() {
        return this.appDeploymentStatusId;
    }

    public void setAppDeploymentStatusId(Integer appDeploymentStatusId) {
        this.appDeploymentStatusId = appDeploymentStatusId;
    }

    @Column(name="DESCRIPTION", nullable=false, length=45)
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }




}
