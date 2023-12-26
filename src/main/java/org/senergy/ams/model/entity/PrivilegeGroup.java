/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.senergy.ams.model.entity;

import SIPLlib.DBaccess2;
import SIPLlib.DataTable;
import SIPLlib.Helper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.gson.JsonArray;
import SIPLlib.SIPLlibException;
import org.senergy.ams.model.DBconnection;
import org.senergy.ams.model.DBentity;
import org.senergy.ams.model.DBoperationException;

import java.math.BigInteger;

/**
 *
 * @author admin
 */
public class PrivilegeGroup extends DBentity {
    public final static String ENTITY_NANE="PrivilegeGroup";
    public final static int PRIVILEGE_BYTE=2;
    int locationId;

    public PrivilegeGroup()
    {

        super(ENTITY_NANE,0);
    }

    public int id;
    public String name;
    public byte[] operationPrivilege;
    protected BigInteger[] menuPrivilege;

    public PrivilegeGroup(int id,String name,int disabled)
    {
        super(ENTITY_NANE,disabled);
        this.id=id;
        this.name=name;
    }
    public PrivilegeGroup(int id,String name)
    {
        super(ENTITY_NANE,0);
        this.id=id;
        this.name=name;
    }
    public PrivilegeGroup(int id,String name,byte[] operationPrivilege,byte[] menuPrivilege,int disabled)
    {
        super(ENTITY_NANE,disabled);
        this.id=id;
        this.name=name;
        this.setOperationPrivilegeBytes(operationPrivilege);
        this.setMenuPrivilegeBytes(menuPrivilege);
    }

    public byte[] getOperationPrivilegeBytes() {
        return encryptPrivilege(this.operationPrivilege);
    }
    public byte[] getMenuPrivilegeBytes() {
        byte[] data = null;
        if (this.menuPrivilege != null) {
            data = new byte[0];
            for (int i = 0; i < this.menuPrivilege.length; i++) {
                byte[] mp = new byte[]{0, 0, 0, 0};
                byte[] dataTemp1 = this.menuPrivilege[i].toByteArray();
                if (dataTemp1.length > 4) {
                    System.arraycopy(dataTemp1, dataTemp1.length - 4, mp, 0, 4);
                } else {
                    System.arraycopy(dataTemp1, 0, mp, (4 - dataTemp1.length), dataTemp1.length);
                }
                byte[] dataTemp2 = new byte[data.length];
                System.arraycopy(data, 0, dataTemp2, 0, data.length);
                data = new byte[data.length + 4];
                System.arraycopy(dataTemp2, 0, data, 0, dataTemp2.length);
                System.arraycopy(mp, 0, data, dataTemp2.length, 4);
            }
            return encryptPrivilege(data);
        }
        return data;
    }
    public void setOperationPrivilegeBytes(byte[] data) {
        data = decryptPrivilege(data);
        if (data != null) {
            this.operationPrivilege = data;
        } else {
            this.operationPrivilege = new byte[0];
        }
    }
    public void setMenuPrivilegeBytes(byte[] data) {
        data = decryptPrivilege(data);
        if (data != null) {
            this.menuPrivilege = new BigInteger[data.length / 4];
            for (int j = 0; j < data.length / 4; j++) {
                this.menuPrivilege[j] = Helper.getBigInteger(data, j * 4, 4);
            }
        } else {
            this.menuPrivilege = new BigInteger[0];
        }
    }
    private byte[] encryptPrivilege(byte[] data) {
        if (data != null) {
            byte[] edata = new byte[data.length + 2];
            System.arraycopy(data, 0, edata, 0, data.length);
            byte[] crc = Helper.getCRC(data, 0, data.length);
            if ((crc != null) && crc.length == 2) {
                System.arraycopy(crc, 0, edata, data.length, 2);
                return edata;
            }
        }
        return null;
    }
    private byte[] decryptPrivilege(byte[] data) {
        if (Helper.checkCRC(data, 0, data.length)) {
            byte[] edata = new byte[data.length - 2];
            System.arraycopy(data, 0, edata, 0, edata.length);
            return edata;
        }
        return null;
    }

