package com.macilias.apps.service.crowdtangle;

import com.google.gson.*;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public class Service {

    private final Client client;
    private final String listId;

    public Service(Client client, String listId) {
        this.client = client;
        this.listId = listId;
    }

    public int followerCount(Optional<String> where, Optional<String> since) throws IOException {
        JsonObject json = client.get(new URL("https://api.crowdtangle.com/leaderboard?listId=" + listId));

        JsonObject result = json.getAsJsonObject("result");
        JsonArray accountStatistics = result.getAsJsonArray("accountStatistics");

        int followerCount = 0;

        for (JsonElement accountStatistic : accountStatistics) {
            JsonObject account = accountStatistic.getAsJsonObject().getAsJsonObject("account");

            followerCount += account.get("subscriberCount").getAsInt();
        }

        return followerCount;
    }
}
