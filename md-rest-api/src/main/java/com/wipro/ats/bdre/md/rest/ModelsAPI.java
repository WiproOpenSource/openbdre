package com.wipro.ats.bdre.md.rest;

import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.dao.ModelPropertiesDAO;
import com.wipro.ats.bdre.md.dao.ModelsDAO;
import com.wipro.ats.bdre.md.dao.jpa.Messages;
import com.wipro.ats.bdre.md.dao.jpa.ModelProperties;
import com.wipro.ats.bdre.md.dao.jpa.ModelPropertiesId;
import com.wipro.ats.bdre.md.dao.jpa.Models;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.Map;

/**
 * Created by cloudera on 10/21/17.
 */

@Controller
@RequestMapping("/models")
public class ModelsAPI extends MetadataAPIBase {

    private static final Logger LOGGER = Logger.getLogger(ModelsAPI.class);
    @Override
    public Object execute(String[] params) {
        return null;
    }
    @Autowired
    ModelsDAO modelsDAO;
    @Autowired
    ModelPropertiesDAO modelPropertiesDAO;

    @RequestMapping(value = {"/createModels"}, method = RequestMethod.POST)

    @ResponseBody
    public
    RestWrapper createModel(@RequestParam Map<String, String> map, Principal principal) {
        LOGGER.debug(" value of map is " + map.size());
        RestWrapper restWrapper = null;
        Models model=new Models();

        for(String key:map.keySet()) {
            if (!("").equals(map.get(key))) {
                if (key.startsWith("Model_")) {
                    if (key.contains("modelName")) {
                        model.setModelName(map.get(key));
                    } else if (key.contains("modelType")) {
                        model.setModelType(map.get(key));
                    } else if (key.contains("messageName")) {
                        Messages message = new Messages();
                        message.setMessageName(map.get(key));
                        model.setMessages(message);
                    } else if (key.contains("continuousFeatures")) {
                        model.setContinuousFeatures(map.get(key));
                    } else if (key.contains("categoryFeatures")) {
                        model.setCategoricalFeatures(map.get(key));
                    }
                }
            }
        }
        modelsDAO.insert(model);
            for(String key1:map.keySet()){
                if(!("").equals(map.get(key1))){
                    if(key1.startsWith("ModelProperties_")){
                        ModelProperties modelProperties=new ModelProperties();
                        ModelPropertiesId modelPropertiesId=new ModelPropertiesId();
                        modelPropertiesId.setModelName(model.getModelName());
                        modelPropertiesId.setPropKey(key1);
                        modelProperties.setId(modelPropertiesId);
                        modelProperties.setModels(model);
                        modelProperties.setPropValue(map.get(key1));
                        modelProperties.setDescription("Properties for "+model.getModelType());
                        modelProperties.setConfigGroup(model.getModelType());
                        modelPropertiesDAO.insert(modelProperties);
                    }
                }
        }


            restWrapper = new RestWrapper(model, RestWrapper.OK);

        return restWrapper;
    }


}
