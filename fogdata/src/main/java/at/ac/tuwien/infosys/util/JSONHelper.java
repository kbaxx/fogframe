package at.ac.tuwien.infosys.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by Kevin Bachmann on 11/11/2016.
 */
public class JSONHelper {
    public static <T> String toJsonString(T obj){
        if(obj == null) return "";
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        String jsonString = "";
        //Object to JSON in String
        try {
            jsonString = mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
        return jsonString;
    }

    public static <T> T toObject(String jsonString, Class<T> clazz){
        if(jsonString == null || clazz == null) return null;
        if(jsonString.equals("OK")){
            System.out.println("OK AS JSON STRING??!!");
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        //JSON from String to Object
        T obj = null;
        try {
            obj = mapper.readValue(jsonString, clazz);
        } catch (IOException e) {
            System.out.println("ERROR jsonStr: "+jsonString);
            e.printStackTrace();
            return null;
        }
        return obj;
    }

    public static <T> T toCollection(String jsonString, Class collectionClazz, Class objectClass){
        if(jsonString == null || collectionClazz == null || objectClass == null) return null;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //JSON from String to Object
        T coll = null;
        try {
            coll = mapper.readValue(jsonString, mapper.getTypeFactory().constructCollectionType(collectionClazz, objectClass));
        } catch (IOException e) {
            System.out.println("ERROR jsonStr: "+jsonString);
            e.printStackTrace();
            return null;
        }
        return coll;
    }
}
