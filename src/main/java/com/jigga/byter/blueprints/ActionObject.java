package com.jigga.byter.blueprints;

import java.util.HashMap;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.net.URL;

public class ActionObject implements Actions {

    private final ArrayList<Map<String, Object>> profileArray;

    public ActionObject() {
        profileArray = new ArrayList<>();
    }

    @Override
    public void displayProfile(String username) {
        Map<String, Object> profile = getProfile(username);
        if (profile != null) {
            System.out.println("User Profile:");
            System.out.println("Username: " + profile.getOrDefault("username", "not_set"));
            System.out.println("Email: " + profile.getOrDefault("email", "not_set"));
            System.out.println("Phone: " + profile.getOrDefault("phone", "not_set"));
            System.out.println("Age: " + profile.getOrDefault("age", 0));
            System.out.println("Location: " + profile.getOrDefault("location", "not_set"));
        } else {
            System.out.println("Profile not found.");
        }
    }

    @Override
    public Map<String, Object> getProfile(String username) {
        for (Map<String, Object> profile : profileArray) {
            Object u = profile.get("username");
            if (u != null && u.equals(username)) {
                return profile;
            }
        }
        Map<String, Object> newProfile = new HashMap<>();
        newProfile.put("username", (username == null || username.isEmpty()) ? "not_set" : username);
        newProfile.put("email", "not_set");
        newProfile.put("phone", "not_set");
        newProfile.put("age", 0);
        newProfile.put("location", "not_set");
        profileArray.add(newProfile);
        return newProfile;
    }

    @Override
    public boolean setProfile(Map<String, Object> profile) {
        // Implementation for setting the user profile
        return profileArray.add(profile);
    }

   @Override
    public Map<String, Object> getWeather(String city) {
        Map<String, Object> weatherData = new HashMap<>();
        String apiKey = System.getenv("OPENWEATHER_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            // try reading .env in project root as a fallback
            apiKey = readEnvVarFromDotEnv("OPENWEATHER_API_KEY");
        }
        if (apiKey == null || apiKey.isEmpty()) {
            weatherData.put("error", "OPENWEATHER_API_KEY not set in environment or .env");
            return weatherData;
        }

        try {
            String q = URLEncoder.encode(city, StandardCharsets.UTF_8);
            String urlStr = "https://api.openweathermap.org/data/2.5/weather?q=" + q + "&units=metric&appid=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8);
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(7000);
            conn.setRequestMethod("GET");

            int rc = conn.getResponseCode();
            BufferedReader in;
            if (rc >= 200 && rc < 300) {
                in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            } else {
                in = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
            }

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            in.close();
            String body = sb.toString();

            if (rc >= 200 && rc < 300) {
                // crude JSON extraction to avoid extra libs
                String tempStr = extractBetween(body, "\"temp\":", ",");
                String desc = extractBetween(body, "\"description\":\"", "\"");
                String loc = extractBetween(body, "\"name\":\"", "\"");

                String tempOut = (tempStr != null) ? (tempStr + "°C") : "n/a";
                String descOut = (desc != null) ? desc : "n/a";
                String locOut = (loc != null) ? loc : city;

                weatherData.put("location", locOut);
                weatherData.put("temperature", tempOut);
                weatherData.put("condition", descOut);
                weatherData.put("raw", body);
            } else {
                weatherData.put("error", "API error (code " + rc + "): " + body);
            }
        } catch (Exception e) {
            weatherData.put("error", "Exception: " + e.getMessage());
        }

        return weatherData;
    }

    // simple helper to grab substring after key up to terminator
    private String extractBetween(String src, String startToken, String endToken) {
        int s = src.indexOf(startToken);
        if (s == -1) return null;
        s += startToken.length();
        int e = (endToken == null) ? src.length() : src.indexOf(endToken, s);
        if (e == -1) e = src.length();
        return src.substring(s, e).replaceAll("[\"\\s]", "");
    }

    // read key from .env file in project root (simple parser)
    private String readEnvVarFromDotEnv(String key) {
        try {
            var path = Paths.get(".env");
            if (!Files.exists(path)) return null;
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (String ln : lines) {
                String line = ln.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                int eq = line.indexOf('=');
                if (eq == -1) continue;
                String k = line.substring(0, eq).trim();
                if (!k.equals(key)) continue;
                String v = line.substring(eq + 1).trim();
                // strip optional quotes
                if ((v.startsWith("\"") && v.endsWith("\"")) || (v.startsWith("'") && v.endsWith("'"))) {
                    v = v.substring(1, v.length() - 1);
                }
                return v;
            }
        } catch (IOException ignored) {}
        return null;
    }

}
