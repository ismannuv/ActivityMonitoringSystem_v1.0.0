package org.senergy.ams.model.entity;

import SIPLlib.DBaccess2;
import SIPLlib.DataTable;
import SIPLlib.SIPLlibException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonArray;
import org.senergy.ams.model.Config;
import org.senergy.ams.model.DBentity;
import org.senergy.ams.model.DBoperationException;

import java.math.BigInteger;
import java.security.MessageDigest;

public class User extends DBentity {
    public static int NAME_SIZE=12;

    @Override
    public String toString() {
        return "User{" +
                "status=" + status +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", cardUID=" + cardUID +
                ", emailId='" + emailId + '\'' +
                ", mobileNo='" + mobileNo + '\'' +
                ", privilegeGroup=" + privilegeGroup +
                ", pin='" + pin + '\'' +
                ", validFrom='" + validFrom + '\'' +
                ", validUpto='" + validUpto + '\'' +
                ", fp1=" + fp1 +
                ", fp2=" + fp2 +
                ", authType=" + authType +
                ", objectNode=" + objectNode +
                '}';
    }

    public static enum USER_STATUS_ENUM {

        INVALID(0), VALIDITY_EXPIRED(1), VALID(2);
        private int value;

        private USER_STATUS_ENUM(int val) {
            this.value = val;
        }

        public static USER_STATUS_ENUM get(int val) {
            switch (val) {
                case 2:
                    return VALIDITY_EXPIRED;
                case 3:
                    return VALID;
                default:
                    return INVALID;
            }
        }

        public int getValue() {
            return this.value;
        }
    }
    public USER_STATUS_ENUM status=USER_STATUS_ENUM.INVALID;
    public final static String ENTITY_NANE = "User";

    public String id;
    public String name;
    private String password;
    public BigInteger cardUID;
    private String emailId;
    public String mobileNo;
    public PrivilegeGroup privilegeGroup;
    public String pin;
    public String validFrom, validUpto;
    public int fp1, fp2;
    private int authType = 1;

    protected ObjectNode objectNode;

    public User() {
        super(ENTITY_NANE, 0);
    }

    public User(String id) {
        super(ENTITY_NANE, 0);
        this.id = id;
    }

    public User(String id, String name, String emailId, String mobileNo, int disabled) {
        super(ENTITY_NANE, 0);
        this.id = id;
        this.name = name;
        this.emailId = emailId;
        this.mobileNo = mobileNo;
        this.disabled = disabled;
    }

    public User(String id, String name, BigInteger cardUID, String emailId, String mobileNo, String pin, String validFrom, String validUpto, int fp1, int fp2, int authType, int disabled) {
        super(ENTITY_NANE, 0);
        this.id = id;
        this.name = name;
        this.cardUID = cardUID;
        this.emailId = emailId;
        this.mobileNo = mobileNo;
        this.pin = pin;
        this.validFrom = validFrom;
        this.validUpto = validUpto;
        this.fp1 = fp1;
        this.fp2 = fp2;
        this.authType = authType;
        this.disabled = disabled;
    }



    public enum Operation_enum {

