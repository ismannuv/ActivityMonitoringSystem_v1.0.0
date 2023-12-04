package org.senergy.ams.model.entity;

import SIPLlib.DBaccess2;
import SIPLlib.DataTable;
import SIPLlib.Helper;
import SIPLlib.SIPLlibException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.senergy.ams.model.DBentity;
import org.senergy.ams.model.DBoperationException;

import java.math.BigInteger;
import java.security.MessageDigest;

public class User extends DBentity {
    public final static String ENTITY_NANE="User";
//    public static final int DUMMY_USER=0,SUPER_ADMIN = 1,DB_ADMIN=2,ADMIN=3,SUPERVISOR=4,ATTENDANT=5;
    public String id;
    private String password;
    protected String name;
    private String emailId;
    public String mobileNo;
    public PrivilegeGroup privilegeGroup;
    public BigInteger cardUID;
    public String pin;
    public String validFrom,validUpto;
    public int fp1,fp2;
    private String enrolledFingers;


    public enum Operation_enum {

        UNKNOWN(0), LOGIN(1), LOGOUT(2), EDIT_PROFILE(3), CHANGE_PASSWORD(4),GET_SESSION(5),SET_SESSION(6),RESET_PASSWORD_ACCEPT(7),REQUEST_RESET_PASSWORD(8),SWITCH_LOCATION(9),RESET_PASSWORD_REJECT(10),VERIFY_PASSWORD(11), GET_RESET_PASS_REQUEST(12),GET_DB_STATISTICS_DATA(13),GET_NOTIFICATIONS(14),DISMISS_NOTIFICATION(15),PROCESS_STATUS(16),MAKE_AUDIT_LOG(17),SET_IDLE(18),GET_LICENCE_KEY(19),VERIFY_AD_PASSWORD(20);
        private int value;

        private Operation_enum(int val) {
            this.value = val;
        }

        public static User.Operation_enum get(int val) {
            switch (val) {
                case 1:
                    return LOGIN;
                case 2:
                    return LOGOUT;
                case 3:
                    return EDIT_PROFILE;
                case 4:
                    return CHANGE_PASSWORD;
                case 5:
                    return GET_SESSION;
                case 6:
                    return SET_SESSION;
                case 7:
                    return RESET_PASSWORD_ACCEPT;
                case 8:
                    return REQUEST_RESET_PASSWORD;
                case 9:
                    return SWITCH_LOCATION;
                case 10:
                    return RESET_PASSWORD_REJECT;
                case 11:
                    return VERIFY_PASSWORD;
                case 12:
                    return GET_RESET_PASS_REQUEST;
                case 13:
                    return GET_DB_STATISTICS_DATA;
                case 14:
                    return GET_NOTIFICATIONS;
                case 15:
                    return DISMISS_NOTIFICATION;
                case 16:
                    return PROCESS_STATUS;
                case 17:
                    return MAKE_AUDIT_LOG;
                case 18:
                    return SET_IDLE;
                case 19:
                    return GET_LICENCE_KEY;
                case 20:
                    return VERIFY_AD_PASSWORD;
                default:
                    return UNKNOWN;
            }
        }

