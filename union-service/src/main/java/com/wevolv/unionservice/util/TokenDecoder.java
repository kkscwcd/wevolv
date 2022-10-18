package com.wevolv.unionservice.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
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

    /**
     * Get the User subscribed group from JWT
     *
     * @param token
     * @return
     */
    public static List<String> getUserGroupsFromToken(String token) {

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
        return (List<String>) mapFromString.get("group_name");
    }

    public static String getUserGivenAndFamilyNameFromToken(String token) {

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
        return (String) mapFromString.get("given_name") + mapFromString.get("family_name");
    }
}
