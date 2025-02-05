package com.abdelrahman.elemary.httpserver.config;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
public class ConfigurationManager {

    private static  ConfigurationManager myConfigurationManager;
    private static  Configuration myCurrentConfiguration;
    private ConfigurationManager (){

    }

    public static  ConfigurationManager getInstance(){
        if (myConfigurationManager == null){
            myConfigurationManager =new ConfigurationManager();
        }
        return myConfigurationManager;
    }

    public  void loadConfigurationFile(String filePath)   {
        try {
            InputStream input = new FileInputStream(filePath);
            Properties prop = new Properties();
            prop.load(input);
            // load a properties file
            myCurrentConfiguration = new Configuration();

            // Add your property mappings here
             myCurrentConfiguration.setPort(Integer.parseInt(prop.getProperty("port")));
             myCurrentConfiguration.setWebroot(prop.getProperty("webroot"));

        } catch (IOException e) {
            throw new HttpConfigurationException(e);
        }



    }
    public Configuration  getCurrentConfiguration(){
            if (myCurrentConfiguration == null){
                throw new HttpConfigurationException("No current configuration is set");
            }
            return myCurrentConfiguration;
    }



}
