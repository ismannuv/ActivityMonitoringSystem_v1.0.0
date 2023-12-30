package org.senergy.ams.utils;

import org.senergy.ams.model.Config;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Global {
    public static String setServerNetwork(String ip,String gateway,String mask)
    {
        String result;
        String[] cmd=new String[]{"ifconfig",Config.LANinterface,ip};
        result=executeShellCommand(cmd);
        Config.logger.finest("ip set "+result);
        cmd=new String[]{"ifconfig",Config.LANinterface,"netmask",mask};
        result=executeShellCommand(cmd);
        Config.logger.finest("mask set "+result);
        cmd=new String[]{"route","add","default","gw",gateway};
        result=executeShellCommand(cmd);
        Config.logger.finest("gateway set "+result);
        return result;
    }
    public static String executeShellCommand(String[] command) {
        StringBuilder output = new StringBuilder();
        StringBuilder error = new StringBuilder();
        String s;
        Process p;
        try {
            // Config.logger.finest("exec shell command : ");
            for(String cmd:command)
                Config.logger.info(cmd+" ");
            p = Runtime.getRuntime().exec(command);

            p.waitFor();

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            BufferedReader stdError
                    = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            // read the output from the command
            while ((s = stdInput.readLine()) != null) {
                output.append(s);
            }

            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                error.append(s);
            }

        } catch (Exception e) {
            Config.logger.finer(e.toString());
        }
        if(error.length()>0)
            s = error.toString();
        else if(output.length()>0)
            s = output.toString();
        else
            s = "";
        // Config.logger.finest("reply : "+s);
        return s;
    }
}
