package org.senergy.ams.app;

import org.senergy.ams.model.Config;
import org.senergy.ams.server.ServerSync;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        Config config = new Config();
        config.init();
        ServerSync.start(Config.serverPort);
    }
}