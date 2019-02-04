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
package grkvlt;

import grkvlt.client.AzureSQL;
import grkvlt.client.MusixMatch;
import grkvlt.data.Guesses;
import grkvlt.data.RockLyrics;
import grkvlt.data.Statistics;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class Resources {

    public static final Integer NOT = 0;
    public static final Integer CHRISTIAN_ROCK = 1;

    private MusixMatch musixMatch;
    private AzureSQL azureSQL;

    public Resources() {
        musixMatch = new MusixMatch();
        azureSQL = new AzureSQL();
    }

    /**
     * Downloads a random track from either Christian Rock or Rock genres, and returns the {@literal trackId}.
     *
     * @return the track identifier from the lyrics service
     */
    @Path("random")
    @GET
    public Integer random() {
        return musixMatch.getRandom();
    }

    /**
     * Returns the lyrics of a paeticular track after performing a search and replace to change religious words
     * to more secular versions found in normal Rock tracks.
     *
     * @param trackId the track identifier from the lyrics service
     * @return the {@link RockLyrics lyrics} data for a track
     */
    @Path("lyrics/{trackId}")
    @GET
    public RockLyrics lyrics(@PathParam("trackId") Integer trackId) {
        return musixMatch.getLyrics(trackId);
    }

    /**
     * Submits a guess from the user as to whether a track is from the Christian Rock genre or not, and updates the
     * database of guesses. Returns the total number of guesses, the number correct and whether the track genre was
     * Christian Rock or not.
     *
     * @param trackId the track identifier from the lyrics service
     * @param christianRock set to one if the guess is Christian Rock, zero otherwise
     * @return the {@link Guesses guess} data for the track
     */
    @Path("guess/{trackId}")
    @POST
    public Guesses guess(@PathParam("trackId") Integer trackId, @QueryParam("christianRock") Integer christianRock) {
        int genre = musixMatch.isChristianRock(trackId) ? CHRISTIAN_ROCK : NOT;
        azureSQL.makeGuess(trackId, christianRock, genre);
        return azureSQL.getGuesses(trackId);
    }

    /**
     * Returns the number of guesses made, and the number of tracks guessed at.
     *
     * @return a {@link Statistics stats} object with the numbers
     */
    @Path("stats")
    @GET
    public Statistics stats() {
        return azureSQL.getStatistics();
    }
}