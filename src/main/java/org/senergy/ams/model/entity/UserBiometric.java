/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.senergy.ams.model.entity;

import SIPLlib.DBaccess2;
import SIPLlib.DataTable;
import SIPLlib.Helper;
import SIPLlib.SIPLlibException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.gson.JsonArray;
import org.senergy.ams.model.DBentity;
import org.senergy.ams.model.DBoperationException;

/**
 *
 * @author admin
 */
public class UserBiometric extends DBentity{
    
    public final static String ENTITY_NANE="User Biometric";
    public final static int PRIVILEGE_BYTE=4;
    public static final int MISC_SEARCH=51;
    public final static String[] FP_NAMES=new String[]{"","Right Thumb","Right Index Finger","Right Middle Finger","Right Ring Finger","Right Little Finger","Left Thumb","Left Index Finger","Left Middle Finger","Left Ring Finger","Left Little Finger"};
    
    public final static int FP=1,IRIS=2;
    
    public String userId;
    public String userName;
    public int fp1;
    public int fp2;
    public int type;
    public int index;
    public int quality;
    public byte[] data;
    
    public UserBiometric()
    {
        super(ENTITY_NANE,0);
    }
    public UserBiometric(String userId,int type,int index)
    {
        super(ENTITY_NANE,0);
        this.userId=userId;
        this.type=type;
        this.index=index;
    }
    public UserBiometric(String userId,String userName,int type,int index,byte[] data)
    {
        super(ENTITY_NANE,0);
        this.userId=userId;
        this.userName=userName;
        this.type=type;
        this.index=index;
        this.data=data;
    }
    public UserBiometric(String userId,String userName,int type,int index,byte[] data,int fp1,int fp2)
    {
        super(ENTITY_NANE,0);
        this.userId=userId;
        this.userName=userName;
        this.fp1=fp1;
        this.fp2=fp2;
        this.type=type;
        this.index=index;
        this.data=data;
    }
    public UserBiometric(String userId,int type,int index,int quality,byte[] data)
    {
        super(ENTITY_NANE,0);
        this.userId=userId;
        this.type=type;
        this.index=index;
        this.quality=quality;
        this.data=data;
    }
    
//    @Override
//    public JsonObject miscellaneousOperation(JsonNode obj) throws DBoperationException
//    {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//
//        /*JsonObject ret=new JsonObject();
//        JsonObject result=new JsonObject();
//        int operation = obj.get("operation").getAsInt();
//        switch(operation)
//        {
//            case MISC_SEARCH:
//            {
//                try{
//                    result.add("data", search(obj));
//                    result.add("status", new JsonPrimitive(true));
//                }
//                catch(SIPLlibException e)
//                {
//
//                }
//            }
//            break;
//        }
//        ret.add("result", result);
//        return ret;*/
//    }
//    public static JsonArray search(JsonObject obj) throws SIPLlibException
//    {
//        JsonArray arr=new JsonArray();
//        String id=obj.get("id").getAsString();
//        int location=obj.get("locationId").getAsInt();
//        DBaccess2 DBcon= DBconnection.newInstance();
//        if(DBcon.dqlQuery("SELECT u.userId,ul.name,u.`index`,hex(u.data) as 'data' from locationusers lu left join user ul on ul.id=lu.userId left join userbiometrics u ON u.userId=lu.userId where concat(u.userId,'_', u.`type`,'_',u.`index`)='"+id+"' and lu.locationId="+location))
//        {
//            DataTable dt=DBcon.getResultSet();
//            if(dt!=null)
//            {
//                if(dt.next())
//                {
//                    JsonArray objarr=new JsonArray();
//                    for(int i=0;i<dt.getColumnCount();i++)
//                    {
//                        objarr.add(new JsonPrimitive(dt.getObject(i)+""));
//                    }
//                    arr.add(objarr);
//                }
//            }
//        }
//        return arr;
//    }
    public boolean isOldCabinetLocation(int locationId) throws SIPLlibException
    {
        this.createDBcon(DBcon);
        if(this.DBcon.dqlQuery("SELECT id FROM location WHERE id="+locationId+" AND (conv(hex(SUBSTR( features,1,1)),16,10)&1)=1"))
        {
            DataTable dt=this.DBcon.getResultSet();
            if(dt.getRowCount()>0 && dt.next())
            {
                if(dt.getInt("id")==locationId)
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean add(DBaccess2 conObj) throws DBoperationException {
       this.createDBcon(conObj);
       try{           
         DBcon.preparedQuery("insert into userbiometrics(`userId`,`type`,`index`,`data`,`quality`) values('"+this.userId+"',"+this.type+","+this.index+",?,"+this.quality+")",this.data);
         //sync(DBcon,this.sessionId,SyncEntity.UPLOAD,"'"+this.userId+"_"+this.type+"_"+this.index+"'",SyncEntity.ASSIGNED_CABINETS); //upload on assigned cabinets
         return true;
       }
       catch(SIPLlibException ex)
       {
           throw new DBoperationException(ADD, ex);
       }
    }

    @Override
    public boolean update(DBaccess2 conObj) throws DBoperationException {
       this.createDBcon(conObj);
       try{     
         DBcon.preparedQuery("update userbiometrics set quality="+this.quality+",data=? where userId='"+this.userId+"' and `type`=1 and `index`="+this.index,this.data);  
        // sync(DBcon,this.sessionId,SyncEntity.UPLOAD,"'"+this.userId+"_"+this.type+"_"+this.index+"'",SyncEntity.ASSIGNED_CABINETS); //upload on assigned cabinets
         return true;
       }
       catch(SIPLlibException ex)
       {
           throw new DBoperationException(ADD, ex);
       }
    }

    @Override
    public boolean updateParam(String id, String paramName, String newVal, JsonNode obj) throws DBoperationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean permanentDelete(DBaccess2 conObj) throws DBoperationException {
        this.createDBcon(conObj);
       try{           
         DBcon.dmlQuery("delete from userbiometrics where userId='"+this.userId+"' and `type`="+this.type+" and `index`="+this.index);  
//         sync(DBcon,this.sessionId,SyncEntity.DELETE,"'"+this.userId+"_"+this.type+"_"+this.index+"'",SyncEntity.ALL_CABINETS,0,null); //upload on assigned cabinets
         return true;
       }
       catch(SIPLlibException ex)
       {
           throw new DBoperationException(ADD, ex);
       }
    }

    @Override
    public JsonArray permanentDelete(DBaccess2 conObj, JsonNode obj) throws DBoperationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean temporarydelete(DBaccess2 conObj) throws DBoperationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean temporarydelete(DBaccess2 conObj, JsonNode obj) throws DBoperationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean restore(DBaccess2 conObj) throws DBoperationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean restore(DBaccess2 conObj, JsonNode obj) throws DBoperationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayNode get(DBaccess2 conObj, JsonNode obj) throws DBoperationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long getCount(DBaccess2 conObj, JsonNode filter) throws DBoperationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayNode getAll(DBaccess2 conObj, JsonNode filter) throws DBoperationException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ArrayNode arrayNode = objectMapper.createArrayNode();
            int all = filter.get("all").asInt();
            int location = filter.get("locationId").asInt();
            this.createDBcon(conObj);
            String qry = "";
            switch (all) {
                default:
                    qry = "SELECT ub.userId,ub.`type`,ub.`index`,u.name AS 'userName',ub.data,u.fp1,u.fp2 FROM locationusers lu LEFT JOIN userbiometrics ub ON ub.userId=lu.userId LEFT JOIN user u ON u.id=ub.userId WHERE lu.locationId="+location+" AND ub.userId IS NOT NULL" ;
                    break;
            }
            if (DBcon.dqlQuery(qry)) {
                DataTable dt = DBcon.getResultSet();
                while (dt.next()) {
                    UserBiometric userBiometric = new UserBiometric( dt.getString("userId"),dt.getString("userName"),dt.getInt("type"), dt.getInt("index"),(byte[])dt.getObject("data"),dt.getInt("fp1"),dt.getInt("fp2"));
                    arrayNode.add(userBiometric.toJson());
                }
                return arrayNode;
            } else {
                throw new DBoperationException(GET_ALL, "Query Failed");
            }
        } catch (Exception ex) {
            throw new DBoperationException(GET_ALL, ex);
        }
    }

    @Override
    public void getAllNew(DBaccess2 conObj, JsonNode filter) {

    }

    @Override
    public ArrayNode export(DBaccess2 conObj, JsonNode filter) throws DBoperationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

    }

