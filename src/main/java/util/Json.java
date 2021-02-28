package util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;

import java.io.IOException;

public class Json {

    private static ObjectMapper mapper = defaultMapper();

    // method for creating an object mapper
    private static ObjectMapper defaultMapper() {
        // static configuration of a new element
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);     // won't crash on unknown properties
        return mapper;
    }

    // parses string to JsonNode file
    // String -> JsonNode
    public static JsonNode parse(String jsonSource) throws IOException {
         return mapper.readTree(jsonSource);    // mapper reads tree of .json file
    }

    // taking from json file to desired class
    // JsonNode -> class
    public static <A> A fromJson(JsonNode node, Class<A> targetClass) throws JsonProcessingException {
        return mapper.treeToValue(node, targetClass);   // this can also fail
    }

    // reverse mapping object to json
    // class -> JsonNode
    public static JsonNode toJson(Object obj) {
        return mapper.valueToTree(obj);
    }

    public static String stringify(JsonNode node) throws JsonProcessingException {
        return generateJson(node, false);
    }

    public static String stringifyPretty(JsonNode node) throws JsonProcessingException{
        return generateJson(node, true);
    }

    private static String generateJson(Object obj, Boolean pretty) throws JsonProcessingException{
        ObjectWriter writer = mapper.writer();
        if (pretty) {
            writer = writer.with(SerializationFeature.INDENT_OUTPUT);   // this will make it look good on logical level
        }
        return writer.writeValueAsString(obj);
    }
}
