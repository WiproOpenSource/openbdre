package com.wipro.ats.bdre.md.api;

import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.dao.MessagesDAO;
import com.wipro.ats.bdre.md.dao.ProcessDAO;
import com.wipro.ats.bdre.md.dao.PropertiesDAO;
import com.wipro.ats.bdre.md.dao.jpa.Messages;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import com.wipro.ats.bdre.md.dao.jpa.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.util.*;

/**
 * Created by cloudera on 5/22/17.
 */
public class GetMessageColumns extends MetadataAPIBase {

    @Autowired
    ProcessDAO processDAO;
    @Autowired
    PropertiesDAO propertiesDAO;
    @Autowired
    MessagesDAO messagesDAO;

    public GetMessageColumns(){
        AutowireCapableBeanFactory acbFactory = getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);

    }
    public static Map<Integer,Set<Integer>> prevMap = new HashMap();
    public static List<Integer> listOfSourcePids = new ArrayList();
    public static List<Integer> listOfTransformations = new ArrayList();
    public static List<Integer> listOfEmitters = new ArrayList();
    public static List<Integer> listOfSourcesForGivenPid = new ArrayList<Integer>();
    public static List<Integer> listOfPidWithNonEmptySchema = new ArrayList<Integer>();

    public static void main(String[] args) {
        // Set<String> columnNames = new GetMessageColumns().getMessageColumnNames(45);
        System.out.println("final result = " + new GetMessageColumns().getMessageColumnNames(44));
        //System.out.println("final result = " + new GetMessageColumns().getMessageList(45));
    }

    public Set<String> getColumnNames(Integer pid) {
        Process selectedProcess = processDAO.get(pid);
        Process parentProcess = selectedProcess.getProcess();
        Integer parentProcessId = parentProcess.getProcessId();
        String nextProcessOfParent = processDAO.get(parentProcessId).getNextProcessId();
        listOfSourcesForGivenPid.clear();
        prevMap.clear();
        listOfSourcePids.clear();
        for (String nextProcessId : nextProcessOfParent.split(",")) {
            listOfSourcePids.add(Integer.valueOf(nextProcessId));
        }

        Map<Integer, String> nextPidMap = new HashMap<Integer, String>();
        nextPidMap.put(parentProcessId, nextProcessOfParent);
        List<com.wipro.ats.bdre.md.dao.jpa.Process> jpaProcessList = processDAO.subProcesslist(parentProcessId);
        for (Process subProcess : jpaProcessList) {
            nextPidMap.put(subProcess.getProcessId(), subProcess.getNextProcessId());
        }


        List<Integer> currentUpstreamList = new ArrayList();

        currentUpstreamList.addAll(listOfSourcePids);
        //populate prevMap with source pids,
        for (Integer sourcePid : listOfSourcePids) {
            prevMap.put(sourcePid, null);
        }

        while (!currentUpstreamList.isEmpty()) {
            System.out.println("currentUpstreamList = " + currentUpstreamList);
            GetMessageColumns getMessageColumns = new GetMessageColumns();
            System.out.println(" calling identifyflows");
            getMessageColumns.identifyFlows(currentUpstreamList, nextPidMap,parentProcessId);
        }
        System.out.println("prevMap = " + prevMap);

        List<Integer> sourcesForGivenNode = new GetMessageColumns().recursivelyGetSources(pid);
        Set<String> columnNames = new HashSet<>();






        for(Integer sourcePid : sourcesForGivenNode){
            List<Properties> messageProperties =  propertiesDAO.getPropertiesForConfig(sourcePid, "message");

            if(messageProperties != null){
                Properties messageProperty = messageProperties.get(0);
                String messageName = messageProperty.getPropValue();

                Messages message = messagesDAO.get(messageName);
                String schema = message.getMessageSchema();
                String[] columnAndDataTypes = schema.split(",");
                for(String s : columnAndDataTypes){
                    columnNames.add(s.split(":")[0]);
                }
            }
        }
        return columnNames;
    }


    public HashMap<Integer,String> getMessageList(Integer pid) {
        Process selectedProcess = processDAO.get(pid);
        Process parentProcess = selectedProcess.getProcess();
        Integer parentProcessId = parentProcess.getProcessId();
        String nextProcessOfParent = processDAO.get(parentProcessId).getNextProcessId();
        listOfSourcesForGivenPid.clear();
        prevMap.clear();
        listOfSourcePids.clear();
        listOfPidWithNonEmptySchema.clear();
        for (String nextProcessId : nextProcessOfParent.split(",")) {
            listOfSourcePids.add(Integer.valueOf(nextProcessId));
        }



        Map<Integer, String> nextPidMap = new HashMap<Integer, String>();
        nextPidMap.put(parentProcessId, nextProcessOfParent);
        List<com.wipro.ats.bdre.md.dao.jpa.Process> jpaProcessList = processDAO.subProcesslist(parentProcessId);
        for (Process subProcess : jpaProcessList) {
            nextPidMap.put(subProcess.getProcessId(), subProcess.getNextProcessId());
        }

        List<Integer> currentUpstreamList = new ArrayList();
        currentUpstreamList.addAll(listOfSourcePids);
        //populate prevMap with source pids,
        for (Integer sourcePid : listOfSourcePids) {
            prevMap.put(sourcePid, null);
        }

        while (!currentUpstreamList.isEmpty()) {
            System.out.println("currentUpstreamList = " + currentUpstreamList);
            GetMessageColumns getMessageColumns = new GetMessageColumns();
            System.out.println(" calling identifyflows");
            getMessageColumns.identifyFlows(currentUpstreamList, nextPidMap,parentProcessId);
        }
        System.out.println("prevMap = in message list " + prevMap);
        HashMap<Integer,String> hashMap=new HashMap<>();
        System.out.println("prevMap.get(pid) "+prevMap.get(pid));
        if (prevMap.get(pid)!=null){
            for(Integer prevId:prevMap.get(pid))
            {
                List<Properties> messageProperties1 =  propertiesDAO.getPropertiesForConfig(prevId, "message");
                if (messageProperties1.size()!=0)
                {
                    Properties messageProperty = messageProperties1.get(0);
                    String messageName = messageProperty.getPropValue();
                    hashMap.put(prevId,messageName);
                }
                else
                {
                    Process process=processDAO.get(prevId);
                    hashMap.put(prevId,process.getProcessName());
                }


            }
        }
        return hashMap;
    }



    public Set<String> getMessageColumnNames(Integer pid) {
        Set<String> columnDetails = new HashSet<>();
        Process selectedProcess = processDAO.get(pid);
        Process parentProcess = selectedProcess.getProcess();
        Integer parentProcessId = parentProcess.getProcessId();
        String nextProcessOfParent = processDAO.get(parentProcessId).getNextProcessId();
        System.out.println(nextProcessOfParent +" "+pid);

        List<Properties> messageProperties1 =  propertiesDAO.getPropertiesForConfig(pid, "message");
        System.out.println("messageProperties1 " +messageProperties1);


        if(messageProperties1.size()!=0){
            Properties messageProperty = messageProperties1.get(0);
            if(nextProcessOfParent.contains(pid.toString())){
                String messageName = messageProperty.getPropValue();
                Messages message = messagesDAO.get(messageName);
                String schema = message.getMessageSchema();
                String[] columnAndDataTypes = schema.split(",");
                columnDetails.addAll(Arrays.asList(columnAndDataTypes));
            }
            else
            {
                String schema = messageProperty.getPropValue();
                String[] columnAndDataTypes = schema.split(",");
                columnDetails.addAll(Arrays.asList(columnAndDataTypes));
            }

        }


        if (columnDetails.size()!=0)
            return columnDetails;


















        listOfPidWithNonEmptySchema.clear();
        listOfSourcesForGivenPid.clear();
        prevMap.clear();
        listOfSourcePids.clear();

        for (String nextProcessId : nextProcessOfParent.split(",")) {
            listOfSourcePids.add(Integer.valueOf(nextProcessId));
        }



        Map<Integer, String> nextPidMap = new HashMap<Integer, String>();
        nextPidMap.put(parentProcessId, nextProcessOfParent);
        List<com.wipro.ats.bdre.md.dao.jpa.Process> jpaProcessList = processDAO.subProcesslist(parentProcessId);
        for (Process subProcess : jpaProcessList) {
            nextPidMap.put(subProcess.getProcessId(), subProcess.getNextProcessId());
        }

        List<Integer> currentUpstreamList = new ArrayList();
        currentUpstreamList.addAll(listOfSourcePids);
        //populate prevMap with source pids,
        for (Integer sourcePid : listOfSourcePids) {
            prevMap.put(sourcePid, null);
        }

        while (!currentUpstreamList.isEmpty()) {
            System.out.println("currentUpstreamList = " + currentUpstreamList);
            GetMessageColumns getMessageColumns = new GetMessageColumns();
            System.out.println(" calling identifyflows");
            getMessageColumns.identifyFlows(currentUpstreamList, nextPidMap,parentProcessId);
        }
        System.out.println("prevMap = " + prevMap);

        List<Integer> pidListWithNonEmptySchema = new GetMessageColumns().recursivelyGetNonEmptySchema(pid);
        System.out.println("pidListWithNonEmptySchema = " + pidListWithNonEmptySchema);



        for(Integer sourcePid : pidListWithNonEmptySchema){
            List<Properties> messageProperties =  propertiesDAO.getPropertiesForConfig(sourcePid, "message");
            System.out.println("messageProperties " +messageProperties);
            if(messageProperties.size()!=0){
                Properties messageProperty = messageProperties.get(0);
                if(listOfSourcePids.contains(sourcePid)){
                    String messageName = messageProperty.getPropValue();
                    Messages message = messagesDAO.get(messageName);
                    String schema = message.getMessageSchema();
                    String[] columnAndDataTypes = schema.split(",");
                    columnDetails.addAll(Arrays.asList(columnAndDataTypes));
                }
                else
                {
                    String schema = messageProperty.getPropValue();
                    String[] columnAndDataTypes = schema.split(",");
                    columnDetails.addAll(Arrays.asList(columnAndDataTypes));
                }

            }
        }
        return columnDetails;
    }

    public List<Integer> recursivelyGetNonEmptySchema(Integer pid){
        Set<Integer> prevPIds = prevMap.get(pid);
        System.out.println("prevPIds = " + prevPIds);
        if(prevPIds == null){

        }
        else{
            for(Integer prevPId: prevPIds){
                List<Properties> messageProperties =  propertiesDAO.getPropertiesForConfig(prevPId, "message");
                if (messageProperties.size()!=0)
                    listOfPidWithNonEmptySchema.add(prevPId);
                else
                    recursivelyGetNonEmptySchema(prevPId);
               /* if(listOfSourcePids.contains(prevPId) && !listOfSourcesForGivenPid.contains(prevPId)){
                    listOfSourcesForGivenPid.add(prevPId);
                    System.out.println("source pid = " + prevPId);
                }*/

            }
        }
        return listOfPidWithNonEmptySchema;
    }







    public List<Integer> recursivelyGetSources(Integer pid){
        Set<Integer> prevPIds = prevMap.get(pid);
        System.out.println("prevPIds = " + prevPIds);
        if(prevPIds == null){

        }
        else{
            for(Integer prevPId: prevPIds){
                if(listOfSourcePids.contains(prevPId) && !listOfSourcesForGivenPid.contains(prevPId)){
                    listOfSourcesForGivenPid.add(prevPId);
                    System.out.println("source pid = " + prevPId);
                }
                recursivelyGetSources(prevPId);
            }
        }
        return listOfSourcesForGivenPid;
    }

    public void identifyFlows(List<Integer> currentUpstreamList, Map<Integer,String> nextPidMap, Integer parentProcessId){
        //prevMapTemp holds the prev ids only for pids involved in current iteration
        Map<Integer,Set<Integer>> prevMapTemp = new HashMap();
        for(Integer currentPid:currentUpstreamList){

            String nextPidString = nextPidMap.get(currentPid);
            //splitting next process id with comma into string array
            String[] nextPidArray = nextPidString.split(",");
            //new array of integers to hold next pids
            int[] nextPids = new int[nextPidArray.length];
            //iterate through the string array to construct the prev map
            for(int i=0;i<nextPidArray.length;i++){
                //cast String to Integer
                nextPids[i]=Integer.parseInt(nextPidArray[i]);
                //populate prevMap with nextPid of currentPid
                new GetMessageColumns().add(nextPids[i],currentPid,prevMapTemp);
                new GetMessageColumns().add(nextPids[i],currentPid,prevMap);
            }
        }

       /* //update the currentUpstreamList with the keys of the prevMap i.e all unique next ids of current step will be upstreams of following iteration
        currentUpstreamList.clear();
        currentUpstreamList.addAll(prevMapTemp.keySet());
*/
        //update the currentUpstreamList with the keys of the prevMap i.e all unique next ids of current step will be upstreams of following iteration
        currentUpstreamList.clear();
        //if the set contains parentProcessId, remove it
        if (prevMapTemp.containsKey(parentProcessId))
            prevMapTemp.remove(parentProcessId);
        currentUpstreamList.addAll(prevMapTemp.keySet());
    }

    //method to add previous pids as values to list against given process-id as key
    public void add(Integer key, Integer newValue, Map<Integer,Set<Integer>> prevMap) {
        Set<Integer> currentValue = prevMap.get(key);
        if (currentValue == null) {
            currentValue = new HashSet<>();
            prevMap.put(key, currentValue);
        }
        currentValue.add(newValue);
    }

    @Override
    public Object execute(String[] params) {
        return null;
    }
}
