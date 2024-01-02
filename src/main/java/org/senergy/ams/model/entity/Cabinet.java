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
import org.senergy.ams.model.DBentity;
import org.senergy.ams.model.DBoperationException;

public class Cabinet extends DBentity {

    public final static String ENTITY_NANE = "Cabinet";

    public int id;

    protected String siteName;
    protected String siteAddress;
    protected String siteContactNo;
    protected String siteRegistrationNo;

    public int cabinetId;
    public String cabinetName;
    public String location;
    protected String ip;
    protected String subnetMask;
    protected String gateway;
    protected String primaryDNS;
    protected String secondaryDNS;
    protected byte[] config;

    protected ObjectNode objectNode;


    public Cabinet() {
        super(ENTITY_NANE, 0);
    }

    public Cabinet(int id) {
        super(ENTITY_NANE, 0);
        this.id = id;
    }

    public Cabinet(int id, String siteName, int cabinetId, String cabinetName, String location, String ip, String subnetMask, String gateway, String primaryDNS, String secondaryDNS, int disabled) {
        super(ENTITY_NANE, 0);
        this.id = id;
        this.siteName = siteName;
        this.cabinetId = cabinetId;
        this.cabinetName = cabinetName;
        this.location = location;
        this.ip = ip;
        this.subnetMask = subnetMask;
        this.gateway = gateway;
        this.primaryDNS = primaryDNS;
        this.secondaryDNS = secondaryDNS;
        this.disabled = disabled;
    }

    public Cabinet(int id, String siteName, String siteAddress, String siteContactNo, String siteRegistrationNo, int disabled) {
        super(ENTITY_NANE, 0);
        this.id = id;
        this.siteName = siteName;
        this.siteAddress = siteAddress;
        this.siteContactNo = siteContactNo;
        this.siteRegistrationNo = siteRegistrationNo;
        this.disabled = disabled;
    }