        public int getValue() {
            return this.value;
        }
    }
    public User()
    {
        super(ENTITY_NANE,0);
    }
    public User(String id) {

        super(ENTITY_NANE,0);
        this.id = id;
    }
    public User(String id,String name,BigInteger cardUID,String mobileNo,String emailId, int disabled){
        super(ENTITY_NANE,disabled);
        this.id=id;
        this.name=name;
        this.cardUID = cardUID;
        this.mobileNo=mobileNo;
        this.emailId=emailId;
        this.disabled=disabled;
    }
    public boolean login() throws DBoperationException {
        try{
            this.createDBcon(null);
            if(DBcon.dqlQuery("SELECT u.*,p.id AS 'privilegeGroupId' from user u left join privilegegroup p on u.privilegeGroupId=p.id WHERE u.password='"+generateMD5(this.password)+"'"))
            {
                DataTable dt=DBcon.getResultSet();
                if(dt.next())
                {
                    this.id=dt.getString("id");
                    this.name=dt.getString("name");
                    this.cardUID=dt.getBigInteger("cardUid");
                    this.mobileNo=dt.getString("mobileNo");
                    this.emailId=dt.getString("email");
                    this.disabled=dt.getInt("disabled");
                    JsonObject jsonObject =new JsonObject();
                    jsonObject.add("id",new JsonPrimitive(dt.getInt("privilegeGroupId")));

                    this.privilegeGroup= (PrivilegeGroup) new PrivilegeGroup().get(DBcon,jsonObject);


                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                throw  new DBoperationException(GET, "Query Failed");
            }
        }
        catch(Exception ex)
        {
            throw new DBoperationException(GET, ex);
        }
    }

    @Override
    public String getIdentifier() {
        return null;
    }
    public boolean addDBentity(DBentity entity) throws DBoperationException {
        entity.sessionId=this.sessionId;
        if (entity.add(null)) {
//            AuditTrail.operatorLog(this, entity, DBentity.ADD,"");
            return true;
        } else {
            throw new DBoperationException(ADD,"");
        }
    }
    public boolean updateDBentity(DBentity entity) throws DBoperationException {
        entity.sessionId=this.sessionId;
        if (entity.update(null)) {
//            AuditTrail.operatorLog(this, entity, DBentity.UPDATE,"");
            return true;
        } else {
            throw new DBoperationException(UPDATE,"");
        }
    }
    public static DBentity[] getAllDBentity(DBentity entity, JsonObject filter) throws DBoperationException {
        return entity.getAll(null,filter);
    }

    public static DBentity getDBentity(DBentity entity,JsonObject obj)throws DBoperationException  {
        return entity.get(null,obj);
    }
    public boolean temporaryDeleteDBentity(DBentity entity, JsonArray idList) throws DBoperationException {
        entity.sessionId=this.sessionId;
        JsonObject obj=new JsonObject();
//        obj.add("locationId",new JsonPrimitive(this.loaction[this.selectedLocation].id));
        obj.add("idList",idList);
        if (entity.temporarydelete(null,obj)) {
//            AuditTrail.operatorLog(this, entity,TEMPORARY_DELETE, idList.toString());
            return true;
        } else {
            throw new DBoperationException(TEMPORARY_DELETE,"");
        }
    }

    public boolean restoreDBentity(DBentity entity, JsonArray idList) throws DBoperationException {
        entity.sessionId=this.sessionId;
        JsonObject obj=new JsonObject();
//        obj.add("locationId",new JsonPrimitive(this.loaction[this.selectedLocation].id));
        obj.add("idList",idList);
        if (entity.restore(null,obj)) {
//            AuditTrail.operatorLog(this, entity, RESTORE, idList.toString());
            return true;
        } else {
            throw new DBoperationException(RESTORE,"");
        }
    }

    @Override
    public boolean add(DBaccess2 conObj) throws DBoperationException {
        try{
            this.createDBcon(conObj);

            if(this.cardUID!=null && DBcon.dqlQuery("select u.id,u.name from user u where u.cardUid="+this.cardUID))
            {
                DataTable dt=DBcon.getResultSet();
                if(dt.next())
                {
                    throw new DBoperationException(ADD,"Card is already assigned to "+dt.getString("id")+" : "+dt.getString("name"));
                }
            }

            if(DBcon.preparedQuery("insert into user (id,cardUid,name,emailId,mobileNo,pin,fp1,fp2) values(?,?,?,?,?,?,?,?,?)",this.id,this.cardUID,this.name,this.emailId,this.mobileNo,this.pin,this.fp1,this.fp2))
            {
                return true;
            }
            else
            {
                throw new DBoperationException(ADD, "Failed to insert into user table");
            }
        }
        catch(SIPLlibException ex)
        {
            throw new DBoperationException(ADD, ex);
        }
    }

    @Override
    public boolean update(DBaccess2 conObj) throws DBoperationException {
        return false;
    }

    @Override
    public boolean updateParam(String id, String paramName, String newVal, JsonObject obj) throws DBoperationException {
        return false;
    }

    @Override
    public boolean permanentDelete(DBaccess2 conObj) throws DBoperationException {
        return false;
    }

    @Override
    public JsonArray permanentDelete(DBaccess2 conObj, JsonObject obj) throws DBoperationException {
        return null;
    }

    @Override
    public boolean temporarydelete(DBaccess2 conObj) throws DBoperationException {
        return false;
    }

    @Override
    public boolean temporarydelete(DBaccess2 conObj, JsonObject obj) throws DBoperationException {
        return false;
    }

    @Override
    public boolean restore(DBaccess2 conObj) throws DBoperationException {
        return false;
    }

    @Override
    public boolean restore(DBaccess2 conObj, JsonObject obj) throws DBoperationException {
        return false;
    }

    @Override
    public DBentity get(DBaccess2 conObj, JsonObject obj) throws DBoperationException {
        try{
            this.id=obj.get("id").getAsString();
            this.createDBcon(conObj);
            if(DBcon.dqlQuery("SELECT u.*,p.id AS 'privilegeGroupId' from user u left join privilegegroup p on u.privilegeGroupId=p.id WHERE u.id='"+this.id+"'"))
            {
                DataTable dt=DBcon.getResultSet();
                if(dt.next())
                {
                    User user= new User(dt.getString("id"),dt.getString("name"),dt.getBigInteger("cardUid"),dt.getString("mobileNo"),dt.getString("email"),dt.getInt("disabled"));
                    user.pin= dt.getString("pin");
                    user.emailId= dt.getString("emailId");
                    user.validFrom=dt.getString("validFrom");
                    user.validUpto=dt.getString("validUpto");
                    JsonObject jsonObject =new JsonObject();
                    jsonObject.add("id",new JsonPrimitive(dt.getInt("privilegeGroupId")));

                    user.privilegeGroup= (PrivilegeGroup) new PrivilegeGroup().get(DBcon,jsonObject);


                    return user;
                }
                else
                {
                    throw  new DBoperationException(GET, "Entity Not Found");
                }
            }
            else
            {
                throw  new DBoperationException(GET, "Query Failed");
            }
        }
        catch(Exception ex)
        {
            throw new DBoperationException(GET, ex);
        }
    }

    @Override
    public long getCount(DBaccess2 conObj, JsonObject filter) throws DBoperationException {
        return 0;
    }

    @Override
    public DBentity[] getAll(DBaccess2 conObj, JsonObject filter) throws DBoperationException {
        try{
            int all=filter.get("all").getAsInt();
            this.createDBcon(conObj);
            String qry="";
            switch(all)
            {

                case DBentity.GET_ALL_ENABLED:
                    qry="SELECT * FROM user u WHERE u.disabled!=0";
                    break;
                default:
                    qry="SELECT * FROM user";
                    break;
            }
            if(DBcon.dqlQuery(qry))
            {
                DataTable dt=DBcon.getResultSet();
                User[] entity=new User[dt.getRowCount()];
                int i=0;
                while(dt.next())
                {
                    entity[i]=new User(dt.getString("id"),dt.getString("name"),dt.getBigInteger("cardUid"),dt.getString("mobileNo"),dt.getString("email"),dt.getInt("disabled"));

                    i++;
                }
                return entity;
            }
            else
            {
                throw  new DBoperationException(GET_ALL, "Query Failed");
            }
        }
        catch(Exception ex)
        {
            throw new DBoperationException(GET_ALL, ex);
        }
    }

    @Override
    public DBentity[] export(DBaccess2 conObj, JsonObject filter) throws DBoperationException {
        return new DBentity[0];
    }

    @Override
    public JsonArray importCSV(DBaccess2 conObj, JsonArray arr) throws DBoperationException {
        return null;
    }

    private void getEnrolledFingers(DBaccess2 DBcon) throws SIPLlibException {
        this.enrolledFingers="";
        this.createDBcon(DBcon);
        if(this.DBcon.dqlQuery("select GROUP_CONCAT(ub.`index`) as 'fingers' from userbiometrics ub where ub.`type`=1 and ub.userId='"+this.id+"'"))
        {
            DataTable dt=DBcon.getResultSet();
            if(dt.next())
            {
                this.enrolledFingers=dt.getString("fingers");
            }
        }
    }
    @Override
    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        if (this.id != null) {
            obj.add("id", new JsonPrimitive(this.id));
        } else {
            obj.add("id", new JsonPrimitive(""));
        }

        if (this.name != null) {
            obj.add("name", new JsonPrimitive(this.name));
        } else {
            obj.add("name", new JsonPrimitive(""));
        }
        if (this.emailId != null) {
            obj.add("emailId", new JsonPrimitive(this.emailId));
        } else {
            obj.add("emailId", new JsonPrimitive(""));
        }
        if (this.mobileNo != null) {
            obj.add("mobileNo", new JsonPrimitive(this.mobileNo));
        } else {
            obj.add("mobileNo", new JsonPrimitive(""));
        }
        if (this.privilegeGroup != null) {
            obj.add("privilegeGroup", this.privilegeGroup.toJson());
        } else {
            obj.add("privilegeGroup", new JsonObject());
        }
        if (this.cardUID != null) {
            obj.add("cardUID", new JsonPrimitive(Helper.byteArrayToHexString(this.cardUID.toByteArray())));
        } else {
            obj.add("cardUID", new JsonPrimitive(""));
        }
        if (this.pin != null) {
            obj.add("pin", new JsonPrimitive(this.pin));
        } else {
            obj.add("pin", new JsonPrimitive(""));
        }

        obj.add("fp1", new JsonPrimitive(this.fp1));

        obj.add("fp2", new JsonPrimitive(this.fp2));

        if (this.validFrom != null) {
            obj.add("validFrom", new JsonPrimitive(this.validFrom));
        } else {
            obj.add("validFrom", new JsonPrimitive(""));
        }
        if (this.validUpto != null) {
            obj.add("validUpto", new JsonPrimitive(this.validUpto));
        } else {
            obj.add("validUpto", new JsonPrimitive(""));
        }
        if(this.enrolledFingers != null){
            obj.add("enrolledFingers", new JsonPrimitive(this.enrolledFingers));
        }else {
            obj.add("enrolledFingers", new JsonPrimitive(""));
        }
        return obj;
    }