        UNKNOWN(0), LOGIN(1), LOGOUT(2), EDIT_PROFILE(3), CHANGE_PASSWORD(4), GET_SESSION(5), SET_SESSION(6), RESET_PASSWORD_ACCEPT(7), REQUEST_RESET_PASSWORD(8), SWITCH_LOCATION(9), RESET_PASSWORD_REJECT(10), VERIFY_PASSWORD(11), GET_RESET_PASS_REQUEST(12), GET_DB_STATISTICS_DATA(13), GET_NOTIFICATIONS(14), DISMISS_NOTIFICATION(15), PROCESS_STATUS(16), MAKE_AUDIT_LOG(17), SET_IDLE(18), GET_LICENCE_KEY(19), VERIFY_AD_PASSWORD(20);
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
                    objectNode.put("id", dt.getInt("privilegeGroupId"));

//                    this.privilegeGroup = (PrivilegeGroup) new PrivilegeGroup().get1(DBcon, objectNode);
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
    public String getIdentifier() {
        return null;
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
            this.createDBcon(conObj);
            ObjectMapper objectMapper = new ObjectMapper();
            ArrayNode arrayNode = objectMapper.createArrayNode();
            this.id = obj.get("id").asText();
            if (DBcon.dqlQuery("SELECT u.*,p.id AS 'privilegeGroupId' from user u left join privilegegroup p on u.privilegeGroupId=p.id WHERE u.id='" + this.id + "'")) {
                DataTable dt = DBcon.getResultSet();
                if (dt.next()) {
                    User user = new User(dt.getString("id"), dt.getString("name"), dt.getBigInteger("cardUID"), dt.getString("emailId"), dt.getString("mobileNo"), dt.getString("pin"),
                            dt.getString("validFrom"), dt.getString("validUpto"), dt.getInt("fp1"), dt.getInt("fp2"), dt.getInt("authType"),
                            dt.getInt("disabled"));

                    ObjectNode objectNode = objectMapper.createObjectNode();
                    objectNode.put("id", dt.getInt("privilegeGroupId"));
                    user.privilegeGroup = objectMapper.treeToValue(new PrivilegeGroup().get(DBcon, objectNode).get(0), PrivilegeGroup.class);

                    arrayNode.add(user.toJson());
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
            this.createDBcon(conObj);
            ObjectMapper objectMapper = new ObjectMapper();
            ArrayNode arrayNode = objectMapper.createArrayNode();
            int all = filter.get("all").asInt();
            String qry = "";
            switch (all) {
                case DBentity.GET_ALL_ENABLED:
                    qry = "SELECT * FROM user u WHERE u.privilegeGroupId not in(0,1) and u.disabled=0";
                    break;
                case DBentity.GET_ALL_DISABLED:
                    qry = "SELECT * FROM user u WHERE u.privilegeGroupId not in(0,1) and u.disabled=1";
                    break;
                default:
                    qry = "SELECT u.id,u.name,u.emailId,u.mobileNo,u.validFrom,u.validUpto,u.privilegeGroupId,pg.name AS 'role' FROM user u LEFT JOIN privilegegroup pg ON pg.id=u.privilegeGroupId where u.privilegeGroupId not in(0,1) ";
                    break;
            }
            if (DBcon.dqlQuery(qry)) {
                DataTable dt = DBcon.getResultSet();
                User user = null;
                PrivilegeGroup privilegeGroup = null;
                while (dt.next()) {
                    if (user == null) {
                        user = new User(dt.getString("id"), dt.getString("name"), dt.getString("emailId"), dt.getString("mobileNo"), dt.getInt("disabled"));
                        privilegeGroup = new PrivilegeGroup(dt.getInt("privilegeGroupId"), dt.getString("role"));
                    } else {
                        user.id = dt.getString("id");
                        user.name = dt.getString("name");
                        user.emailId = dt.getString("emailId");
                        user.mobileNo = dt.getString("mobileNo");
                        user.disabled = dt.getInt("disabled");
                    }
                    user.validFrom = dt.getString("validFrom");
                    user.validUpto = dt.getString("validUpto");

                    privilegeGroup.id = dt.getInt("privilegeGroupId");
                    privilegeGroup.name = dt.getString("role");
                    user.privilegeGroup = privilegeGroup;

                    arrayNode.add(user.toJson().deepCopy());
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
        return null;
    }

    @Override
    public JsonArray importCSV(DBaccess2 conObj, JsonArray arr) throws DBoperationException {
        return null;
    }

    @Override
    public JsonNode toJson() {

        ObjectMapper objectMapper = new ObjectMapper();

        if (objectNode == null) {
            objectNode = objectMapper.createObjectNode();
        }

        if (this.id != null) {
            objectNode.put("id", this.id);
        } else {
            objectNode.put("id", "");
        }

        if (this.name != null) {
            objectNode.put("name", this.name);
        } else {
            objectNode.put("name", "");
        }

        if (this.password != null) {
            objectNode.put("password", this.password);
        } else {
            objectNode.put("password", "");
        }

        if (this.cardUID != null) {
            objectNode.put("cardUID", this.cardUID);
        } else {
            objectNode.put("cardUID", "");
        }

        if (this.emailId != null) {
            objectNode.put("emailId", this.emailId);
        } else {
            objectNode.put("emailId", "");
        }

        if (this.mobileNo != null) {
            objectNode.put("mobileNo", this.mobileNo);
        } else {
            objectNode.put("mobileNo", "");
        }

        if (this.privilegeGroup != null) {
            objectNode.set("privilegeGroup", this.privilegeGroup.toJson());
            objectNode.put("role", this.privilegeGroup.id);
        } else {
            objectNode.set("privilegeGroup", new ObjectMapper().createObjectNode());
        }

        if (this.pin != null) {
            objectNode.put("pin", this.pin);
        } else {
            objectNode.put("pin", "");
        }

        if (this.validFrom != null) {
            objectNode.put("validFrom", this.validFrom);
        } else {
            objectNode.put("validFrom", "");
        }

        if (this.validUpto != null) {
            objectNode.put("validUpto", this.validUpto);
        } else {
            objectNode.put("validUpto", "");
        }

        objectNode.put("fp1", this.fp1);
        objectNode.put("fp2", this.fp2);
        objectNode.put("authType", this.authType);

        return objectNode;
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
    public boolean checkValidity()
    {
        try{
            long from,to,curr=System.currentTimeMillis();
            if(this.validFrom==null)
                from=curr;
            else
                from=Config.dateFormat.parse(this.validFrom).getTime();

            if(this.validUpto ==null)
                to=-1;
            else
                to= Config.dateFormat.parse(this.validUpto).getTime();

            if(to==-1)
            {
                if(from<=curr)
                    return true;
                else
                    return false;
            }
            else
            {
                if(from<=curr && curr<to)
                    return true;
                else
                    return false;
            }
        }
        catch(Exception e)
        {
            return false;
        }
    }
    public void getUserBy(int mode) throws Exception {
        this.createDBcon(null);
        String qry="";
        switch (mode){
           case 1://by card
               System.out.println("card :"+this.cardUID);
               qry="SELECT u.*,if(ub.userId IS NULL,0,1) AS 'FpEnrolled' from user u LEFT JOIN userbiometrics ub ON ub.userId=u.id where u.disabled=0 and u.cardUid ="+this.cardUID.toString();
               break;
            case 2://by FP (user id)
                System.out.println("id :"+this.id);

                qry="SELECT u.*,if(ub.userId IS NULL,0,1) AS 'FpEnrolled' from user u LEFT JOIN userbiometrics ub ON ub.userId=u.id where u.disabled=0 and u.id ='"+this.id+"'";

                break;
            case 3://by pin
                System.out.println("pin :"+this.pin);

                qry="SELECT u.*,if(ub.userId IS NULL,0,1) AS 'FpEnrolled' from user u LEFT JOIN userbiometrics ub ON ub.userId=u.id where u.disabled=0 and u.pin ='"+this.pin+"'";
                break;
        }
        if (DBcon.dqlQuery(qry) ) {
            DataTable dt = DBcon.getResultSet();
            if (dt.next()) {
                this.id = dt.getString("id");
                this.name = dt.getString("name");
                this.cardUID = dt.getBigInteger("cardUid");
                this.mobileNo = dt.getString("mobileNo");
                this.emailId = dt.getString("emailId");
                this.pin = dt.getString("pin");
                this.fp1 = dt.getInt("FpEnrolled");// using as fp enrolled or not
                this.validFrom = dt.getString("validFrom");
                this.validUpto = dt.getString("validUpto");
                if(!this.checkValidity()){
                    this.status = USER_STATUS_ENUM.VALIDITY_EXPIRED;
                }else{
                    this.status = USER_STATUS_ENUM.VALID;
                }
            }
        } else {
            throw new DBoperationException(GET, "Query Failed");
        }
    }
}
