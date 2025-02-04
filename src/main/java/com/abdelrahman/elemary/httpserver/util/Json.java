package com.abdelrahman.elemary.httpserver.util;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import  com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class Json {
    private static final ObjectMapper myObjectMapper = defaultObjectMapper();

    private static ObjectMapper defaultObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        return om;
    }

    public static JsonNode parse(String jsonSrc) throws JsonProcessingException {
        return myObjectMapper.readTree(jsonSrc);

    }

    public static <A> A fromJSON(JsonNode node, Class <A> clazz) throws JsonProcessingException {
        return myObjectMapper.treeToValue(node,clazz);

    }

    public static JsonNode toJson (Object obj){
        return myObjectMapper.valueToTree(obj);
    }

    public static String stringify(){
            return "";
    }


    private static  String generateJson (Object o) throws JsonProcessingException {
        ObjectWriter objectWriter = myObjectMapper.writer();
       return objectWriter.writeValueAsString(o);
    }
}
