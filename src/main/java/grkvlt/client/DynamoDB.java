/*
 * Copyright 2019 by Andrew Donald Kennedy.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package grkvlt.client;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableResult;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.google.common.collect.ImmutableMap;
import grkvlt.data.Guesses;

public class DynamoDB {

    public static final String TABLE_NAME = "ChristianRockOrNot";

    private AmazonDynamoDB client;

    public DynamoDB() {
        client = client();

        try {
            DescribeTableResult table = client.describeTable(TABLE_NAME);
        } catch (ResourceNotFoundException rnfe) {
            client.createTable(new CreateTableRequest().withTableName(TABLE_NAME));
        }
    }

    private AmazonDynamoDB client() {
        ClientConfiguration clientConfiguration = new ClientConfiguration()
                .withMaxErrorRetry(10)
                .withThrottledRetries(true);
        return AmazonDynamoDBClientBuilder.standard()
                .withClientConfiguration(clientConfiguration)
                .build();
    }

    public Guesses getGuesses(Integer trackId) {
        GetItemRequest request = new GetItemRequest()
                .withTableName(TABLE_NAME)
                .withKey(ImmutableMap.of("TrackId", new AttributeValue().withN(trackId.toString())))
                .withAttributesToGet("Total", "ChristianRock");
        GetItemResult result = client.getItem(request);

        Integer total = Integer.valueOf(result.getItem().get("Total").getN());
        Integer christianRock = Integer.valueOf(result.getItem().get("ChristianRock").getN());

        Guesses guesses = new Guesses();
        guesses.setTrackId(trackId);
        guesses.setTotal(total);
        guesses.setChristianRock(christianRock);
        return guesses;
    }

    public void makeGuess(Integer trackId, Integer christianRock) {
        UpdateItemRequest updateTotal = new UpdateItemRequest()
                .withTableName(TABLE_NAME)
                .withKey(ImmutableMap.of("TrackId", new AttributeValue().withN(trackId.toString())))
                .withUpdateExpression("SET Total = Total + 1");
        client.updateItem(updateTotal);

        UpdateItemRequest updateChristianRock = new UpdateItemRequest()
                .withTableName(TABLE_NAME)
                .withKey(ImmutableMap.of("TrackId", new AttributeValue().withN(trackId.toString())))
                .withUpdateExpression("SET ChristianRock = ChristianRock + " + christianRock.toString());
        client.updateItem(updateChristianRock);
    }
}
