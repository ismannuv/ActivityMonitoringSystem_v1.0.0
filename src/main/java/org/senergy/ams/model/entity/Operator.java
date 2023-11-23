package org.senergy.ams.model.entity;

import SIPLlib.DBaccess2;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.senergy.ams.model.DBentity;
import org.senergy.ams.model.DBoperationException;

public class Operator extends DBentity {
    public static final String ENTITY_NANE="Operator";

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
        return null;
    }

    @Override
    public long getCount(DBaccess2 conObj, JsonObject filter) throws DBoperationException {
        return 0;
    }

    @Override
    public DBentity[] getAll(DBaccess2 conObj, JsonObject filter) throws DBoperationException {
        return new DBentity[0];
    }

    @Override
    public DBentity[] export(DBaccess2 conObj, JsonObject filter) throws DBoperationException {
        return new DBentity[0];
    }

    @Override
    public JsonArray importCSV(DBaccess2 conObj, JsonArray arr) throws DBoperationException {
        return null;
    }

    @Override
    public void fromJson(JsonObject json) {


    }
}
