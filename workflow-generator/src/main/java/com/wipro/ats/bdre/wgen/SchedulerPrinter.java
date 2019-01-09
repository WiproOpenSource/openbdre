package com.wipro.ats.bdre.wgen;

/**
 * Created by cloudera on 8/3/16.
 */
public class SchedulerPrinter {
    public String execute(String coordinatorName){
        return " <coordinator-app name=\""+coordinatorName+"\" frequency=\"${frequency}\" start=\"${startTime}\" end=\"${endTime}\" timezone=\"${timezone}\" xmlns=\"uri:oozie:coordinator:0.1\">\n" +
                "   <action>\n" +
                "       <workflow>\n" +
                "           <app-path>${workflowPath}</app-path>\n" +
                "       </workflow>\n" +
                "   </action>\n" +
                "</coordinator-app>" ;
    }
}
