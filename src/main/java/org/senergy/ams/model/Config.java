/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.senergy.ams.model;

import SIPLlib.Helper;
import SIPLlib.SimpleCrypto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;

import javax.crypto.SecretKey;
import java.io.*;
import java.security.Key;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.*;


/**
 * @author ismannuv
 */
public class Config {

    public static final String version = "AMS_v1.0.1";
    private static final LogFormatter fileFormatter = new LogFormatter();
    private static final LogFormatter consoleFormatter = new LogFormatter();
    public static DateFormat dateFormat;




    public static int logConfig = 0;
    public static Logger logger;


    private static boolean logIntoFile = false;
    private static boolean logOnConsole = false;
    private static boolean logFullExceptions = false;
    private static boolean logSIPLlib = false;
    private static int logFileSizeInBytes = 1024;
    private static int logFileCount = 5;
    private static String logLevel = "";
    private static String logFileDate = null;

    ////////////////// other configs //////////////

    ////HTTPS Server/////
    public static String sslKeyStorePathWithName;//for ssl certificate
    public static String keyStorePassword;
    public static String keyStoreType;//JKS //PCKS12
    public static boolean enableHttps;
    public static String postUrl;
    public static int serverPort;
    public static boolean debugMode;

    public static String DBip;
    public static String DBport;
    public static String DBname;
    public static String DBuser;
    public static String DBpassword;
    public static SecretKey JWT_KEY;
    public static String JWT_KEY_STRING;

    public void init() {
        Properties prop = new Properties();
        try {

            try {
                prop.load(new FileInputStream("Config.properties"));
            }
            catch (FileNotFoundException e)
            {
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Config.properties");
                if (inputStream != null) {
                    prop.load(inputStream);
                } else {
                    throw new FileNotFoundException("property file not found");
                }
            }
            JWT_KEY = Jwts.SIG.HS256.key().build();
            JWT_KEY_STRING=Encoders.BASE64.encode(JWT_KEY.getEncoded());
            DBip = prop.getProperty("DBip");
            DBport = prop.getProperty("DBport");
            DBname = prop.getProperty("DBname");
            DBuser = prop.getProperty("DBuser");
            DBpassword = SimpleCrypto.passwordDecrypt(prop.getProperty("DBpassword"));


            dateFormat = new SimpleDateFormat(prop.getProperty("dateFormat"));

            debugMode = Boolean.parseBoolean(prop.getProperty("debugMode", "false"));


            enableHttps = Boolean.parseBoolean(prop.getProperty("enableHttps","false"));


            ////////HTTPS Server///////
            sslKeyStorePathWithName = prop.getProperty("sslKeyStorePathWithName","/home/pi/sipl/ams/appData/ssl.keystore");
            keyStorePassword = prop.getProperty("keyStorePassword","sipl@0203");
            keyStoreType = prop.getProperty("keyStoreType","JKS");

            postUrl = prop.getProperty("postUrl","/faceApi");
            serverPort = Integer.parseInt(prop.getProperty("serverPort", "8050"));

            logIntoFile = Boolean.parseBoolean(prop.getProperty("logIntoFile", "false"));
            logOnConsole = Boolean.parseBoolean(prop.getProperty("logOnConsole", "true"));
            logFullExceptions = Boolean.parseBoolean(prop.getProperty("logFullExceptions", "false"));
            logSIPLlib = Boolean.parseBoolean(prop.getProperty("logSIPLlib", "false"));
            logFileSizeInBytes = Integer.parseInt(prop.getProperty("logFileSizeInBytes", "1024"));

            logFileCount = Integer.parseInt(prop.getProperty("logFileCount", "1024"));
            logLevel = prop.getProperty("logLevel", "CONFIG");
            logConfig = Integer.parseInt(prop.getProperty("logConfig", "0"));
            initLogger(version);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public static void initLogger(String appName) {

        logger = Logger.getLogger(appName);
        logger.setLevel(Level.ALL);
        logFileDate = Config.dateFormat.format(new Date());
        if (logIntoFile) {
            String fileName = "log/" + appName + "_app.log";
            try {
                // Create a FileHandler with 1MB file size and a single log file. We
                // also tell the handler to append the log message.
                FileHandler file = new FileHandler(fileName, logFileSizeInBytes, logFileCount, true);
                file.setFormatter(fileFormatter);
                logger.addHandler(file);
            } catch (Exception e) {
                logger.warning("Failed to initialize log file-" + fileName);
            }
        }
        if (logOnConsole) {
            try {
                logger.getParent().getHandlers()[0].setFormatter(consoleFormatter);
            } catch (Exception e) {
                logger.warning("Failed to initialize log consoleFormatter");
            }
        } else {
            logger.setUseParentHandlers(false);
        }

        if (logSIPLlib) {
            Helper.setDebugMode(true, logFullExceptions);
        } else {
            Helper.setDebugMode(false);
        }
//        System.setOut(new PrintStream(new LoggingOutputStream(logger, Level.INFO)));
//        System.setErr(new PrintStream(new LoggingOutputStream(logger, Level.WARNING)));
        try {
            logger.setLevel(Level.parse(logLevel));
        } catch (Exception e) {
            logger.setLevel(Level.CONFIG);
            logger.severe("Invalid Log level");
        }
    }

    public static void logException(Level level, Exception e) {
        if (e != null) {
            if (logFullExceptions) {
                logger.log(level, e.toString());
                e.printStackTrace();
            } else {
                logger.log(level, e.toString());
            }
        }
    }

    public static void logIfEnabled(int bit, String msg) {
        if ((logConfig & bit) > 0) {
            logger.info(msg);
        }
    }



}
class LogFormatter extends Formatter {

    private SimpleDateFormat dtFormat = new SimpleDateFormat("dd-MM-YYYY HH:mm:ss.sss");
    private String lastHeader = "";
    private long lastMillis = 0;

    @Override
    public String format(LogRecord record) {
        long millis = record.getMillis();
        boolean printHeader = true, printDt = false;
        String log = "";
        String header = record.getThreadID() + " " + record.getSourceClassName() + "." + record.getSourceMethodName();
        if (lastHeader.equals(header)) {
            if (Math.abs(lastMillis - millis) >= 100) {
                printDt = true;
            } else {
                printHeader = false;
            }
        }
        if (printHeader) {
            log = dtFormat.format(millis) + " : " + header + "()\n";
            lastHeader = header;
            lastMillis = millis;
        } else if (printDt) {
            log = dtFormat.format(millis) + " : \n";
            lastMillis = millis;
        }
        log += record.getLevel().getName() + ":" + record.getMessage() + "\n";
        return log;
    }
}

class LoggingOutputStream extends OutputStream {

    private final ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
    private final Logger logger;
    private final Level level;

    public LoggingOutputStream(Logger logger, Level level) {
        this.logger = logger;
        this.level = level;
    }

    @Override
    public void write(int b) {
        if (b == '\n') {
            String line = baos.toString();
            baos.reset();
            this.logger.log(this.level, line);
        } else {
            baos.write(b);
        }
    }

}


