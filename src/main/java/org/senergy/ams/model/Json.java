/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.senergy.ams.model;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author nbpatil
 */
public class Json {

    public int operation;
    public String type;
    public JsonArray data;
    public long errorCode;
    public String error;
    public boolean status;
    public String timestamp;
    public String errorDeatails;

    public Json(int operation) {
        this.operation = operation;
        this.errorCode=0;
        this.error = "";        
        this.errorDeatails="";
        this.data = new JsonArray();
        this.type = "";
        this.status = false;
        this.timestamp=Config.dateFormat.format(new Date());
    }

    public Json(String jsonStr) {
        this.operation = 0;
        this.errorCode=0;
        this.error = "";
        this.errorDeatails="";
        this.data = new JsonArray();
        this.type = "";
        this.status = false;
        this.timestamp="";
        JsonParser parser = new JsonParser();
        JsonElement obj = parser.parse(jsonStr);
         if (obj.isJsonObject()) {
             JsonObject jsonObj = (JsonObject) obj;
                decode(jsonObj);
         }
    }
    
    public Json(JsonObject jsonObj) {
        this.operation = 0;
        this.errorCode=0;
        this.error = "";
        this.errorDeatails="";
        this.data = new JsonArray();
        this.type = "";
        this.status = false;
        this.timestamp="";
       decode(jsonObj);
         
    }
    public static JsonObject getAsObject(String jsonStr)
    {
        try{
            JsonParser parser = new JsonParser();
            JsonElement obj = parser.parse(jsonStr);
            if (obj.isJsonObject()) {
                return (JsonObject) obj;
            }
        }
        catch(Exception e)
        {
            
        }
        return new JsonObject();
    }
    public static JsonArray getAsArray(String jsonStr)
    {
        try{
            JsonParser parser = new JsonParser();
            JsonElement obj = parser.parse(jsonStr);
            if (obj.isJsonArray()) {
                return (JsonArray) obj;
            }
        }
        catch(Exception e)
        {
            
        }
        return new JsonArray();
    }
    private void decode(JsonObject jsonObj) {
        JsonElement obj ;
            obj = jsonObj.get("operation");
            if (obj != null) {
                this.operation = obj.getAsInt();
            }
            obj = jsonObj.get("type");
            if (obj != null) {
                this.type = obj.getAsString();
            }
            obj = jsonObj.get("data");
            if (obj != null && obj.isJsonArray()) {
                this.data = obj.getAsJsonArray();
            }
            obj = jsonObj.get("error");
            if (obj != null) {
                this.error = obj.getAsString();
            }
            obj = jsonObj.get("status");
            if (obj != null) {
                this.status = obj.getAsBoolean();
            }
    }

    @Override
    public String toString() {
        JsonObject obj = new JsonObject();
        obj.add("operation", new JsonPrimitive(this.operation));
        obj.add("type", new JsonPrimitive(this.type));
        obj.add("data", this.data);
       /* BigInteger code=BigInteger.valueOf(-1);
        if(code.compareTo(BigInteger.ZERO)<0)
        {
            code=code..not();
            code=code.add(BigInteger.ONE);
            Integer.
        }*/
        JsonObject err=new JsonObject();
        err.add("code",new JsonPrimitive(this.errorCode));
        err.add("message",new JsonPrimitive(this.error));
        err.add("cause", new JsonPrimitive(this.errorDeatails));
        obj.add("error", err);
        obj.add("status", new JsonPrimitive(this.status));
        obj.add("timestamp", new JsonPrimitive(this.timestamp));  
        return obj.toString();
    }
    public JsonObject toJsonObject() {
        JsonObject obj = new JsonObject();
        obj.add("operation", new JsonPrimitive(this.operation));
        obj.add("type", new JsonPrimitive(this.type));
        obj.add("data", this.data);
       /* BigInteger code=BigInteger.valueOf(-1);
        if(code.compareTo(BigInteger.ZERO)<0)
        {
            code=code..not();
            code=code.add(BigInteger.ONE);
            Integer.
        }*/
        JsonObject err=new JsonObject();
        err.add("code",new JsonPrimitive(this.errorCode));
        err.add("message",new JsonPrimitive(this.error));
        err.add("cause", new JsonPrimitive(this.errorDeatails));
        obj.add("error", err);
        obj.add("status", new JsonPrimitive(this.status));
        obj.add("timestamp", new JsonPrimitive(this.timestamp));  
        return obj;
    }
    public static JsonArray encode(Object[][] obj) {
        JsonArray rowdata = new JsonArray();
        if (obj != null) {
            for (int i = 0; i < obj.length; i++) {
                JsonArray row = new JsonArray();
                for (int j = 0; j < obj[i].length; j++) {
                    if (obj[i][j] == null) {
                        obj[i][j] = "";
                    }
                    row.add(new JsonPrimitive(obj[i][j].toString()));
                }
                rowdata.add(row);
            }
        }
        return rowdata;
    }

    public static JsonArray encode(String[] columns) {
        JsonArray array = new JsonArray();
        for (int i = 0; i < columns.length; i++) {
            array.add(new JsonPrimitive(columns[i]));
        }
        return array;
    }

    public static String display(String jsonStr) {
        String str = "";
        JsonParser parser = new JsonParser();
        JsonElement obj = parser.parse(jsonStr);
        if (obj.isJsonObject()) {
            JsonObject json = (JsonObject) obj;
            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                JsonElement e = entry.getValue();
                if (e.isJsonArray() || e.isJsonObject()) {

                    str += entry.getKey() + ":[" + display(e.toString()) + "]<br>";
                } else {
                    str += entry.getKey() + ":" + entry.getValue().getAsString() + "<br>";
                }

            }
        } else if (obj.isJsonArray()) {
            JsonArray jsonArr = (JsonArray) obj;
            for (int i = 0; i < jsonArr.size(); i++) {
                JsonElement e = jsonArr.get(i);
                if (e.isJsonArray() || e.isJsonObject()) {
                    if (i != 0) {
                        str += ",";
                    }
                    str += " " + display(e.toString());
                    
                } else {
                    str += e.getAsString() + "<br>";
                }
            }

        }
        return str;
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
    public static JsonObject parseObject(String jsonStr)
    {
        try{
            JsonParser parser = new JsonParser();
            JsonElement obj = parser.parse(jsonStr);
            if (obj.isJsonObject()) {
                return (JsonObject) obj;
            }
        }
        catch(Exception e)
        {
            
        }
        return null;
    }
    public static JsonArray parseArray(String jsonStr)
    {
        try{
            JsonParser parser = new JsonParser();
            JsonElement obj = parser.parse(jsonStr);
            if (obj.isJsonArray()) {
                return (JsonArray) obj;
            }
        }
        catch(Exception e)
        {
            
        }
        return null;
    }
    public static JsonObject parseObject(Reader fr)
    {
        try{
            JsonParser parser = new JsonParser();
            JsonElement obj = parser.parse(fr);
            if (obj.isJsonObject()) {
                return (JsonObject) obj;
            }
        }
        catch(Exception e)
        {
            
        }
        return null;
    }
    public static JsonArray parseArray(Reader fr)
    {
        try{
            JsonParser parser = new JsonParser();
            JsonElement obj = parser.parse(fr);
            if (obj.isJsonArray()) {
                return (JsonArray) obj;
            }
        }
        catch(Exception e)
        {
            
        }
        return null;
    }
}