    @Override
    public boolean add(DBaccess2 conObj) throws DBoperationException {
        DBaccess2 DBcon = DBconnection.newInstance();
        try{
            return DBcon.preparedQuery("insert into privilegeGroup(id,name,menuPrivilege,operationPrivilege,reportPrivilege) values(?,?,?,?,?)", this.id,this.name, this.getMenuPrivilegeBytes(), this.getOperationPrivilegeBytes(), "");
        }
        catch(Exception e){
            throw new RuntimeException("");
        }
    }

    @Override
    public boolean update(DBaccess2 conObj) throws DBoperationException {
        DBaccess2 DBcon = DBconnection.newInstance();
        try{
            if (DBcon.beginTransaction()) {
                byte[] menuPrivilege = this.getMenuPrivilegeBytes();
                byte[] operationPrivilege = this.getOperationPrivilegeBytes();
                boolean retVal = false;
                try {
                    retVal = DBcon.preparedQuery("update privilegeGroup set name=?,menuPrivilege=?,operationPrivilege=? where id=?", this.name,menuPrivilege, operationPrivilege, this.id);
                }
                finally {
                    DBcon.endTransaction(retVal);
                    return true;
                }
            }
        }
        catch(Exception e){
            throw new RuntimeException();
        }
        return false;
    }

    @Override
    public boolean updateParam(String id, String paramName, String newVal, JsonNode obj) throws DBoperationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean permanentDelete(DBaccess2 conObj) throws DBoperationException {
        try{
            boolean retVal = DBcon.dmlQuery("delete from privilegegroup where id="+this.id );
            return retVal;
        }
        catch(SIPLlibException ex)
        {
            throw new DBoperationException(PERMANENT_DELETE, ex);
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

        /*try{
//            this.locationId=obj.get("locationId").getAsInt();
            String idList=obj.get("idList").getAsJsonArray().toString().replace('[', ' ').replace(']', ' ');
            this.createDBcon(conObj);
            return DBcon.dmlQuery("update privilegegroup set disabled=1 where id in ("+idList+") ");
        }
        catch(Exception ex)
        {
            throw new DBoperationException(TEMPORARY_DELETE, ex);
        }*/
    }

    @Override
    public boolean restore(DBaccess2 conObj) throws DBoperationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean restore(DBaccess2 conObj,JsonNode obj) throws DBoperationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

        /*try{
//            this.locationId=obj.get("locationId").getAsInt();
            String idList=obj.get("idList").getAsJsonArray().toString().replace('[', ' ').replace(']', ' ');
            this.createDBcon(conObj);
            return DBcon.dmlQuery("update privilegegroup set disabled=0 where id in ("+idList+")");
        }
        catch(Exception ex)
        {
            throw new DBoperationException(RESTORE, ex);
        }*/
    }

    @Override
    public ArrayNode get(DBaccess2 conObj, JsonNode obj) throws DBoperationException {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = objectMapper.createArrayNode();
        this.id=obj.get("id").asInt();
        DBaccess2 DBcon = DBconnection.newInstance();
        try{
            if (DBcon.preparedQuery("select * from privilegeGroup where id=?", this.id)) {
                DataTable dt = DBcon.getResultSet();
                if (dt.next()) {
                    PrivilegeGroup op = new PrivilegeGroup(dt.getInt("id"), dt.getString("name"),(byte[]) dt.getObject("operationPrivilege"),(byte[]) dt.getObject("menuPrivilege"),  dt.getInt("disabled"));
                    arrayNode.add(op.toJson());
                    return arrayNode;
                }else
                {
                    throw  new DBoperationException(GET, "Failed");
                }
            }else
            {
                throw  new DBoperationException(GET, "Query Failed");
            }
        }
        catch(Exception e){
            throw new DBoperationException(GET, e);
        }
    }

