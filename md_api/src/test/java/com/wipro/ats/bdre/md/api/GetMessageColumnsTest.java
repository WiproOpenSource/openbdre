package com.wipro.ats.bdre.md.api;

import com.wipro.ats.bdre.md.beans.ProcessInfo;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.Set;

/**
 * Created by cloudera on 5/22/17.
 */
public class GetMessageColumnsTest {
    private static final Logger LOGGER = Logger.getLogger(GetMessageColumnsTest.class);

    @Ignore
    @Test
    public void testGetParentProcess() throws Exception {
        GetMessageColumns getMessageColumns = new GetMessageColumns();
        Set<String> sourceIds = getMessageColumns.getColumnNames(3);
        LOGGER.info(sourceIds.toString());
    }
}
