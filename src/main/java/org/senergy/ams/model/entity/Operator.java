package org.senergy.ams.model.entity;

import SIPLlib.DBaccess2;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.gson.JsonArray;
import org.senergy.ams.model.DBentity;
import org.senergy.ams.model.DBoperationException;

public class Operator extends DBentity {
    public static final String ENTITY_NANE="Operator";
    public static final int SUPER_ADMIN = 1,DB_ADMIN=2,ADMIN=3;
    public String id;
    private String password;
    protected String name;
    private String emailId;
    public String mobileCountryCode,mobileNo;
    public int type;

    public boolean login() {
        return true;
    }

    public enum Operation_enum {

        UNKNOWN(0), LOGIN(1), LOGOUT(2), EDIT_PROFILE(3), CHANGE_PASSWORD(4),GET_SESSION(5),SET_SESSION(6),RESET_PASSWORD_ACCEPT(7),REQUEST_RESET_PASSWORD(8),SWITCH_LOCATION(9),RESET_PASSWORD_REJECT(10),VERIFY_PASSWORD(11), GET_RESET_PASS_REQUEST(12),GET_DB_STATISTICS_DATA(13),GET_NOTIFICATIONS(14),DISMISS_NOTIFICATION(15),PROCESS_STATUS(16),MAKE_AUDIT_LOG(17),SET_IDLE(18),GET_LICENCE_KEY(19),VERIFY_AD_PASSWORD(20);
        private int value;

        private Operation_enum(int val) {
            this.value = val;
        }

        public static Operation_enum get(int val) {
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
    public Operator() {
        super(ENTITY_NANE,0);
    }

    @Override
    public String getIdentifier() {
        return null;
    }

    @Override
    public boolean add(DBaccess2 conObj) throws DBoperationException {
        return false;
    }

    @Override
    public boolean update(DBaccess2 conObj) throws DBoperationException {
        return false;
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
        return null;
    }

    @Override
    public long getCount(DBaccess2 conObj, JsonNode filter) throws DBoperationException {
        return 0;
    }

    @Override
    public ArrayNode getAll(DBaccess2 conObj, JsonNode filter) throws DBoperationException {
        return null;
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
        return null;
        /*JsonObject obj = new JsonObject();
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
        je = json.get("emailId");
        if (je != null) {
            this.emailId = je.asText().isEmpty() ? null :je.asText();
        }

        je = json.get("password");
        if (je != null) {
            this.password = je.asText();
        }

    }

}
