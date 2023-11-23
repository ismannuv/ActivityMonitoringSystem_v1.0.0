/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.senergy.ams.model;

/**
 *
 * @author Maharashtra
 */
public interface JsonEntity {
    public com.google.gson.JsonObject toJson();

    public void fromJson(com.google.gson.JsonObject json);
}
