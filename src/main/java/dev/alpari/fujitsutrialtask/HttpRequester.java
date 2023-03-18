package dev.alpari.fujitsutrialtask;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class HttpRequester {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /**
     * Make http request to the server's api by url and provide weather data as .json file.
     *
     * @param url api url
     * @param json json data (weather data)
     */
    public void makeHttpPostRequest(String url, String json) {

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            CompletableFuture<HttpResponse<String>> future = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

            // Get response
            HttpResponse<String> response = future.join();
            int statusCode = response.statusCode();
            HttpHeaders headers = response.headers();
            String responseBody = response.body();

            System.out.println("Status code: " + statusCode);
            System.out.println("Headers: " + headers);
            System.out.println("Response body: " + responseBody);

        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
