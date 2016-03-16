package com.wipro.ats.bdre.md.app;
import java.util.List;

/**
 * Created by cloudera on 3/10/16.
 */
public class AppStore {
    public List<StoreJson> getApplicationList() {
        return applicationList;
    }

    public void setApplicationList(List<StoreJson> applicationList) {
        this.applicationList = applicationList;
    }

    List<StoreJson> applicationList;
}
