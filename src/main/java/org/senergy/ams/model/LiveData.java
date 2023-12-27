package org.senergy.ams.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LiveData {

    public BlockingQueue<JsonNode> getAllQueueData= new LinkedBlockingQueue<>();
    public BlockingQueue<ArrayNode> getAllQueueDataArrayNode= new LinkedBlockingQueue<>();
    public boolean fetchingLargeData=false;
    public boolean startSending=false;
    private String error="";

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
        stopFetchingData();
    }
    public void setError(Exception ex){
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        // Write the exception's stack trace to the PrintWriter
        ex.printStackTrace(printWriter);

        // Flush and close the PrintWriter to get the stack trace as a String
        printWriter.flush();
        printWriter.close();

        this.error= stringWriter.toString();
    }

    public void startFetchingData(){
        getAllQueueData.clear();
        fetchingLargeData=true;
        error="";
    }

    public void stopFetchingData() {
        fetchingLargeData=false;
    }
}
