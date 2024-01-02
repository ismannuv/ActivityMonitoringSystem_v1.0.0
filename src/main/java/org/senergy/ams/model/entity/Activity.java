package org.senergy.ams.model.entity;

import SIPLlib.DBaccess2;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonArray;
import org.senergy.ams.model.DBentity;
import org.senergy.ams.model.DBoperationException;

public class Activity extends DBentity {

    public final static String ENTITY_NANE = "Cabinet";

    public int code;
    public String name;
    protected String fromTime;
    protected String toTime;
    protected int weekDays;
    protected int activityTimeLimit;
    protected int frequency;

    protected User[] activityUsers;
    protected Key[] activityKeys;

    protected ObjectNode objectNode;

    public Activity() {
        super(ENTITY_NANE, 0);
    }

    public Activity(int code) {
        super(ENTITY_NANE, 0);
        this.code = code;
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

        ObjectMapper objectMapper = new ObjectMapper();

        if (objectNode == null) {
            objectNode = objectMapper.createObjectNode();
        }

        objectNode.put("code", this.code);

        if (this.name != null) {
            objectNode.put("name", this.name);
        } else {
            objectNode.put("code", "");
        }

        if (this.fromTime != null) {
            objectNode.put("fromTime", this.fromTime);
        } else {
            objectNode.put("fromTime", "");
        }

        if (this.toTime != null) {
            objectNode.put("toTime", this.toTime);
        } else {
            objectNode.put("toTime", "");
        }

        objectNode.put("weekDays", this.weekDays);
        objectNode.put("activityTimeLimit", this.activityTimeLimit);
        objectNode.put("frequency", this.frequency);

        if (this.activityUsers != null) {
            ArrayNode activityUsersArray = objectMapper.createArrayNode();
            for (int i = 0; i < this.activityUsers.length; i++) {
            }
            objectNode.set("activityUsers", activityUsersArray);
        } else {
            objectNode.set("activityUsers", objectMapper.createArrayNode());
        }

        if (this.activityKeys != null) {
            ArrayNode activityKeysArray = objectMapper.createArrayNode();
            for (int i = 0; i < this.activityKeys.length; i++) {

            }
            objectNode.set("activityKeys", activityKeysArray);
        } else {
            objectNode.set("activityKeys", objectMapper.createArrayNode());
        }

        return objectNode;
    }

    @Override
    public void fromJson(JsonNode json) {
        JsonNode je;

        je = json.get("code");
        if (je != null) {
            this.code = je.asInt();
        }

        je = json.get("name");
        if (je != null) {
            this.name = je.asText();
        }

        je = json.get("fromTime");
        if (je != null) {
            this.fromTime = je.asText();
        }

        je = json.get("toTime");
        if (je != null) {
            this.toTime = je.asText();
        }

        je = json.get("weekDays");
        if (je != null) {
            this.weekDays = je.asInt();
        }

        je = json.get("activityTimeLimit");
        if (je != null) {
            this.activityTimeLimit = je.asInt();
        }

        je = json.get("frequency");
        if (je != null) {
            this.frequency = je.asInt();
        }

        je = json.get("activityUsers");
        if (je != null && je.isArray()) {
            ArrayNode an = (ArrayNode) je;
            this.activityUsers = new User[an.size()];
            for (int i = 0; i < an.size(); i++) {
                this.activityUsers[i] = null;
            }
        }

        je = json.get("activityKeys");
        if (je != null && je.isArray()) {
            ArrayNode an = (ArrayNode) je;
            this.activityKeys = new Key[an.size()];
            for (int i = 0; i < an.size(); i++) {
                this.activityKeys[i] = null;
            }
        }
    }
}
