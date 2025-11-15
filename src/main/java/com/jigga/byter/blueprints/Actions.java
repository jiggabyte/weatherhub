package com.jigga.byter.blueprints;

import java.util.Map;

public interface Actions {

    void displayProfile(String username);

    Map<String, Object> getProfile(String username);

    boolean setProfile(Map<String, Object> profile);

    Map<String, Object> getWeather(String location);

}