    @Override
    public JsonArray importCSV(DBaccess2 conObj, JsonArray arr) throws DBoperationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

    }


    @Override
    public void fromJson(JsonNode json) {
        JsonNode je;
        je = json.get("userId");
        if (je != null) {
            this.userId = je.asText();
        }
        je=json.get("type");
        if(je!=null){
        this.type=je.asInt();
        }else{
            this.type=1;
        }
        je=json.get("index");
        if(je!=null){
        this.index=je.asInt();
        }else{
            this.index=1;
        }
        je=json.get("data");
        if(je!=null)
        {
            this.data=Helper.hexStringToByteArray(je.asText());
        }else{this.data=Helper.hexStringToByteArray(Integer.toString(0));}
    }
    @Override
    public JsonNode toJson() {
        return null;
        /*JsonObject obj = super.toJson();
        obj.add("userId", new JsonPrimitive(this.userId));
        if(this.userName!=null)
        {
            obj.add("userName", new JsonPrimitive(this.userName));
        }else{obj.add("userName", new JsonPrimitive(""));}
        obj.add("type", new JsonPrimitive(this.type));
        obj.add("index", new JsonPrimitive(this.index));
        obj.add("quality", new JsonPrimitive(this.quality));
        if(this.data!=null && this.data.length>0){
            obj.add("data",new JsonPrimitive(Helper.byteArrayToHexString(this.data)));
        }else{
            obj.add("data",new JsonPrimitive(0));
        }
        obj.add("fp1", new JsonPrimitive(this.fp1));
        
        obj.add("fp2", new JsonPrimitive(this.fp2));
        return obj;*/
    }

    /*public static void moduleErase(DBaccess2 DBcon,long sessionId) throws SIPLlibException
    {
        if(DBcon==null)
           DBcon= DBconnection.newInstance();
        DBcon.dmlQuery("update cabinetuserbiometricsync set command=0");//cancel all pending commands
        DBcon.dmlQuery("update cabinetsync cu set command="+SyncEntity.ERASE_MODULE_DB+",changedAt=now(),syncStatus=0,retry=3,sessionId="+sessionId+" where type="+SyncEntity.FINGERS);
    }*/
    
    @Override
    public String getIdentifier()
    {
        return this.userId+" "+FP_NAMES[this.index];
    }
}