    @Override
    public void fromJson(JsonObject json) {
        JsonElement je;
        je = json.get("id");
        if (je != null) {
            this.id = je.getAsString();
        }
        je = json.get("name");
        if (je != null) {
            this.name = je.getAsString();
        }
        je = json.get("emailId");
        if (je != null) {
            this.emailId = je.getAsString().equals("") ? null :je.getAsString();
        }

        je = json.get("password");
        if (je != null) {
            this.password = je.getAsString();
        }

    }
    public String generateMD5(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data.getBytes());

            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }
    public DBentity getUserFromPassword(DBaccess2 conObj,String password) throws DBoperationException {
        try{
            this.createDBcon(conObj);
            if(DBcon.dqlQuery("SELECT u.*,p.id AS 'privilegeGroupId' from user u left join privilegegroup p on u.privilegeGroupId=p.id WHERE u.password='"+generateMD5(password)+"'"))
            {
                DataTable dt=DBcon.getResultSet();
                if(dt.next())
                {
                    User user= new User(dt.getString("id"),dt.getString("name"),dt.getBigInteger("cardUid"),dt.getString("mobileNo"),dt.getString("email"),dt.getInt("disabled"));
                    JsonObject jsonObject =new JsonObject();
                    jsonObject.add("id",new JsonPrimitive(dt.getInt("privilegeGroupId")));

                    user.privilegeGroup= (PrivilegeGroup) new PrivilegeGroup().get(DBcon,jsonObject);


                    return user;
                }
                else
                {
                    throw  new DBoperationException(GET, "Entity Not Found");
                }
            }
            else
            {
                throw  new DBoperationException(GET, "Query Failed");
            }
        }
        catch(Exception ex)
        {
            throw new DBoperationException(GET, ex);
        }
    }
}
