package org.senergy.ams.model.entity;

import SIPLlib.DBaccess2;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.senergy.ams.model.DBentity;
import org.senergy.ams.model.DBoperationException;

public class User extends DBentity {
    public final static String ENTITY_NANE="User";
    public User()
    {
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
