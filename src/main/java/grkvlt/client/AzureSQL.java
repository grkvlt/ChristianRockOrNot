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

import com.google.common.base.Throwables;
import grkvlt.data.Guesses;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AzureSQL {

    public static final String CONNECTION_STRING = "jdbc:sqlserver://christianrock.database.windows.net:1433;database=christianrock;user=%s@christianrock;password=%s;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";

    private Connection connection;

    public AzureSQL() {
        String user = System.getProperty("azureSQLUser");
        String password = System.getProperty("azureSQLPassword");
        connection = connect(user, password);
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
    public Guesses getGuesses(Integer trackId) {
        try {
            PreparedStatement select = connection.prepareStatement("SELECT Total, ChristianRock FROM ChristianRockOrNot WHERE TrackId = ?");
            select.setInt(1, trackId);
            ResultSet results = select.executeQuery();
            Integer total = results.getInt("Total");
            Integer christianRock = results.getInt("ChristianRock");
            results.close();
            select.close();

            Guesses guesses = new Guesses();
            guesses.setTrackId(trackId);
            guesses.setTotal(total);
            guesses.setChristianRock(christianRock);
            return guesses;
        } catch (SQLException sqle) {
            throw new RuntimeException(sqle);
        }
    }

    /**
     * Make a guess as to whether a track is christian rock or not.
     *
     * @param trackId the track to make the guess for
     * @param christianRock one if the guess is christian rock, otherwise zero
     */
    public void makeGuess(Integer trackId, Integer christianRock) {
        try {
            PreparedStatement update = connection.prepareStatement("UPDATE ChristianRockOrNot SET Total = Total + 1, ChristianRock = ChristianRock + ? WHERE TrackId = ?");
            update.setInt(1, christianRock);
            update.setInt(2, trackId);
            update.executeUpdate();
            update.close();
        } catch (SQLException sqle) {
            throw new RuntimeException(sqle);
        }
    }
}
