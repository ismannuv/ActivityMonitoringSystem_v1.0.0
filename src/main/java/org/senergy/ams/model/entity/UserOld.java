package org.senergy.ams.model.entity;

import SIPLlib.DBaccess2;
import SIPLlib.DataTable;
import SIPLlib.Helper;
import SIPLlib.SIPLlibException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.senergy.ams.app.AMS;
import org.senergy.ams.model.Config;
import org.senergy.ams.model.DBentity;
import org.senergy.ams.model.DBoperationException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class UserOld extends DBentity {
    public final static String ENTITY_NANE = "User";
    //    public static final int DUMMY_USER=0,SUPER_ADMIN = 1,DB_ADMIN=2,SUPERVISOR=3,ATTENDANT=4;
    public String id;
    protected String name;
    private String password;
    public BigInteger cardUID;
    private String emailId;
    public String mobileNo;
    public PrivilegeGroup privilegeGroup;
    public String pin;
    public String validFrom, validUpto;
    public int fp1, fp2;
    private int authType = 1;
    public ObjectNode uObjectNode;


// '1'-'Pin + Card','2'-'Card + Finger','3'-'Finger + Pin'


    public enum Operation_enum {

        UNKNOWN(0), LOGIN(1), LOGOUT(2), EDIT_PROFILE(3), CHANGE_PASSWORD(4), GET_SESSION(5), SET_SESSION(6), RESET_PASSWORD_ACCEPT(7), REQUEST_RESET_PASSWORD(8), SWITCH_LOCATION(9), RESET_PASSWORD_REJECT(10), VERIFY_PASSWORD(11), GET_RESET_PASS_REQUEST(12), GET_DB_STATISTICS_DATA(13), GET_NOTIFICATIONS(14), DISMISS_NOTIFICATION(15), PROCESS_STATUS(16), MAKE_AUDIT_LOG(17), SET_IDLE(18), GET_LICENCE_KEY(19), VERIFY_AD_PASSWORD(20);
        private int value;

        private Operation_enum(int val) {
            this.value = val;
        }

        public static UserOld.Operation_enum get(int val) {
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

    public UserOld() {
        super(ENTITY_NANE, 0);
    }

    public UserOld(String id) {

        super(ENTITY_NANE, 0);
        this.id = id;
    }

    public UserOld(String id, String name, BigInteger cardUID, String mobileNo, String emailId, int disabled) {
        super(ENTITY_NANE, disabled);
        this.id = id;
        this.name = name;
        this.cardUID = cardUID;
        this.mobileNo = mobileNo;
        this.emailId = emailId;
        this.disabled = disabled;
    }

    public boolean login() throws DBoperationException {
        try {
            this.createDBcon(null);
            if (DBcon.dqlQuery("SELECT u.*,p.id AS 'privilegeGroupId' from user u left join privilegegroup p on u.privilegeGroupId=p.id WHERE u.id='" + this.id + "' and  u.password='" + generateMD5(this.password) + "'")) {
                DataTable dt = DBcon.getResultSet();
                if (dt.next()) {
                    this.id = dt.getString("id");
                    this.name = dt.getString("name");
                    this.cardUID = dt.getBigInteger("cardUid");
                    this.mobileNo = dt.getString("mobileNo");
                    this.emailId = dt.getString("emailId");
                    this.disabled = dt.getInt("disabled");

                    ObjectMapper om = new ObjectMapper();
                    ObjectNode objectNode = om.createObjectNode();
                    objectNode.put("id",dt.getInt("privilegeGroupId"));

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.add("id", new JsonPrimitive(dt.getInt("privilegeGroupId")));

                    this.privilegeGroup = om.treeToValue(new PrivilegeGroup().get(DBcon, objectNode).get(0), PrivilegeGroup.class);

                    return true;
                } else {
                    return false;
                }
            } else {
                throw new DBoperationException(GET, "Query Failed");
            }
        } catch (Exception ex) {
            throw new DBoperationException(GET, ex);
        }
    }

    @Override
    public String getIdentifier() {
        return null;
    }

    public boolean addDBentity(DBentity entity) throws DBoperationException {
        entity.sessionId = this.sessionId;
        if (entity.add(null)) {
//            AuditTrail.operatorLog(this, entity, DBentity.ADD,"");
            return true;
        } else {
            throw new DBoperationException(ADD, "");
        }
    }

    public boolean updateDBentity(DBentity entity) throws DBoperationException {
        entity.sessionId = this.sessionId;
        if (entity.update(null)) {
//            AuditTrail.operatorLog(this, entity, DBentity.UPDATE,"");
            return true;
        } else {
            throw new DBoperationException(UPDATE, "");
        }
    }

    public static ArrayNode getAllDBentity(DBentity entity, JsonNode filter) throws DBoperationException {
        return entity.getAll(null, filter);
    }
    public static void getAllDBentityNew(DBentity entity, JsonNode filter) {
        entity.getAllNew(null, filter);
    }
    public static ArrayNode getDBentity(DBentity entity, JsonNode obj) throws DBoperationException {
        return entity.get(null, obj);
    }

    public boolean temporaryDeleteDBentity(DBentity entity, ArrayNode idList) throws DBoperationException {
        entity.sessionId = this.sessionId;
        ObjectNode obj = new ObjectMapper().createObjectNode();
//        obj.add("locationId",new JsonPrimitive(this.loaction[this.selectedLocation].id));
        obj.set("idList", idList);
        if (entity.temporarydelete(null, obj)) {
//            AuditTrail.operatorLog(this, entity,TEMPORARY_DELETE, idList.toString());
            return true;
        } else {
            throw new DBoperationException(TEMPORARY_DELETE, "");
        }
    }

    public boolean restoreDBentity(DBentity entity, ArrayNode idList) throws DBoperationException {
        entity.sessionId = this.sessionId;
        ObjectNode obj = new ObjectMapper().createObjectNode();

//        obj.add("locationId",new JsonPrimitive(this.loaction[this.selectedLocation].id));
        obj.set("idList", idList);
        if (entity.restore(null, obj)) {
//            AuditTrail.operatorLog(this, entity, RESTORE, idList.toString());
            return true;
        } else {
            throw new DBoperationException(RESTORE, "");
        }
    }

    @Override
    public boolean add(DBaccess2 conObj) throws DBoperationException {
        try {
            this.createDBcon(conObj);
            if (this.cardUID != null && DBcon.dqlQuery("select u.id,u.name from user u where u.cardUid=" + this.cardUID)) {
                DataTable dt = DBcon.getResultSet();
                if (dt.next()) {
                    throw new DBoperationException(ADD, "Card is already assigned to " + dt.getString("id") + " : " + dt.getString("name"));
                }
            }

            this.password = generateMD5("senergy" + this.id);
            if (DBcon.preparedQuery("insert into user (id, name, cardUid, emailId, mobileNo, validFrom, validUpto, pin, password, privilegeGroupId, fp1, fp2, authType) values(?,?,?,?,?,?,?,?,?,?,?,?,?)", this.id, this.name, this.cardUID, this.emailId, this.mobileNo, this.validFrom, this.validUpto, this.pin, this.password, this.privilegeGroup.id, this.fp1, this.fp2, this.authType)) {
                return true;
            } else {
                throw new DBoperationException(ADD, "Failed to insert into user table");
            }
        } catch (SIPLlibException ex) {
            throw new DBoperationException(ADD, ex);
        }
    }

    @Override
    public boolean update(DBaccess2 conObj) throws DBoperationException {
        try {
            this.createDBcon(conObj);

            boolean updateLocUser = false;
            if (DBcon.preparedQuery("update user set name=?, cardUid=?, emailId=?, mobileNo=?, validFrom=?, validUpto=?, pin=?, privilegeGroupId=?, fp1=?, fp2=?, authType=? where id=?", this.name, this.cardUID, this.emailId, this.mobileNo, this.validFrom, this.validUpto, this.pin, this.privilegeGroup.id, this.fp1, this.fp2, this.authType, this.id)) {


                return true;
            } else {
                throw new DBoperationException(UPDATE, "Failed to update user table");
            }
        } catch (SIPLlibException ex) {
            throw new DBoperationException(UPDATE, ex);
        }
    }

    @Override
    public boolean updateParam(String id, String paramName, String newVal, JsonNode obj) throws DBoperationException {
        return false;
    }

    @Override
    public boolean permanentDelete(DBaccess2 conObj) throws DBoperationException {
        return false;
    }

    @Override
    public JsonArray permanentDelete(DBaccess2 conObj, JsonNode obj) throws DBoperationException {
        return null;
    }

    @Override
    public boolean temporarydelete(DBaccess2 conObj) throws DBoperationException {
        return false;
    }

    @Override
    public boolean temporarydelete(DBaccess2 conObj, JsonNode obj) throws DBoperationException {
        return false;
    }

    @Override
    public boolean restore(DBaccess2 conObj) throws DBoperationException {
        return false;
    }

    @Override
    public boolean restore(DBaccess2 conObj, JsonNode obj) throws DBoperationException {
        return false;
    }

    @Override
    public ArrayNode get(DBaccess2 conObj, JsonNode obj) throws DBoperationException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ArrayNode arrayNode = objectMapper.createArrayNode();
            this.id = obj.get("id").asText();
            this.createDBcon(conObj);
            if (DBcon.dqlQuery("SELECT u.*,p.id AS 'privilegeGroupId' from user u left join privilegegroup p on u.privilegeGroupId=p.id WHERE u.id='" + this.id + "'")) {
                DataTable dt = DBcon.getResultSet();
                if (dt.next()) {
                    UserOld userOld = new UserOld(dt.getString("id"), dt.getString("name"), dt.getBigInteger("cardUid"), dt.getString("mobileNo"), dt.getString("email"), dt.getInt("disabled"));
                    userOld.pin = dt.getString("pin");
                    userOld.emailId = dt.getString("emailId");
                    userOld.validFrom = dt.getString("validFrom");
                    userOld.validUpto = dt.getString("validUpto");
                    userOld.fp1 = dt.getInt("fp1");
                    userOld.fp2 = dt.getInt("fp2");
                    userOld.authType = dt.getInt("authType");
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.add("id", new JsonPrimitive(dt.getInt("privilegeGroupId")));
                    ObjectMapper om = new ObjectMapper();
                    ObjectNode objectNode = om.createObjectNode();
                    objectNode.put("id",dt.getInt("privilegeGroupId"));


                    userOld.privilegeGroup = objectMapper.treeToValue(new PrivilegeGroup().get(DBcon, objectNode).get(0), PrivilegeGroup.class);

                    arrayNode.add(userOld.toJson());
                    return arrayNode;
                } else {
                    throw new DBoperationException(GET, "Entity Not Found");
                }
            } else {
                throw new DBoperationException(GET, "Query Failed");
            }
        } catch (Exception ex) {
            throw new DBoperationException(GET, ex);
        }
    }

    @Override
    public long getCount(DBaccess2 conObj, JsonNode filter) throws DBoperationException {
        return 0;
    }

    @Override
    public ArrayNode getAll(DBaccess2 conObj, JsonNode filter) throws DBoperationException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ArrayNode arrayNode = objectMapper.createArrayNode();
            int all = filter.get("all").asInt();
            int limit = filter.get("limit").asInt();
            this.createDBcon(conObj);
            String qry = "";
            switch (all) {

                case DBentity.GET_ALL_ENABLED:
                    qry = "SELECT * FROM user u WHERE u.privilegeGroupId not in(0,1) and u.disabled!=0";
                    break;
                default:
                    qry = "SELECT u.id,u.name,u.emailId,u.mobileNo,u.validFrom,u.validUpto,pg.name AS 'role' FROM user u\n" +
                            "LEFT JOIN privilegegroup pg ON pg.id=u.privilegeGroupId\n" +
                            " where u.privilegeGroupId not in(0,1) limit "+limit;
                    break;
            }
            System.out.println("######111 :"+System.currentTimeMillis());
            if (DBcon.dqlQuery(qry)) {
                System.out.println("######222 :"+System.currentTimeMillis());
                DataTable dt = DBcon.getResultSet();
                System.out.println("######333 :"+System.currentTimeMillis());
                UserOld userOld =null;
                PrivilegeGroup privilegeGroup=null;
                while (dt.next()) {
                    if(userOld ==null){
                        userOld = new UserOld(dt.getString("id"), dt.getString("name"), dt.getBigInteger("cardUid"), dt.getString("mobileNo"), dt.getString("emailId"), dt.getInt("disabled"));
                        privilegeGroup =new PrivilegeGroup(dt.getInt("privilegeGroupId"),dt.getString("role"));
                    }else {
                        userOld.id = dt.getString("id");
                        userOld.name = dt.getString("name");
                        userOld.cardUID = dt.getBigInteger("cardUid");
                        userOld.mobileNo = dt.getString("mobileNo");
                        userOld.emailId = dt.getString("emailId");
                        userOld.disabled = dt.getInt("disabled");

                        privilegeGroup.id=dt.getInt("privilegeGroupId");
                        privilegeGroup.name=dt.getString("role");
                    }

                    userOld.validFrom = dt.getString("validFrom");
                    userOld.validUpto = dt.getString("validUpto");
                    userOld.privilegeGroup = privilegeGroup;
                    arrayNode.add(userOld.toJson().deepCopy());
                }
                System.out.println("######444 :"+System.currentTimeMillis());

                return arrayNode;
            } else {
                throw new DBoperationException(GET_ALL, "Query Failed");
            }
        } catch (Exception ex) {
            throw new DBoperationException(GET_ALL, ex);
        }
    }
    public void getAllNew2(DBaccess2 conObj, JsonNode filter)  {
        this.createDBcon(conObj);
        AMS.liveData.startFetchingData();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    ObjectMapper objectMapper = new ObjectMapper();
                    int all = filter.get("all").asInt();
                    int limit = filter.has("limit") ? filter.get("limit").asInt() :1000;

                    String qry = "";
                    switch (all) {

                        case DBentity.GET_ALL_ENABLED:
                            qry = "SELECT * FROM user u WHERE u.privilegeGroupId not in(0,1) and u.disabled!=0";
                            break;
                        default:
                            qry = "SELECT u.id,u.name,u.emailId,u.mobileNo,u.cardUid,u.validFrom,u.validUpto,u.privilegeGroupId,pg.name AS 'role',u.disabled FROM user u\n" +
                                    "LEFT JOIN privilegegroup pg ON pg.id=u.privilegeGroupId\n" +
                                    " where u.privilegeGroupId not in(0,1) limit "+limit;
//                            qry = "SELECT u.id,u.name,u.emailId,u.mobileNo,u.validFrom,u.validUpto,pg.name AS 'role' FROM user u\n" +
//                                    "LEFT JOIN privilegegroup pg ON pg.id=u.privilegeGroupId\n" +
//                                    " where u.privilegeGroupId not in(0,1) ";
                            break;
                    }

                    System.out.println("######111 :"+System.currentTimeMillis());
                    if (DBcon.dqlQuery(qry)) {

                        System.out.println("######222 :"+System.currentTimeMillis());
                        DataTable dt = DBcon.getResultSet();
                        System.out.println("######333 :"+System.currentTimeMillis());
                        UserOld userOld =null;

                        ArrayNode arrayNode = objectMapper.createArrayNode();

                        int size=1000;
                        int total = dt.getRowCount();
                        int cntr=0;
                        while (dt.next()) {

                            ObjectNode objectNode = objectMapper.createObjectNode();
                            objectNode.put("id", dt.getString("id"));
                            objectNode.put("name", dt.getString("name"));
                            objectNode.put("cardUID", Helper.byteArrayToHexString(dt.getBigInteger("cardUid").toByteArray()));
                            objectNode.put("emailId", dt.getString("emailId"));
                            objectNode.put("mobileNo", dt.getString("mobileNo"));
                            objectNode.put("validFrom", dt.getString("validFrom"));
                            objectNode.put("validUpto", dt.getString("validUpto"));
                            objectNode.set("privilegeGroup", objectMapper.createObjectNode()
                                    .put("role",dt.getInt("privilegeGroupId"))
                                    .put("name",dt.getString("role"))

                            );
                            arrayNode.add(objectNode);
                            cntr++;
                            if(cntr>=total){
                                AMS.liveData.getAllQueueDataArrayNode.add(arrayNode.deepCopy());
                                AMS.liveData.startSending=true;
                                arrayNode.removeAll();
                            }
                            if(arrayNode.size()>=size){
                                AMS.liveData.getAllQueueDataArrayNode.add(arrayNode.deepCopy());
                                AMS.liveData.startSending=true;
                                arrayNode.removeAll();
                            }
//                            AMS.liveData.getAllQueueData.add(user.toJson());
//                            AMS.liveData.startSending=true;
                        }
                        System.out.println("######444 :"+System.currentTimeMillis());

//                        return arrayNode;
                    } else {
                        AMS.liveData.setError("Query Failed");
//                        throw new DBoperationException(GET_ALL, "Query Failed");
                    }
                } catch (Exception ex) {
                    AMS.liveData.setError(ex);
//                    throw new DBoperationException(GET_ALL, ex);
                }
                finally {
                    AMS.liveData.stopFetchingData();
                }
            }
        });
        t.start();

    }
    public void getAllNew(DBaccess2 conObj, JsonNode filter)  {
        this.createDBcon(conObj);
        AMS.liveData.startFetchingData();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    ObjectMapper objectMapper = new ObjectMapper();
                    int all = filter.get("all").asInt();
                    int limit = filter.has("limit") ? filter.get("limit").asInt() :1000;

                    String qry = "";
                    switch (all) {

                        case DBentity.GET_ALL_ENABLED:
                            qry = "SELECT * FROM user u WHERE u.privilegeGroupId not in(0,1) and u.disabled!=0";
                            break;
                        default:
                            qry = "SELECT u.id,u.name,u.emailId,u.mobileNo,u.validFrom,u.validUpto,pg.name AS 'role' FROM user u\n" +
                                    "LEFT JOIN privilegegroup pg ON pg.id=u.privilegeGroupId\n" +
                                    " where u.privilegeGroupId not in(0,1) limit "+limit;
//                            qry = "SELECT u.id,u.name,u.emailId,u.mobileNo,u.validFrom,u.validUpto,pg.name AS 'role' FROM user u\n" +
//                                    "LEFT JOIN privilegegroup pg ON pg.id=u.privilegeGroupId\n" +
//                                    " where u.privilegeGroupId not in(0,1) ";
                            break;
                    }

                    System.out.println("######111 :"+System.currentTimeMillis());
                    if (DBcon.dqlQuery(qry)) {

                        System.out.println("######222 :"+System.currentTimeMillis());
                        DataTable dt = DBcon.getResultSet();
                        System.out.println("######333 :"+System.currentTimeMillis());
                        UserOld userOld =null;

                        ArrayNode arrayNode = objectMapper.createArrayNode();

                        int size=1000;
                        int total = dt.getRowCount();
                        int cntr=0;
                        while (dt.next()) {
                            if(userOld ==null){
                                userOld = new UserOld(dt.getString("id"), dt.getString("name"), dt.getBigInteger("cardUid"), dt.getString("mobileNo"), dt.getString("emailId"), dt.getInt("disabled"));
                            }else {
                                userOld.id = dt.getString("id");
                                userOld.name = dt.getString("name");
                                userOld.cardUID = dt.getBigInteger("cardUid");
                                userOld.mobileNo = dt.getString("mobileNo");
                                userOld.emailId = dt.getString("emailId");
                                userOld.disabled = dt.getInt("disabled");
                            }

                            userOld.validFrom = dt.getString("validFrom");
                            userOld.validUpto = dt.getString("validUpto");
                            userOld.privilegeGroup = new PrivilegeGroup(dt.getInt("privilegeGroupId"),dt.getString("role"));

                            arrayNode.add(userOld.toJson().deepCopy());
                            cntr++;
                            if(cntr>=total){
                                AMS.liveData.getAllQueueDataArrayNode.add(arrayNode.deepCopy());
                                AMS.liveData.startSending=true;
                                arrayNode.removeAll();
                            }
                            if(arrayNode.size()>=size){
                                AMS.liveData.getAllQueueDataArrayNode.add(arrayNode.deepCopy());
                                AMS.liveData.startSending=true;
                                arrayNode.removeAll();
                            }
//                            AMS.liveData.getAllQueueData.add(user.toJson());
//                            AMS.liveData.startSending=true;
                        }
                        System.out.println("######444 :"+System.currentTimeMillis());

//                        return arrayNode;
                    } else {
                        AMS.liveData.setError("Query Failed");
//                        throw new DBoperationException(GET_ALL, "Query Failed");
                    }
                } catch (Exception ex) {
                    AMS.liveData.setError(ex);
//                    throw new DBoperationException(GET_ALL, ex);
                }
                finally {
                    AMS.liveData.stopFetchingData();
                }
            }
        });
        t.start();

    }
    public void getAllNew1(DBaccess2 conObj, JsonNode filter)  {
        this.createDBcon(conObj);
        AMS.liveData.startFetchingData();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    ObjectMapper objectMapper = new ObjectMapper();
                    int all = filter.get("all").asInt();
                    int limit = filter.has("limit") ? filter.get("limit").asInt() :1000;
                    Class.forName("com.mysql.jdbc.Driver").newInstance();
                    // Establish a connection to the database
                    Connection connection = DriverManager.getConnection("jdbc:mysql://"+Config.DBip+":"+Config.DBport+"/"+Config.DBname, Config.DBuser, Config.DBpassword);

                    // Create a PreparedStatement with placeholders (?)
                    Statement preparedStatement = connection.createStatement();


                    String qry = "";
                    switch (all) {

                        case DBentity.GET_ALL_ENABLED:
                            qry = "SELECT * FROM user u WHERE u.privilegeGroupId not in(0,1) and u.disabled!=0";
                            break;
                        default:
                            qry = "SELECT u.id,u.name,u.emailId,u.mobileNo,u.cardUid,u.validFrom,u.validUpto,u.privilegeGroupId,pg.name AS 'role',u.disabled FROM user u\n" +
                                    "LEFT JOIN privilegegroup pg ON pg.id=u.privilegeGroupId\n" +
                                    " where u.privilegeGroupId not in(0,1) limit "+limit;
//                            qry = "SELECT u.id,u.name,u.emailId,u.mobileNo,u.validFrom,u.validUpto,pg.name AS 'role' FROM user u\n" +
//                                    "LEFT JOIN privilegegroup pg ON pg.id=u.privilegeGroupId\n" +
//                                    " where u.privilegeGroupId not in(0,1) ";
                            break;
                    }
                    // Execute the query and obtain the result set
                    System.out.println("######111 :"+System.currentTimeMillis());
                    try (ResultSet dt = preparedStatement.executeQuery(qry)) {
                        System.out.println("######222 :"+System.currentTimeMillis());
                        UserOld userOld =null;

                        ArrayNode arrayNode = objectMapper.createArrayNode();
                        int size=1000;
                        dt.last();
                        int total = dt.getRow();
                        dt.beforeFirst();
                        int cntr=0;
                        // Process the result set
                        while (dt.next()) {
                            if(userOld ==null){
                                userOld = new UserOld(dt.getString("id"), dt.getString("name"), (new BigDecimal(dt.getObject("cardUid").toString())).toBigInteger(), dt.getString("mobileNo"), dt.getString("emailId"), dt.getInt("disabled"));
                            }else {
                                userOld.id = dt.getString("id");
                                userOld.name = dt.getString("name");
                                userOld.cardUID = (new BigDecimal(dt.getObject("cardUid").toString())).toBigInteger();
                                userOld.mobileNo = dt.getString("mobileNo");
                                userOld.emailId = dt.getString("emailId");
                                userOld.disabled = dt.getInt("disabled");
                            }

                            userOld.validFrom = dt.getString("validFrom");
                            userOld.validUpto = dt.getString("validUpto");
                            userOld.privilegeGroup = new PrivilegeGroup(dt.getInt("privilegeGroupId"),dt.getString("role"));

                            arrayNode.add(userOld.toJson().deepCopy());
                            cntr++;
                            if(cntr>=total){
                                AMS.liveData.getAllQueueDataArrayNode.add(arrayNode.deepCopy());
                                AMS.liveData.startSending=true;
                                arrayNode.removeAll();
                            }
                            if(arrayNode.size()>=size){
                                AMS.liveData.getAllQueueDataArrayNode.add(arrayNode.deepCopy());
                                AMS.liveData.startSending=true;
                                arrayNode.removeAll();
                            }

                        }
                    }
                    /*System.out.println("######111 :"+System.currentTimeMillis());
                    if (DBcon.dqlQuery(qry)) {

                        System.out.println("######222 :"+System.currentTimeMillis());
                        DataTable dt = DBcon.getResultSet();
                        System.out.println("######333 :"+System.currentTimeMillis());



                        while (dt.next()) {

//                            AMS.liveData.getAllQueueData.add(user.toJson());
//                            AMS.liveData.startSending=true;
                        }
                        System.out.println("######444 :"+System.currentTimeMillis());

//                        return arrayNode;
                    } else {
                        AMS.liveData.setError("Query Failed");
//                        throw new DBoperationException(GET_ALL, "Query Failed");
                    }*/
                } catch (Exception ex) {
                    AMS.liveData.setError(ex);
//                    throw new DBoperationException(GET_ALL, ex);
                }
                finally {
                    AMS.liveData.stopFetchingData();
                }
            }
        });
        t.start();

    }
    @Override
    public ArrayNode export(DBaccess2 conObj, JsonNode filter) throws DBoperationException {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = objectMapper.createArrayNode();
        return arrayNode;
    }

    @Override
    public JsonArray importCSV(DBaccess2 conObj, JsonArray arr) throws DBoperationException {
        return null;
    }

    /*private void getEnrolledFingers(DBaccess2 DBcon) throws SIPLlibException {
        this.enrolledFingers = "";
        this.createDBcon(DBcon);
        if (this.DBcon.dqlQuery("select GROUP_CONCAT(ub.`index`) as 'fingers' from userbiometrics ub where ub.`type`=1 and ub.userId='" + this.id + "'")) {
            DataTable dt = DBcon.getResultSet();
            if (dt.next()) {
                this.enrolledFingers = dt.getString("fingers");
            }
        }
    }*/

    @Override
    public JsonNode toJson() {
        /*ObjectMapper objectMapper = new ObjectMapper();

//        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            return objectMapper.valueToTree(this);*/

        ObjectMapper objectMapper = new ObjectMapper();
        if(uObjectNode==null){
            uObjectNode =objectMapper.createObjectNode();
        }

        if (this.id != null) {
            uObjectNode.put("id", this.id);
        } else {
//            uObjectNode.put("id", "");
        }
        if (this.name != null) {
            uObjectNode.put("name", this.name);
        } else {
//            uObjectNode.put("name", "");
        }
        if (this.cardUID != null) {
            uObjectNode.put("cardUID", Helper.byteArrayToHexString(this.cardUID.toByteArray()));
        } else {
//            uObjectNode.put("cardUID", "");
        }
        if (this.emailId != null) {
            uObjectNode.put("emailId", this.emailId);
        } else {
//            uObjectNode.put("emailId", "");
        }
        if (this.mobileNo != null) {
            uObjectNode.put("mobileNo", this.mobileNo);
        } else {
//            uObjectNode.put("mobileNo", "");
        }
        if (this.validFrom != null) {
            uObjectNode.put("validFrom", this.validFrom);
        } else {
//            uObjectNode.put("validFrom", "");
        }
        if (this.validUpto != null) {
            uObjectNode.put("validUpto", this.validUpto);
        } else {
//            uObjectNode.put("validUpto", "");
        }
        if (this.pin != null) {
            uObjectNode.put("pin", this.pin);
        } else {
//            uObjectNode.put("pin", "");
        }
        if (this.privilegeGroup != null) {
            uObjectNode.set("privilegeGroup", this.privilegeGroup.toJson());
            uObjectNode.put("role", this.privilegeGroup.id);
        } else {
            uObjectNode.set("privilegeGroup", new ObjectMapper().createObjectNode());
        }
        uObjectNode.put("fp1", this.fp1);
        uObjectNode.put("fp2", this.fp2);
        uObjectNode.put("authType", this.authType);
        return uObjectNode;
        /*



         obj = new JsonObject();
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
        if (this.cardUID != null) {
            obj.add("cardUID", new JsonPrimitive(Helper.byteArrayToHexString(this.cardUID.toByteArray())));
        } else {
            obj.add("cardUID", new JsonPrimitive(""));
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
        if (this.pin != null) {
            obj.add("pin", new JsonPrimitive(this.pin));
        } else {
            obj.add("pin", new JsonPrimitive(""));
        }
        if (this.privilegeGroup != null) {
            obj.add("privilegeGroup", this.privilegeGroup.toJson());
            obj.add("role", new JsonPrimitive(this.privilegeGroup.id));
        } else {
            obj.add("privilegeGroup", new JsonObject());
        }
        obj.add("fp1", new JsonPrimitive(this.fp1));
        obj.add("fp2", new JsonPrimitive(this.fp2));
        obj.add("authType", new JsonPrimitive(this.authType));

        return obj;*/
    }

    @Override
    public void fromJson(JsonNode json) {
        JsonNode je;
        je = json.get("id");
        if (je != null) {
            this.id = je.asText();
        }
        je = json.get("name");
        if (je != null) {
            this.name = je.asText();
        }
        je = json.get("cardUID");
        if (je != null) {
            this.cardUID = BigInteger.valueOf(Long.parseLong(je.asText(), 16));
        }
        je = json.get("emailId");
        if (je != null) {
            this.emailId = je.asText().equals("") ? null : je.asText();
        }
        je = json.get("mobileNo");
        if (je != null) {
            this.mobileNo = je.asText().equals("") ? null : je.asText();
        }
        je = json.get("validFrom");
        if (je != null) {
            this.validFrom = je.asText();
        }
        je = json.get("validUpto");
        if (je != null) {
            this.validUpto = je.asText();
        }
        je = json.get("pin");
        if (je != null) {
            this.pin = je.asText();
        }
        je = json.get("password");
        if (je != null) {
            this.password = je.asText();
        }
        je = json.get("privilegeGroupId");
        if (je != null) {
            this.privilegeGroup = new PrivilegeGroup();
            this.privilegeGroup.id = je.asInt();
        }
        je = json.get("fp1");
        if (je != null) {
            this.fp1 = je.asInt();
        }
        je = json.get("fp2");
        if (je != null) {
            this.fp2 = je.asInt();
        }
        je = json.get("authType");
        if (je != null) {
            this.authType = je.asInt();
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

    public DBentity getUserFromPassword(DBaccess2 conObj, String password) throws DBoperationException {
        try {
            this.createDBcon(conObj);
            if (DBcon.dqlQuery("SELECT u.*,p.id AS 'privilegeGroupId' from user u left join privilegegroup p on u.privilegeGroupId=p.id WHERE u.password='" + generateMD5(password) + "'")) {
                DataTable dt = DBcon.getResultSet();
                if (dt.next()) {
                    UserOld userOld = new UserOld(dt.getString("id"), dt.getString("name"), dt.getBigInteger("cardUid"), dt.getString("mobileNo"), dt.getString("emailId"), dt.getInt("disabled"));
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.add("id", new JsonPrimitive(dt.getInt("privilegeGroupId")));

                    ObjectMapper om = new ObjectMapper();
                    ObjectNode objectNode = om.createObjectNode();
                    objectNode.put("id",dt.getInt("privilegeGroupId"));

                    userOld.privilegeGroup = om.treeToValue(new PrivilegeGroup().get(DBcon, objectNode).get(0), PrivilegeGroup.class);


                    return userOld;
                } else {
                    throw new DBoperationException(GET, "Entity Not Found");
                }
            } else {
                throw new DBoperationException(GET, "Query Failed");
            }
        } catch (Exception ex) {
            throw new DBoperationException(GET, ex);
        }
    }
}
