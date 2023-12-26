package org.senergy.ams.model.entity;

import SIPLlib.DBaccess2;
import SIPLlib.DataTable;
import SIPLlib.SIPLlibException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.gson.JsonArray;
import org.senergy.ams.model.DBentity;
import org.senergy.ams.model.DBoperationException;

import java.math.BigInteger;

public class Key extends DBentity {

    public final static String ENTITY_NANE = "Key";

    public int id;
    private int number;
    private String name;
    private BigInteger tagUid;
    private int position;
    private String color;
    private String description;
    private int disabled;

    public Key() {
        super(ENTITY_NANE, 0);
    }

    public Key(String id) {

        super(ENTITY_NANE, 0);
        this.id = Integer.parseInt(id);
    }

    public Key(int id, int number, String name, BigInteger tagUid, int position, String color, String description, int disabled) {
        super(ENTITY_NANE, disabled);
        this.id = id;
        this.number = number;
        this.name = name;
        this.tagUid = tagUid;
        this.position = position;
        this.color = color;
        this.description = description;
        this.disabled = disabled;
    }

    @Override
    public String getIdentifier() {
        return null;
    }

    @Override
    public boolean add(DBaccess2 conObj) throws DBoperationException {
        try {
            this.createDBcon(conObj);
            if (this.tagUid != null && DBcon.dqlQuery("SELECT k.id,k.name FROM `key` k WHERE k.tagUid =" + this.tagUid)) {
                DataTable dt = DBcon.getResultSet();
                if (dt.next()) {
                    throw new DBoperationException(ADD, "Key is already assigned to " + dt.getString("id") + " : " + dt.getString("name"));
                }
            }
            return true;

            /*if (DBcon.preparedQuery("insert into user (id, name, cardUid, emailId, mobileNo, validFrom, validUpto, pin, password, privilegeGroupId, fp1, fp2, authType) values(?,?,?,?,?,?,?,?,?,?,?,?,?)", this.id, this.name, this.cardUID, this.emailId, this.mobileNo, this.validFrom, this.validUpto, this.pin, this.password, this.privilegeGroup.id, this.fp1, this.fp2, this.authType)) {
                return true;
            } else {
                throw new DBoperationException(ADD, "Failed to insert into user table");
            }*/
        } catch (SIPLlibException ex) {
            throw new DBoperationException(ADD, ex);
        }
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
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ArrayNode arrayNode = objectMapper.createArrayNode();
            int all = filter.get("all").asInt();
            this.createDBcon(conObj);
            String qry = "";
            switch (all) {
                case DBentity.GET_ALL:
                    qry = " select * from `key` k ";
                    break;
                case DBentity.GET_ALL_ENABLED:
                    qry = "select * from `key` k where k.disabled = 0";
                    break;
                case DBentity.GET_ALL_DISABLED:
                    qry = "select * from `key` k where k.disabled = 1";
                    break;
            }
            if (DBcon.dqlQuery(qry)) {
                DataTable dt = DBcon.getResultSet();
                while (dt.next()) {
                    Key key= new Key(dt.getInt("id"), dt.getInt("number"), dt.getString("name"), dt.getBigInteger("tagUid"), dt.getInt("position"), dt.getString("color"), dt.getString("description"), dt.getInt("disabled"));
                    arrayNode.add(key.toJson());
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
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = objectMapper.createArrayNode();
        return arrayNode;
    }

    @Override
    public JsonArray importCSV(DBaccess2 conObj, JsonArray arr) throws DBoperationException {
        return null;
    }

    @Override
    public JsonNode toJson() {
        return null;
        /*JsonObject obj = new JsonObject();
        obj.add("id", new JsonPrimitive(this.id));

        obj.add("number", new JsonPrimitive(this.number));

        if (this.name != null) {
            obj.add("name", new JsonPrimitive(this.name));
        } else {
            obj.add("name", new JsonPrimitive(""));
        }

        if (this.tagUid != null) {
            obj.add("tagUid", new JsonPrimitive(this.tagUid));
        } else {
            obj.add("tagUid", new JsonPrimitive(""));
        }

        obj.add("position", new JsonPrimitive(this.position));

        if (this.color != null) {
            obj.add("color", new JsonPrimitive(this.color));
        } else {
            obj.add("color", new JsonPrimitive(""));
        }

        if (this.description != null) {
            obj.add("description", new JsonPrimitive(this.description));
        } else {
            obj.add("description", new JsonPrimitive(""));
        }

        obj.add("disabled", new JsonPrimitive(this.disabled));

        return obj;*/
    }

    @Override
    public void fromJson(JsonNode json) {
        JsonNode je;
        je = json.get("id");
        if (je != null) {
            this.id = je.asInt();
        }
        je = json.get("number");
        if (je != null) {
            this.number = je.asInt();
        }
        je = json.get("name");
        if (je != null) {
            this.name = je.asText();
        }
        je = json.get("tagUid");
        if (je != null) {
            this.tagUid = BigInteger.valueOf(je.asLong());
        }
        je = json.get("position");
        if (je != null) {
            this.position = je.asInt();
        }
        je = json.get("color");
        if (je != null) {
            this.color = je.asText();
        }
        je = json.get("description");
        if (je != null) {
            this.description = je.asText();
        }
        je = json.get("disabled");
        if (je != null) {
            this.disabled = je.asInt();
        }
    }
}
