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

import com.google.common.base.Strings;
import grkvlt.Resources;
import grkvlt.data.Guesses;
import grkvlt.data.Statistics;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AzureSQL {

    public static final String CONNECTION_STRING = "jdbc:sqlserver://christianrock.database.windows.net:1433;database=christianrock;user=%s@christianrock;password=%s;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";

    private Connection connection;

    public AzureSQL() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String user = System.getProperty("azureSQLUser");
        String password = System.getProperty("azureSQLPassword");
        if (!Strings.isNullOrEmpty(user) && !Strings.isNullOrEmpty(password)) {
            connection = connect(user, password);
        }
    }

    private Connection connect(String user, String password) {
        String connectionString = String.format(CONNECTION_STRING, user, password);
        try {
            return DriverManager.getConnection(connectionString);
        } catch (SQLException sqle) {
            throw new RuntimeException(sqle);
        }
    }

    /**
     * Retrieves the counts of total guesses and the number that were correct for a specific track.
     *
     * @param trackId the track to retrieve guesses for
     * @return a {@link Guesses} object with the counts
     */
    public Guesses getGuesses(int trackId) {
        try {
            PreparedStatement select = connection.prepareStatement("SELECT Total, Correct, Genre FROM ChristianRockOrNot WHERE TrackId = ?");
            select.setInt(1, trackId);
            ResultSet results = select.executeQuery();
            if (results.next()) {
                int total = results.getInt("Total");
                int correct = results.getInt("Correct");
                boolean christianRock = (results.getInt("Genre") == Resources.CHRISTIAN_ROCK);
                results.close();
                select.close();

                Guesses guesses = new Guesses();
                guesses.setTrackId(trackId);
                guesses.setTotal(total);
                guesses.setCorrect(correct);
                guesses.setChristianRock(christianRock);
                return guesses;
            } else {
                throw new RuntimeException("Database error");
            }
        } catch (SQLException sqle) {
            throw new RuntimeException(sqle);
        }
    }

    /**
     * Make a guess as to whether a track is christian rock or not.
     *
     * @param trackId the track to make the guess for
     * @param christianRock one if the guess is christian rock, otherwise zero
     * @param genre one if the genre is christian rock, otherwise zero
     */
    public void makeGuess(int trackId, int christianRock, int genre) {
        try {
            PreparedStatement select = connection.prepareStatement("SELECT COUNT(TrackId) AS Rows FROM ChristianRockOrNot WHERE TrackId = ?");
            select.setInt(1, trackId);
            ResultSet results = select.executeQuery();
            if (results.next()) {
                int rows = results.getInt("Rows");
                results.close();
                select.close();

                if (rows == 0) {
                    PreparedStatement insert = connection.prepareStatement("INSERT INTO ChristianRockOrNot (TrackId, Total, Correct, Genre) VALUES (?, 1, ?, ?)");
                    insert.setInt(1, trackId);
                    insert.setInt(2, christianRock == genre ? 1 : 0);
                    insert.setInt(3, genre);
                    insert.executeUpdate();
                    insert.close();
                } else {
                    PreparedStatement update = connection.prepareStatement("UPDATE ChristianRockOrNot SET Total = Total + 1, Correct = Correct + ?, Genre = ? WHERE TrackId = ?");
                    update.setInt(1, christianRock == genre ? 1 : 0);
                    update.setInt(2, genre);
                    update.setInt(3, trackId);
                    update.executeUpdate();
                    update.close();
                }
            } else {
                throw new RuntimeException("Database error");
            }
        } catch (SQLException sqle) {
            throw new RuntimeException(sqle);
        }
    }

    public Statistics getStatistics() {
        try {
            PreparedStatement select = connection.prepareStatement("SELECT SUM(Total) AS Guesses, COUNT(Total) AS Tracks FROM ChristianRockOrNot");
            ResultSet results = select.executeQuery();
            if (results.next()) {
                int guesses = results.getInt("Guesses");
                int tracks = results.getInt("Tracks");

                Statistics stats = new Statistics();
                stats.setGuesses(guesses);
                stats.setTracks(tracks);
                return stats;
            } else {
                throw new RuntimeException("Database error");
            }
        } catch (SQLException sqle) {
            throw new RuntimeException(sqle);
        }
    }
}
