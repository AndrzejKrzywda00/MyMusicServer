package httpserver.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import httpserver.config.exceptions.HttpConfigurationException;
import util.Json;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ConfigurationManager {

    // this is singleton for normal purposes it's one for whole server

    private static ConfigurationManager manager;    // static is important
    private static Configuration myConfiguration;   // instance of configuratuion this manager has

    private ConfigurationManager() {
    }

    public static ConfigurationManager getInstance() {
        if (manager == null) {
            manager = new ConfigurationManager();
        }
        return manager;
    }

    /***
     * Used to load a configuration file by the path provider
     * @param filePath  is the path to .json file
     */
    public void loadConfigurationFile(String filePath) {
        FileReader fileReader = null;   // instance of reader

        try {
            fileReader = new FileReader(filePath);
        } catch (FileNotFoundException e) {
            throw new HttpConfigurationException(e);
        }

        StringBuffer buffer = new StringBuffer();
        int i;
        while (true) {
            try {
                if (!((i = fileReader.read()) != -1)) break;
            } catch (IOException e) {
                throw new HttpConfigurationException(e);
            }   // as log it reads
            buffer.append((char)i);
        }   // buffer is populated with data from file

        JsonNode conf = null;  // parses string to JsonNode

        try {
            conf = Json.parse(buffer.toString());
        } catch (IOException e) {
            throw new HttpConfigurationException("Error parsing the configuration file: ", e);
        }

        try {
            myConfiguration = Json.fromJson(conf,Configuration.class);
        } catch (JsonProcessingException e) {
            throw new HttpConfigurationException("Error parsing the configuration file internal: ", e);
        }
    }

    /***
     * Returns current loaded Configuration
     */
    public Configuration getConfiuration() {
        if (myConfiguration == null) {
            // do sth
            throw new HttpConfigurationException("No running configuration");
        }
        else {
            return myConfiguration;
        }
    }

}
