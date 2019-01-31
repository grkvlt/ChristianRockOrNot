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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/")
public class Resources {

    private MusixMatch musixMatch;
    private AzureSQL azureSQL;

    public Resources() {
        musixMatch = new MusixMatch();
        azureSQL = new AzureSQL();
    }

    @Path("lyrics")
    @Produces("application/json")
    @GET
    public RockLyrics lyrics() throws Exception {
        return musixMatch.getLyrics();
    }

    @Path("guess/{trackId}")
    @Produces("application/json")
    @POST
    public Guesses guess(@PathParam("trackId") Integer trackId, @QueryParam("christianRock") Integer christianRock) {
        azureSQL.makeGuess(trackId, christianRock);
        return azureSQL.getGuesses(trackId);
    }
}