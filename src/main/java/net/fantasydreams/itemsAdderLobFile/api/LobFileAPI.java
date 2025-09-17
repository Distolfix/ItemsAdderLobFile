package net.fantasydreams.itemsAdderLobFile.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class LobFileAPI {
    private final String apiKey;
    private final String baseUrl;
    private final HttpClient httpClient;
    private final Gson gson;

    public LobFileAPI(String apiKey, String baseUrl) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.gson = new Gson();
    }

    public String getLatestPackDownloadUrl() throws IOException, InterruptedException {
        String fullUrl = baseUrl + "/rest/get-file-list";
        System.out.println("DEBUG: Calling API URL: " + fullUrl);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .header("X-API-Key", apiKey)
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("API request failed with status: " + response.statusCode() + " - " + response.body());
        }

        JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);

        // Check if the API call was successful
        if (!jsonResponse.has("success") || !jsonResponse.get("success").getAsBoolean()) {
            throw new IOException("API call failed: " + jsonResponse.toString());
        }

        // Get the file list array
        if (!jsonResponse.has("file_list") || !jsonResponse.get("file_list").isJsonArray()) {
            throw new IOException("Invalid API response: missing or invalid 'file_list' field");
        }

        JsonArray files = jsonResponse.getAsJsonArray("file_list");

        if (files.size() == 0) {
            throw new IOException("No files found in the API response");
        }

        // Get the first file (most recent based on sorting by upload_time desc)
        JsonObject latestFile = files.get(0).getAsJsonObject();

        if (!latestFile.has("name") || !latestFile.has("extension")) {
            throw new IOException("Latest file missing name or extension");
        }

        String fileName = latestFile.get("name").getAsString();
        String extension = latestFile.get("extension").getAsString();

        // Get preferred domain from response
        String domain = "lobfile.com";
        if (jsonResponse.has("preferred_domain")) {
            domain = jsonResponse.get("preferred_domain").getAsString();
        }

        return "https://" + domain + "/file/" + fileName + "." + extension;
    }

    public boolean testConnection() {
        try {
            getLatestPackDownloadUrl();
            return true;
        } catch (Exception e) {
            System.out.println("API Test Error: " + e.getMessage());
            return false;
        }
    }
}