    @Override
    public long getCount(DBaccess2 conObj, JsonNode filter) throws DBoperationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public ArrayNode getAll(DBaccess2 conObj, JsonNode filter) throws DBoperationException {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = objectMapper.createArrayNode();
        this.createDBcon(conObj);
        try{
            int all=filter.get("all").asInt();
            int userType=filter.get("type").asInt();
            this.createDBcon(conObj);
            String qry="";
            switch(all)
            {
                case DBentity.GET_ALL_ENABLED:
                    qry="select * from privilegegroup pg where pg.id not in (0,1) and disabled=0";
                    break;
                case DBentity.GET_ALL_DISABLED:
                    qry="select * from privilegegroup pg where pg.id not in (0,1) and disabled=1";
                    break;
                default:
                    if(userType==0)//SuperAdmin
                    {
                        qry="select * from privilegegroup ";

                    }else{

                        qry="select * from privilegegroup pg where pg.id not in (0,1)";
                    }
                    break;
            }
//            this.DBcon.dqlQuery("select * from privilegegroup where id!=0");

            if(this.DBcon.dqlQuery(qry))
            {
                DataTable dt=DBcon.getResultSet();
                while(dt.next())
                {
                    PrivilegeGroup privilegeGroup=new PrivilegeGroup(dt.getInt("id"),dt.getString("name"),dt.getInt("disabled"));
                    arrayNode.add(privilegeGroup.toJson());
                }
                return arrayNode;
            }
            else
            {
                throw  new DBoperationException(GET_ALL, "Query Failed");
            }
        }
        catch(SIPLlibException e)
        {
            throw new DBoperationException(GET_ALL, e);
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
    public JsonNode toJson() {
        return null;
        /*JsonObject obj = super.toJson();

        obj.add("id", new JsonPrimitive(this.id));
        if (this.name != null) {
            obj.add("name", new JsonPrimitive(this.name));
        } else {
            obj.add("name", new JsonPrimitive(""));
        }
        if (this.menuPrivilege != null) {
            JsonArray menuPrivilegeArray = new JsonArray();
            for (int i = 0; i < this.menuPrivilege.length; i++) {
                menuPrivilegeArray.add(new JsonPrimitive(this.menuPrivilege[i].toString(16)));
                obj.add("menuPrivilege", menuPrivilegeArray);
            }
        } else {
            obj.add("menuPrivilege", new JsonArray());
        }
        if (this.operationPrivilege != null && this.operationPrivilege.length != 0) {
            JsonArray operationPrivilegeArray = new JsonArray();
            for (int i = 0; i < this.operationPrivilege.length; i++) {
                operationPrivilegeArray.add(new JsonPrimitive(this.operationPrivilege[i]));
                obj.add("operationPrivilege", operationPrivilegeArray);
            }
        } else {
            obj.add("operationPrivilege", new JsonArray());
        }
        return obj;*/
    }

    @Override
    public void fromJson(JsonNode json) {

        JsonNode je;
        je = json.get("id");
        if (je != null) {
            this.id = je.asInt();
        }
        je = json.get("name");
        if (je != null) {
            this.name = je.asText();
        }
        je = json.get("menuPrivilege");
//        if (je != null) {
//            JsonArray ja = je.getAsJsonArray();
//            this.menuPrivilege = new BigInteger[ja.size()];
//            for (int i = 0; i < ja.size(); i++) {
//                this.menuPrivilege[i] = BigInteger.valueOf(Long.parseLong(ja.get(i).getAsString(), 16));
//            }
//        }
//        je = json.get("operationPrivilege");
//        if (je != null) {
//            JsonArray ja = je.getAsJsonArray();
//            this.operationPrivilege = new byte[ja.size()];
//            for (int i = 0; i < ja.size(); i++) {
//                this.operationPrivilege[i] = ja.get(i).getAsByte();
//            }
//        }
    }
    @Override
    public String getIdentifier()
    {
        return this.id+" "+this.name;
    }
}
