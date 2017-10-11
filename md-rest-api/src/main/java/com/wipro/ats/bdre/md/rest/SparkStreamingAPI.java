package com.wipro.ats.bdre.md.rest;

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.api.GetMessageColumns;
import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.*;

/**
 * Created by cloudera on 5/22/17.
 */
@Controller
@RequestMapping("/sparkstreaming")
public class SparkStreamingAPI extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(SparkStreamingAPI.class);

    @RequestMapping(value = "/getmessagecolumns/{id}", method = RequestMethod.POST)
    @ResponseBody
    public RestWrapperOptions listOptions(@PathVariable("id") Integer processId, Principal principal) {

        RestWrapperOptions restWrapperOptions = null;
        try{
            GetMessageColumns getMessageColumns = new GetMessageColumns();
            Set<String> columnNames = getMessageColumns.getMessageColumnNames(processId);
            LOGGER.info(columnNames);
            List<RestWrapperOptions.Option> options = new ArrayList<RestWrapperOptions.Option>();
            for (String column : columnNames) {
                RestWrapperOptions.Option option = new RestWrapperOptions.Option(column,column);
                options.add(option);
            }
            restWrapperOptions = new RestWrapperOptions(options, RestWrapperOptions.OK);
        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapperOptions = new RestWrapperOptions(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapperOptions;

    }





    @RequestMapping(value = "/getMessageList/{id}", method = RequestMethod.POST)
    @ResponseBody
    public RestWrapperOptions listMessageOptions(@PathVariable("id") Integer processId, Principal principal) {

        RestWrapperOptions restWrapperOptions = null;
        try{
            GetMessageColumns getMessageColumns = new GetMessageColumns();
            HashMap<Integer,String> tablePair = getMessageColumns.getMessageList(processId);
            List<RestWrapperOptions.Option> options = new ArrayList<RestWrapperOptions.Option>();
            Iterator it = tablePair.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                System.out.println(pair.getKey() + " = " + pair.getValue());
                RestWrapperOptions.Option option = new RestWrapperOptions.Option((String) pair.getValue(), pair.getKey());
                options.add(option);
                it.remove();
            }

            restWrapperOptions = new RestWrapperOptions(options, RestWrapperOptions.OK);
        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapperOptions = new RestWrapperOptions(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapperOptions;

    }




    @Override
    public Object execute(String[] params) {
        return null;
    }
}
