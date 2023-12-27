/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.senergy.ams.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.*;

import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author nbpatil
 */
public class JsonJackson {


    public int operation;
    public String type;
    public ArrayNode data;
    public long errorCode;
    public String error;
    public boolean status;
    public String timestamp;
    public String errorDeatails;

    public JsonJackson(int operation) {
        this.operation = operation;
        this.errorCode=0;
        this.error = "";
        this.errorDeatails="";
        ObjectMapper om=new ObjectMapper();
        this.data = om.createArrayNode();
        this.type = "";
        this.status = false;
        this.timestamp=Config.dateFormat.format(new Date());
    }

    public JsonJackson(String jsonStr) throws JsonProcessingException {
        this.operation = 0;
        this.errorCode=0;
        this.error = "";
        this.errorDeatails="";
//        this.data = new JsonArray();
        this.type = "";
        this.status = false;
        this.timestamp="";
        ObjectMapper om=new ObjectMapper();
        JsonNode jsonNode = om.readTree(jsonStr);
        decode(jsonNode);
//        JsonParser parser = new JsonParser();
//        JsonElement obj = parser.parse(jsonStr);
//         if (obj.isJsonObject()) {
//             JsonObject jsonObj = (JsonObject) obj;
//                decode(jsonObj);
//         }
    }
    private void decode(JsonNode jsonObj) throws JsonProcessingException {

        JsonNode obj ;

            obj = jsonObj.get("operation");
            if (obj != null) {
                this.operation = obj.asInt();
            }
            obj = jsonObj.get("type");
            if (obj != null) {
                this.type = obj.asText();
            }
            obj = jsonObj.get("data");
            if (obj != null && obj.isArray()) {
                this.data = (ArrayNode) obj;
            }
            obj = jsonObj.get("error");
            if (obj != null) {
                this.error = obj.asText();
            }
            obj = jsonObj.get("status");
            if (obj != null) {
                this.status = obj.asBoolean();
            }
    }

    @Override
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode parentNode = objectMapper.createObjectNode();
        parentNode.put("operation",this.operation);
        parentNode.put("type",this.operation);

        parentNode.set("data",this.data);

        ObjectNode err = objectMapper.createObjectNode();
        err.put("code",this.errorCode);
        err.put("message",this.error);
        err.put("cause",this.errorDeatails);

        parentNode.set("error",err);


        parentNode.put("status",this.status);
        parentNode.put("timestamp",this.timestamp);

        /*JsonObject obj = new JsonObject();
        obj.add("operation", new JsonPrimitive(this.operation));
        obj.add("type", new JsonPrimitive(this.type));
        obj.add("data", this.data);
        JsonObject err=new JsonObject();
        err.add("code",new JsonPrimitive(this.errorCode));
        err.add("message",new JsonPrimitive(this.error));
        err.add("cause", new JsonPrimitive(this.errorDeatails));
        obj.add("error", err);
        obj.add("status", new JsonPrimitive(this.status));
        obj.add("timestamp", new JsonPrimitive(this.timestamp));  
        return obj.toString();*/
        return parentNode.toString();
    }
    public void setError(Exception ex)
    {
        StringWriter errors = new StringWriter();
        ex.printStackTrace(new PrintWriter(errors));
        this.status=false;
        this.error=ex.getMessage();
        this.errorDeatails=errors.toString();
        if(ex instanceof DBoperationException)
            this.errorCode=((DBoperationException)ex).getCode();
        else
            this.errorCode=0;
    }
    public void setError(long code,String message,String cause)
    {
        this.status=false;
        this.errorCode=code;
        this.error=message;
        this.errorDeatails=cause;
    }
    public void setError(String message)
    {
        this.status=false;
        this.errorCode=1;
        this.error=message;
        this.errorDeatails=message;
    }
}
