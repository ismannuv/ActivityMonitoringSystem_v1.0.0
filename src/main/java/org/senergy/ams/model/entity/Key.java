package org.senergy.ams.model.entity;

import SIPLlib.DBaccess2;
import SIPLlib.DataTable;
import SIPLlib.SIPLlibException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonArray;
import org.senergy.ams.model.DBentity;
import org.senergy.ams.model.DBoperationException;

import java.math.BigInteger;

public class Key extends DBentity {

    public final static String ENTITY_NANE = "Key";

    public int id;
    public int number;
    public String name;
    protected BigInteger tagUid;
    public String color;
    protected int strip;
    protected int position;
    public String description;

    protected ObjectNode objectNode;

    public Key() {
        super(ENTITY_NANE, 0);
    }

    public Key(int id) {
        super(ENTITY_NANE, 0);
        this.id = id;
    }

    public Key(int id, int number, String name, BigInteger tagUid, String color, int strip, int position, String description, int disabled) {
        super(ENTITY_NANE, 0);
        this.id = id;
        this.number = number;
        this.name = name;
        this.tagUid = tagUid;
        this.color = color;
        this.strip = strip;
        this.position = position;
        this.description = description;
        this.disabled = disabled;
    }

    public Key(int id, int number, String name, String color, String description, int disabled) {
        super(ENTITY_NANE, 0);
        this.id = id;
        this.number = number;
        this.name = name;
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
            if (this.tagUid != null && DBcon.dqlQuery("select k.id,k.name FROM `key` k WHERE k.tagUid = " + this.tagUid)) {
                DataTable dt = DBcon.getResultSet();
                if (dt.next()) {
                    throw new DBoperationException(ADD, "Key is already assigned to " + dt.getString("id") + " : " + dt.getString("name"));
                }
            }

            if (DBcon.preparedQuery("INSERT INTO `key` (`number`,`name`, tagUid, color, strip, `position`, `description`) VALUES (?,?,?,?,?,?,?)", this.number, this.name, this.tagUid, this.color, this.strip, this.position, this.description)) {
                return true;
            } else {
                throw new DBoperationException(ADD, "Failed to insert into Key table");
            }
        } catch (SIPLlibException ex) {
            throw new DBoperationException(ADD, ex);
        }
    }

    @Override
    public boolean update(DBaccess2 conObj) throws DBoperationException {
        try {
            this.createDBcon(conObj);

            if (DBcon.preparedQuery("UPDATE `key` SET `number`=?, `name`=?, tagUid=?, color=?, strip=?, `position`=?, `description`=? WHERE id = ?", this.number, this.name, this.tagUid, this.color, this.strip, this.position, this.description, this.id)) {
                return true;
            } else {
                throw new DBoperationException(UPDATE, "Failed to update key table");
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
            this.id = obj.get("id").asInt();
            if (DBcon.dqlQuery("SELECT * FROM `key` k WHERE k.id = '" + this.id + "'")) {
                DataTable dt = DBcon.getResultSet();
                if (dt.next()) {
                    Key key = new Key(dt.getInt("id"), dt.getInt("number"), dt.getString("name"), dt.getBigInteger("tagUid"), dt.getString("color"), dt.getInt("strip"), dt.getInt("position"), dt.getString("description"), dt.getInt("disabled"));
                    arrayNode.add(key.toJson());
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
                    qry = "SELECT k.id, k.number, k.name, k.color, k.description, k.disabled  FROM `key` k WHERE k.disabled = 0";
                    break;
                case DBentity.GET_ALL_DISABLED:
                    qry = "SELECT k.id, k.number, k.name, k.color, k.description, k.disabled  FROM `key` k WHERE k.disabled = 1";
                    break;
                default:
                    qry = "SELECT k.id, k.number, k.name, k.color, k.description, k.disabled  FROM `key` k ";
                    break;
            }
            if (DBcon.dqlQuery(qry)) {
                DataTable dt = DBcon.getResultSet();
                Key key = null;
                while (dt.next()) {
                    if (key == null) {
                        key = new Key(dt.getInt("id"), dt.getInt("number"), dt.getString("name"), dt.getString("color"), dt.getString("description"), dt.getInt("disabled"));
                    } else {
                        key.id = dt.getInt("id");
                        key.number = dt.getInt("number");
                        key.name = dt.getString("name");
                        key.color = dt.getString("color");
                        key.description = dt.getString("description");
                        key.disabled = dt.getInt("disabled");
                    }

                    arrayNode.add(key.toJson().deepCopy());
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

        objectNode.put("id", this.id);
        objectNode.put("number", this.number);

        if (this.name != null) {
            objectNode.put("name", this.name);
        } else {
            objectNode.put("name", "");
        }

        if (this.tagUid != null) {
            objectNode.put("tagUid", this.tagUid);
        } else {
            objectNode.put("tagUid", "");
        }

        if (this.color != null) {
            objectNode.put("color", this.color);
        } else {
            objectNode.put("color", "");
        }

        objectNode.put("strip", this.strip);
        objectNode.put("position", this.position);

        if (this.description != null) {
            objectNode.put("description", this.description);
        } else {
            objectNode.put("description", "");
        }

        return objectNode;
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
            this.tagUid = BigInteger.valueOf(Long.parseLong(je.asText(), 16));
        }
        je = json.get("color");
        if (je != null) {
            this.color = je.asText();
        }
        je = json.get("strip");
        if (je != null) {
            this.strip = je.asInt();
        }
        je = json.get("position");
        if (je != null) {
            this.position = je.asInt();
        }
        je = json.get("description");
        if (je != null) {
            this.description = je.asText();
        }
    }

}
