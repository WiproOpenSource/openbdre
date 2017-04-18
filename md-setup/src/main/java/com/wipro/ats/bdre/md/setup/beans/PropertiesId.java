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

public class PropertiesId  implements java.io.Serializable {


     private Integer processId;
     private String propKey;

    public PropertiesId() {
    }

    public PropertiesId(Integer processId, String propKey) {
       this.processId = processId;
       this.propKey = propKey;
    }
   
    public Integer getProcessId() {
        return this.processId;
    }
    
    public void setProcessId(Integer processId) {
        this.processId = processId;
    }

    public String getPropKey() {
        return this.propKey;
    }
    
    public void setPropKey(String propKey) {
        this.propKey = propKey;
    }


   public boolean equals(Object other) {
         if ( (this == other ) ) return true;
		 if ( (other == null ) ) return false;
		 if ( !(other instanceof PropertiesId) ) return false;
		 PropertiesId castOther = ( PropertiesId ) other; 
         
		 return ( (this.getProcessId()==castOther.getProcessId()) || ( this.getProcessId()!=null && castOther.getProcessId()!=null && this.getProcessId().equals(castOther.getProcessId()) ) )
 && ( (this.getPropKey()==castOther.getPropKey()) || ( this.getPropKey()!=null && castOther.getPropKey()!=null && this.getPropKey().equals(castOther.getPropKey()) ) );
   }
   
   public int hashCode() {
         int result = 17;
         
         result = 37 * result + ( getProcessId() == null ? 0 : this.getProcessId().hashCode() );
         result = 37 * result + ( getPropKey() == null ? 0 : this.getPropKey().hashCode() );
         return result;
   }   


}


