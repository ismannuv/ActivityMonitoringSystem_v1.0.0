/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.senergy.ams.model;

import SIPLlib.DBaccess2;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 *
 * @author admin
 */
public abstract class DBentity implements JsonEntity{
    static final public int MISC_OPERATION=0;
    static final public int ADD=1;
    static final public int UPDATE=2;
    static final public int PERMANENT_DELETE=3;
    static final public int TEMPORARY_DELETE=4;
    static final public int RESTORE=5;
    static final public int GET_NEXT=6;
    static final public int GET_ALL=7;
    static final public int GET_ALL_ENABLED=8;
    static final public int GET_ALL_DISABLED=9;
    static final public int EXPORT=10;    
    static final public int IMPORT=11;    
    static final public int GET=12;        
    static final public int ENROLL=13;      
    static final public int GET_COUNT=14;   
    static final public int UPDATE_PARAM=15;
    
    public final String entityName;
    protected DBaccess2 DBcon;
    protected int disabled;
    public long sessionId = 0;
    

    protected DBentity(String entityName,int disabled)
    {
        this.entityName=entityName;
        this.disabled=disabled;
    }
    protected boolean createDBcon(DBaccess2 conObj)
    {
        if(conObj==null)
        {
            DBcon=DBconnection.newInstance();
            return true;
        }
        else
        {
            DBcon=conObj;
            return false;
        }
    }
    public String getAuditLog(int operation)
    {
        return this.toJson().toString();
    }
    abstract public String getIdentifier();
    abstract public boolean add(DBaccess2 conObj) throws DBoperationException;
    abstract public boolean update(DBaccess2 conObj) throws DBoperationException;
    abstract public boolean updateParam(String id,String paramName,String newVal,JsonObject obj) throws DBoperationException;
    abstract public boolean permanentDelete(DBaccess2 conObj) throws DBoperationException;
    abstract public JsonArray permanentDelete(DBaccess2 conObj,JsonObject obj) throws DBoperationException;
    abstract public boolean temporarydelete(DBaccess2 conObj) throws DBoperationException;
    abstract public boolean temporarydelete(DBaccess2 conObj,JsonObject obj) throws DBoperationException;
    abstract public boolean restore(DBaccess2 conObj) throws DBoperationException;
    abstract public boolean restore(DBaccess2 conObj,JsonObject obj) throws DBoperationException;
    abstract public DBentity get(DBaccess2 conObj,JsonObject obj) throws DBoperationException;
    abstract public long getCount(DBaccess2 conObj,JsonObject filter) throws DBoperationException; //filter:{all:7/8/9}
    abstract public DBentity[] getAll(DBaccess2 conObj,JsonObject filter) throws DBoperationException; //filter:{all:7/8/9}
    abstract public DBentity[] export(DBaccess2 conObj,JsonObject filter) throws DBoperationException; //filter:{all:7/8/9}
    abstract public JsonArray importCSV(DBaccess2 conObj,JsonArray arr) throws DBoperationException;
   
    public JsonObject miscellaneousOperation(JsonObject obj) throws DBoperationException
    {
        JsonObject ret=new JsonObject();
        ret.add("result", new JsonObject());
        return ret;
    }
            
    public static String getOperationName(int operation)
    {
        switch(operation)
        {
            case DBentity.MISC_OPERATION:
                return "";
            case DBentity.ADD:
                return "Add";
            case DBentity.UPDATE:
                return "Update";
            case DBentity.PERMANENT_DELETE:
                return "Permanent Delete";
            case DBentity.TEMPORARY_DELETE:
                return "Disable";
            case DBentity.RESTORE:
                return "Restore";                
            case DBentity.GET_ALL:
                return "Get All";             
            case DBentity.GET:
                return "Get";          
            case DBentity.EXPORT:
                return "Export";          
            case DBentity.IMPORT:
                return "Import";        
            case DBentity.GET_COUNT:
                return "Get Count";   
            case DBentity.UPDATE_PARAM:
                return "Update Param";
            default:
                return "Unknown Operation "+operation;                
        }
    }
    @Override
    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.add("disabled", new JsonPrimitive(this.disabled));
        return obj;
    }
}
