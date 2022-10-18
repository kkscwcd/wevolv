package com.wevolv.registration.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class TokenDecoder {

    /**
     * Method getUserIdFromToken, Taking token and decode it to take userId
     */
    public static String getUserIdFromToken(String token) {

        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getDecoder();
        String payload = new String(decoder.decode(chunks[1]));

        final ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> mapFromString = new HashMap<>();
        try {
            mapFromString = mapper.readValue(payload, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            throw new IllegalArgumentException("Bad JWT format or it's not provided.");
        }

        return (String) mapFromString.get("sub");

    }

    public static String getUserFullName(String token) {

        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getDecoder();
        String payload = new String(decoder.decode(chunks[1]));

        final ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> mapFromString = new HashMap<>();
        try {
            mapFromString = mapper.readValue(payload, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            throw new IllegalArgumentException("Bad JWT format or it's not provided.");
        }

        return (String) mapFromString.get("name");

    }

    public static String getJwt(HttpServletRequest request) {
        String jwt = request.getHeader("Authorization");
        if (jwt == null || jwt.equalsIgnoreCase("")) {
            throw new IllegalStateException("Unauthorized");
        }
        return jwt;
    }
    
    public static  Map<String, Object> getUserDetailsFromJWT(String token) {

        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getDecoder();
        String payload = new String(decoder.decode(chunks[1]));

        final ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> mapFromString = new HashMap<>();
        try {
            mapFromString = mapper.readValue(payload, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            throw new IllegalArgumentException("Bad JWT format or it's not provided.");
        }
        return mapFromString;
    }

}