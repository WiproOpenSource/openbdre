package com.wipro.ats.bdre.md.rest.ext;

import com.wipro.ats.bdre.md.rest.RestWrapper;
import com.wipro.ats.bdre.md.rest.RestWrapperOptions;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by SU324335 on 3/29/2016.
 */
public class HiveTableMigrationAPITest {
    private static final Logger LOGGER = Logger.getLogger(HiveTableMigrationAPITest.class);

    @Test
    @Ignore
    public void getDBList()
    {
        HiveTableMigrationAPI hiveTableMigrationAPI = new HiveTableMigrationAPI();
        RestWrapperOptions restWrapperOptions = null;
     //   restWrapperOptions = hiveTableMigrationAPI.getDBList();
    }

    @Test
    @Ignore
    public void getTablesList()
    {
        HiveTableMigrationAPI hiveTableMigrationAPI = new HiveTableMigrationAPI();
        Map<String,String> map = new TreeMap<String, String>();
        map.put("srcDB_default","default");
        RestWrapperOptions restWrapperOptions = hiveTableMigrationAPI.getTableList(map);
        LOGGER.info(restWrapperOptions.getOptions().toString());
    }
}
