package com.jigga.byter;

import com.jigga.byter.blueprints.*;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

class Menu {

    private final ActionObject actionObject = new ActionObject();

    Menu() {}

    void displayMenu() {

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Welcome to WeatherHub");

            System.out.println("1. My Profile");
            System.out.println("2. Get Weather");
            System.out.println("3. End Session");

            System.out.println("Select an Option, by entering a number!");

            try {

                int choice = scanner.nextInt();
                
                switch (choice) {
                    case 1 -> {
                                               // prompt whether to display or save profile
                        System.out.println("1. Display Profile");
                        System.out.println("2. Save Profile");
                        System.out.print("Choose action (1-2): ");
                        try {
                            int action = scanner.nextInt();
                            scanner.nextLine(); // consume newline
                            if (action == 1) {
                                System.out.print("Enter username to display: ");
                                String username = scanner.nextLine().trim();
                                if (username.isEmpty()) {
                                    System.out.println("No username entered. Returning to menu.");
                                } else {
                                    actionObject.displayProfile(username);
                                }
                            } else if (action == 2) {
                                // gather profile fields and save
                                System.out.print("Enter username: ");
                                String username = scanner.nextLine().trim();
                                if (username.isEmpty()) {
                                    System.out.println("Username required. Returning to menu.");
                                } else {
                                    System.out.print("Enter email (or leave blank): ");
                                    String email = scanner.nextLine().trim();
                                    System.out.print("Enter phone (or leave blank): ");
                                    String phone = scanner.nextLine().trim();
                                    System.out.print("Enter age (or leave blank): ");
                                    String ageInput = scanner.nextLine().trim();
                                    int age = 0;
                                    if (!ageInput.isEmpty()) {
                                        try {
                                            age = Integer.parseInt(ageInput);
                                        } catch (NumberFormatException nfe) {
                                            System.out.println("Invalid age provided, defaulting to 0.");
                                        }
                                    }
                                    System.out.print("Enter location (or leave blank): ");
                                    String location = scanner.nextLine().trim();

                                    Map<String, Object> profile = new HashMap<>();
                                    profile.put("username", username);
                                    profile.put("email", email.isEmpty() ? "not_set" : email);
                                    profile.put("phone", phone.isEmpty() ? "not_set" : phone);
                                    profile.put("age", age);
                                    profile.put("location", location.isEmpty() ? "not_set" : location);

                                    boolean saved = actionObject.setProfile(profile);
                                    if (saved) {
                                        System.out.println("Profile saved for user: " + username);
                                    } else {
                                        System.out.println("Failed to save profile.");
                                    }
                                }
                            } else {
                                System.out.println("Invalid action. Returning to menu.");
                            }
                        } catch (InputMismatchException ime) {
                            System.out.println("Invalid input. Returning to menu.");
                            scanner.nextLine(); // consume bad token
                        }
                        pause(700);
                    }
                    case 2 -> {
                        // ask city and fetch weather
                        scanner.nextLine(); // consume leftover newline
                        System.out.print("Enter city: ");
                        String city = scanner.nextLine().trim();
                        if (city.isEmpty()) {
                            System.out.println("No city entered. Returning to menu.");
                            pause(700);
                        } else {
                            Map<String, Object> weather = actionObject.getWeather(city);
                            if (weather.containsKey("error")) {
                                System.out.println("Unable to fetch weather: " + weather.get("error"));
                            } else {
                                System.out.println("Weather for " + weather.getOrDefault("location", city) + ":");
                                System.out.println("Temperature: " + weather.getOrDefault("temperature", "n/a"));
                                System.out.println("Condition: " + weather.getOrDefault("condition", "n/a"));
                            }
                            pause(700);
                        }
                        pause(700);
                    }
                    case 3 -> {
                        System.out.println("Session Ended!");
                        scanner.close();
                        pause(500);
                        return;
                    }
                    default -> {
                        System.out.println("Invalid Option. Please try again.");
                        pause(500);
                    }
                }
            } catch (Exception e) {
                System.out.println("Error reading input:  Please try again with a valid number.");
                System.out.println();
                scanner.nextLine();
                pause(800);
            }

        }
    }

    // helper to sleep without throwing checked exception to callers
    private void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

}