package com.abdelrahman.elemary.httpserver;

import com.abdelrahman.elemary.httpserver.config.Configuration;
import com.abdelrahman.elemary.httpserver.config.ConfigurationManager;

/**
 *
 *
 * Driver Class
 *
 */

public class HttpServer {
    public static void main(String[] args) {
        System.out.println("the server is now started");
        ConfigurationManager.getInstance().loadConfigurationFile("src/main/resources/http.properties");
        Configuration conf =ConfigurationManager.getInstance().getCurrentConfiguration();
        System.out.println(conf.getPort());
        System.out.println(conf.getWebroot());
    }
}