    public Cabinet(int id, String siteName, String siteAddress, String siteContactNo, String siteRegistrationNo, int cabinetId, String cabinetName, String location, String ip, String subnetMask, String gateway, String primaryDNS, String secondaryDNS, byte[] config, int disabled) {
        super(ENTITY_NANE, 0);
        this.id = id;
        this.siteName = siteName;
        this.siteAddress = siteAddress;
        this.siteContactNo = siteContactNo;
        this.siteRegistrationNo = siteRegistrationNo;
        this.cabinetId = cabinetId;
        this.cabinetName = cabinetName;
        this.location = location;
        this.ip = ip;
        this.subnetMask = subnetMask;
        this.gateway = gateway;
        this.primaryDNS = primaryDNS;
        this.secondaryDNS = secondaryDNS;
        this.config = config;
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
            if (this.cabinetName != null && DBcon.dqlQuery("SELECT c.cabinetName, c.ip FROM cabinet c WHERE c.cabinetName = '" + this.cabinetName + "' OR c.ip = '" + this.ip + "'")) {
                DataTable dt = DBcon.getResultSet();
                if (dt.next()) {
                    throw new DBoperationException(ADD, "Cabinate is already assigned to " + dt.getString("cabinetName") + " : " + dt.getString("ip"));
                }
            }

            if (DBcon.preparedQuery("INSERT INTO cabinet (siteName, siteAddress, siteContactNo, siteRegistrationNo, cabinetId, cabinetName, location, ip, subnetMask, gateway, primaryDNS, secondaryDNS, config) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)", this.siteName, this.siteAddress, this.siteContactNo, this.siteRegistrationNo, this.cabinetId, this.cabinetName, this.location, this.ip, this.subnetMask, this.gateway, this.primaryDNS, this.secondaryDNS, this.config)) {
                return true;
            } else {
                throw new DBoperationException(ADD, "Failed to insert into cabinate table");
            }
        } catch (SIPLlibException ex) {
            throw new DBoperationException(ADD, ex);
        }

    }

    @Override
    public boolean update(DBaccess2 conObj) throws DBoperationException {
        try {
            this.createDBcon(conObj);

            if (DBcon.preparedQuery("UPDATE cabinet SET siteName=?, siteAddress=?, siteContactNo=?, siteRegistrationNo=?, cabinetId=?, cabinetName=?, location=?, ip=?, subnetMask=?, gateway=?, primaryDNS=?, secondaryDNS=?, config=? WHERE id=?", this.siteName, this.siteAddress, this.siteContactNo, this.siteRegistrationNo, this.cabinetId, this.cabinetName, this.location, this.ip, this.subnetMask, this.gateway, this.primaryDNS, this.secondaryDNS, this.config, this.id)) {
                return true;
            } else {
                throw new DBoperationException(UPDATE, "Failed to update cabinate table");
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
            if (DBcon.dqlQuery("SELECT  * FROM cabinet c WHERE c.id = '" + this.id + "'")) {
                DataTable dt = DBcon.getResultSet();
                if (dt.next()) {
                    Cabinet cabinet = new Cabinet(dt.getInt("id"), dt.getString("siteName"), dt.getString("siteAddress"), dt.getString("siteContactNo"), dt.getString("siteRegistrationNo"), dt.getInt("cabinetId"), dt.getString("cabinetName"), dt.getString("location"), dt.getString("ip"), dt.getString("subnetMask"), dt.getString("gateway"), dt.getString("primaryDNS"), dt.getString("secondaryDNS"), (byte[]) dt.getObject("config"), dt.getInt("disabled"));
                    arrayNode.add(cabinet.toJson());
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
                    qry = "SELECT c.id, c.siteName, c.cabinetId, c.cabinetName , c.location, c.ip, c.subnetMask, c.gateway, c.primaryDNS, c.secondaryDNS, c.disabled FROM cabinet c WHERE c.disabled = 0";
                    break;
                case DBentity.GET_ALL_DISABLED:
                    qry = "SELECT c.id, c.siteName, c.cabinetId, c.cabinetName, c.cabinetName , c.location, c.ip, c.subnetMask, c.gateway, c.primaryDNS, c.secondaryDNS, c.disabled FROM cabinet c WHERE c.disabled = 1";
                    break;
                default:
                    qry = "SELECT c.id, c.siteName, c.cabinetId, c.cabinetName, c.location, c.ip, c.subnetMask, c.gateway, c.primaryDNS, c.secondaryDNS, c.disabled FROM cabinet c ";
                    break;
            }
            if (DBcon.dqlQuery(qry)) {
                DataTable dt = DBcon.getResultSet();
                Cabinet cabinet = null;
                while (dt.next()) {
                    if (cabinet == null) {
                        cabinet = new Cabinet(dt.getInt("id"), dt.getString("siteName"), dt.getInt("cabinetId"), dt.getString("cabinetName"), dt.getString("location"),
                                dt.getString("ip"), dt.getString("subnetMask"), dt.getString("gateway"),
                                dt.getString("primaryDNS"), dt.getString("secondaryDNS"), dt.getInt("disabled"));
                    } else {
                        cabinet.id = dt.getInt("id");
                        cabinet.siteName = dt.getString("siteName");
                        cabinet.cabinetId = dt.getInt("cabinetId");
                        cabinet.cabinetName = dt.getString("cabinetName");
                        cabinet.location = dt.getString("location");
                        cabinet.ip = dt.getString("ip");
                        cabinet.subnetMask = dt.getString("subnetMask");
                        cabinet.gateway = dt.getString("gateway");
                        cabinet.primaryDNS = dt.getString("primaryDNS");
                        cabinet.secondaryDNS = dt.getString("secondaryDNS");
                        cabinet.disabled = dt.getInt("disabled");
                    }

                    arrayNode.add(cabinet.toJson().deepCopy());
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

        if (this.siteName != null) {
            objectNode.put("siteName", this.siteName);
        } else {
            objectNode.put("siteName", "");
        }

        if (this.siteAddress != null) {
            objectNode.put("siteAddress", this.siteAddress);
        } else {
            objectNode.put("siteAddress", "");
        }

        if (this.siteContactNo != null) {
            objectNode.put("siteContactNo", this.siteContactNo);
        } else {
            objectNode.put("siteContactNo", "");
        }

        if (this.siteRegistrationNo != null) {
            objectNode.put("siteRegistrationNo", this.siteRegistrationNo);
        } else {
            objectNode.put("siteRegistrationNo", "");
        }

        objectNode.put("cabinetId", this.cabinetId);

        if (this.cabinetName != null) {
            objectNode.put("cabinetName", this.cabinetName);
        } else {
            objectNode.put("cabinetName", "");
        }

        if (this.location != null) {
            objectNode.put("location", this.location);
        } else {
            objectNode.put("location", "");
        }

        if (this.ip != null) {
            objectNode.put("ip", this.ip);
        } else {
            objectNode.put("ip", "");
        }

        if (this.subnetMask != null) {
            objectNode.put("subnetMask", this.subnetMask);
        } else {
            objectNode.put("subnetMask", "");
        }

        if (this.gateway != null) {
            objectNode.put("gateway", this.gateway);
        } else {
            objectNode.put("gateway", "");
        }

        if (this.primaryDNS != null) {
            objectNode.put("primaryDNS", this.primaryDNS);
        } else {
            objectNode.put("primaryDNS", "");
        }

        if (this.secondaryDNS != null) {
            objectNode.put("secondaryDNS", this.secondaryDNS);
        } else {
            objectNode.put("secondaryDNS", "");
        }

        if (this.config != null) {
            objectNode.put("config", Helper.byteArrayToHexString(this.config));
        } else {
            objectNode.put("config", "");
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

        je = json.get("siteName");
        if (je != null) {
            this.siteName = je.asText();
        }

        je = json.get("siteAddress");
        if (je != null) {
            this.siteAddress = je.asText();
        }
        je = json.get("siteContactNo");
        if (je != null) {
            this.siteContactNo = je.asText();
        }

        je = json.get("siteRegistrationNo");
        if (je != null) {
            this.siteRegistrationNo = je.asText();
        }

        je = json.get("cabinetId");
        if (je != null) {
            this.cabinetId = je.asInt();
        }

        je = json.get("cabinetName");
        if (je != null) {
            this.cabinetName = je.asText();
        }

        je = json.get("location");
        if (je != null) {
            this.location = je.asText();
        }

        je = json.get("ip");
        if (je != null) {
            this.ip = je.asText();
        }

        je = json.get("subnetMask");
        if (je != null) {
            this.subnetMask = je.asText();
        }

        je = json.get("gateway");
        if (je != null) {
            this.gateway = je.asText();
        }

        je = json.get("primaryDNS");
        if (je != null) {
            this.primaryDNS = je.asText();
        }

        je = json.get("secondaryDNS");
        if (je != null) {
            this.secondaryDNS = je.asText();
        }

        je = json.get("config");
        if (je != null && !je.equals("")) {
            this.config = Helper.hexStringToByteArray(je.asText());
            if (this.config.length == 0) {
                this.config = null;
            }
        }

    }
}
