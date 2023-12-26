/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.senergy.ams.model;

import com.fasterxml.jackson.databind.JsonNode;

/**
 *
 * @author Maharashtra
 */
public interface JsonEntity {
//    public com.google.gson.JsonObject toJson();
    public JsonNode toJson();

//    public void fromJson(com.google.gson.JsonObject json);
    public void fromJson(JsonNode jsonNode);
}
