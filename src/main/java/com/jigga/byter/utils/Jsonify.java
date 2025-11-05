package com.jigga.byter.utils;

import java.util.*;
/**
 * Utility class for converting Java objects to JSON strings and vice versa.
 * work in progress
 */
public class Jsonify {

    public Map<?,?> hashMap;

    public List<?> list;

    private boolean valid;

    private String key;

    private String value;

    private String item;

    private String[] window;

    private String openBrace = "{";

    private String closeBrace = "}";

    private String openSquareBracket = "[";

    private String closeSquareBracket = "]";

    private ArrayList<String> stacker = new ArrayList<>();


    public Jsonify(String json) {
        json = json.trim();
        window = new String[3];
        window[0] = String.valueOf(json.charAt(0));
        window[1] = String.valueOf(json.charAt(1));
        window[2] = String.valueOf(json.charAt(2));

         if(!window[0].equals(openBrace) || !window[0].equals(openSquareBracket)){
            valid = false;
            throw new Error("The json is invalid!");
         }

         for(Character letter : json.toCharArray()){
             System.out.println(letter);
             stacker.add(String.valueOf(letter));

         }

        for(int i = 0; i < json.length() - 3;i++){
            if(window[0].equals(openBrace)){
                stacker.add(openBrace);

            }


        }
    }

    public Jsonify(){}


    
    
}
