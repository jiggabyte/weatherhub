# WeatherHub

A small CLI Java application that demonstrates reading user profiles and fetching current weather for a city from OpenWeatherMap. The project is intended as a learning exercise to practice Java, simple I/O, HTTP requests, and basic file-based configuration.

Demo: https://youtu.be/_aOAM3z18dA

## Overview

WeatherHub provides a simple interactive menu:
- Manage a local (in-memory) user profile (display or save).
- Fetch current weather for a city using the OpenWeatherMap API and print a short summary to the console.

Main entry point: [`com.jigga.byter.Weather`](src/main/java/com/jigga/byter/Weather.java) which launches the interactive [`com.jigga.byter.Menu`](src/main/java/com/jigga/byter/Menu.java).

## Development environment

- Java 17 (OpenJDK or Oracle JDK)
- Maven (the project includes the Maven wrapper)
- Build with the wrapper included: `./mvnw clean package`

Key source files:
- [`src/main/java/com/jigga/byter/Weather.java`](src/main/java/com/jigga/byter/Weather.java)
- [`src/main/java/com/jigga/byter/Menu.java`](src/main/java/com/jigga/byter/Menu.java)
- [`src/main/java/com/jigga/byter/blueprints/ActionObject.java`](src/main/java/com/jigga/byter/blueprints/ActionObject.java)
- [`src/main/java/com/jigga/byter/blueprints/Actions.java`](src/main/java/com/jigga/byter/blueprints/Actions.java)

Project metadata: [`pom.xml`](pom.xml)

## Build and run

1. Build:
   ./mvnw clean package
2. Run the jar:
   java -jar byter-1.0-SNAPSHOT.jar


## Configuration / API key

Weather fetch requires an OpenWeatherMap API key. The app first checks the environment variable `OPENWEATHER_API_KEY` (via `System.getenv`). If not set, it will attempt to read a `.env` file in the project root as a fallback.

- Set the environment variable (recommended):
  export OPENWEATHER_API_KEY=your_api_key_here
- Or place a `.env` file at the project root with the line:
  OPENWEATHER_API_KEY=your_api_key_here

Example file path: [`/.env`](.env)

Note: storing secrets in source files is not recommended for production.

## How to use

- Start the program and follow the menu prompts.
- Option 1: "My Profile" will prompt to display or save a profile.
- Option 2: "Get Weather" prompts for a city and shows a simple weather summary retrieved by [`ActionObject.getWeather`](src/main/java/com/jigga/byter/blueprints/ActionObject.java).

The `getWeather` implementation performs a direct HTTPS GET to OpenWeatherMap and uses a small string-extraction helper to read key fields from the JSON response (kept dependency-free for simplicity).

## Libraries

The project declares `org.json:json` in `pom.xml`, but the current `ActionObject` uses lightweight string extraction to avoid extra runtime parsing libraries. If you prefer robust JSON parsing, add and use a JSON library (e.g., the declared `org.json`) and ensure it is on the runtime classpath.

## Useful websites

- OpenWeatherMap API docs: https://openweathermap.org/api
- Maven wrapper docs: https://github.com/takari/maven-wrapper
- Java 17 docs: https://docs.oracle.com/en/java/

## Future work

- Replace crude JSON extraction with a proper JSON parser (e.g., org.json, Jackson).
- Persist profiles to disk (JSON file) instead of keeping them in memory.
- Improve error handling and retry logic for network calls.
- Add unit tests for `ActionObject` and menu input