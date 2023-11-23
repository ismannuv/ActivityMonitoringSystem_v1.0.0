/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.senergy.ams.model;

import SIPLlib.DBaccess2;
import SIPLlib.MySQLDB2;

/**
 *
 * @author admin
 */
public class DBconnection {
    public static DBaccess2 newInstance(){return new MySQLDB2(Config.DBip,Config.DBport,Config.DBname,Config.DBuser,Config.DBpassword);}
}