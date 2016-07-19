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

package com.wipro.ats.bdre.util;

import com.wipro.ats.bdre.annotation.PersistableCollectionBean;
import com.wipro.ats.bdre.annotation.PersistableParam;
import com.wipro.ats.bdre.exception.MetadataException;
import org.apache.log4j.Logger;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by pushpak on 18/07/2016.
 */
public final class AirflowUtil {
    private static final String AIRFLOW_ACTION_OUTPUT_PROPERTIES = "airflow.action.output.properties";
    private static final Logger LOGGER = Logger.getLogger(AirflowUtil.class);
    private Properties propertiesToBeSaved = new Properties();

    private void persistEmittedKeyValue() {
        try {
            String airflowProp = System.getProperty(AIRFLOW_ACTION_OUTPUT_PROPERTIES);
            if (airflowProp != null) {
                File propFile = new File(airflowProp);
                OutputStream os = new FileOutputStream(propFile);
                propertiesToBeSaved.store(os, "");
                os.close();
                LOGGER.info("Saved properties " + propertiesToBeSaved);
            } else {
                LOGGER.warn(AIRFLOW_ACTION_OUTPUT_PROPERTIES
                        + " System property not defined. Probably not running from Airflow");

            }
        } catch (IOException e) {
            LOGGER.error(e);
            throw new MetadataException(e);
        }
    }

    private void persistSingleBean(Object bean) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        if (bean == null) {
            throw new MetadataException();
        }
        boolean isBeanPartOfCollection = bean.getClass().isAnnotationPresent(PersistableCollectionBean.class);
        String qualifierFieldName = null;
        String qualifierFieldValue = null;
        Properties properties = new Properties();
        if (isBeanPartOfCollection) {
            qualifierFieldName = bean.getClass().getAnnotation(PersistableCollectionBean.class).qualifierField();
        }
        Field[] fields = bean.getClass().getDeclaredFields();
        Map<String, String> annotatedFieldsMap = new HashMap<String, String>();
        //Scan all bean fields
        for (Field field : fields) {
            //Persist only those fields annotated with @PersistableParam
            LOGGER.debug("field present=" + field.getName());
            if (field.isAnnotationPresent(PersistableParam.class)) {
                PersistableParam persistableParam = field.getAnnotation(PersistableParam.class);
                annotatedFieldsMap.put(field.getName(), persistableParam.keyName());
                LOGGER.debug("field annotated with PersistableParam=" + field.getName() + "; with keyName=" + persistableParam.keyName());

            }
        }
        for (PropertyDescriptor pd : Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors()) {
            //skip inherited getClass method
            if (pd.getReadMethod() != null && !"class".equals(pd.getName())) {
                if (annotatedFieldsMap.containsKey(pd.getName())) {
                    Object getterValue = pd.getReadMethod().invoke(bean);
                    if (getterValue instanceof Map) {
                        Map<String, String> getterValueMap = (Map<String, String>) getterValue;

                        for (String mapKey : getterValueMap.keySet()) {
                            properties.setProperty(annotatedFieldsMap.get(pd.getName()) + "." + mapKey,
                                    getterValueMap.get(mapKey) == null ? "" : getterValueMap.get(mapKey));
                        }

                    } else if (getterValue instanceof Set) {
                        Set<String> getterValueSet = (Set<String>) getterValue;
                        StringBuilder stringBuilder = new StringBuilder();

                        for (String s : getterValueSet) {
                            if (stringBuilder.length() != 0) {
                                stringBuilder.append(",");
                            }
                            stringBuilder.append(s);

                        }
                        properties.setProperty(annotatedFieldsMap.get(pd.getName()),
                                getterValue == null ? "" : stringBuilder.toString());

                    } else {
                        properties.setProperty(annotatedFieldsMap.get(pd.getName()),
                                getterValue == null ? "" : getterValue.toString());
                    }
                }
                if (isBeanPartOfCollection && qualifierFieldName.equals(pd.getName())) {
                    qualifierFieldValue = pd.getReadMethod().invoke(bean).toString();
                }
            }
        }
        for (String propertyName : properties.stringPropertyNames()) {
            String propertyKey = isBeanPartOfCollection ? propertyName + qualifierFieldValue : propertyName;
            propertiesToBeSaved.setProperty(propertyKey, properties.getProperty(propertyName));
        }


    }

    /**
     * This method make bean persist depending on annotations used.
     *
     * @param bean      The Object we want to persist
     * @param printOnly whether to print it or not
     */
    public void persistBeanData(Object bean, boolean printOnly) {
        persistBeanTryCatch(bean);
        callPersistEmittedKeyValue(printOnly);
    }

    /**
     * This method make beans persist depending on annotations used.
     *
     * @param beans     The List we want to persist
     * @param printOnly whether to print it or not
     */

    public void persistBeanList(List beans, boolean printOnly) {
        for (Object bean : beans) {
            persistBeanTryCatch(bean);
        }
        callPersistEmittedKeyValue(printOnly);
    }

    private void persistBeanTryCatch(Object bean){
        try {
            persistSingleBean(bean);
        } catch (IntrospectionException e) {
            LOGGER.error(e);
            throw new MetadataException(e);
        } catch (InvocationTargetException e) {
            LOGGER.error(e);
            throw new MetadataException(e);
        } catch (IllegalAccessException e) {
            LOGGER.error(e);
            throw new MetadataException(e);
        }
    }

    private void callPersistEmittedKeyValue(boolean printOnly){
        LOGGER.info("propertiesToBeSaved=" + propertiesToBeSaved);
        if (!printOnly) {
            persistEmittedKeyValue();
        }
    }

}
