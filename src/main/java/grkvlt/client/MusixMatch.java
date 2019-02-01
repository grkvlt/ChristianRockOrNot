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

import com.google.common.collect.Iterables;
import grkvlt.data.RockLyrics;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import org.jmusixmatch.Helper;
import org.jmusixmatch.MusixMatchException;
import org.jmusixmatch.entity.lyrics.Lyrics;
import org.jmusixmatch.entity.lyrics.get.LyricsGetMessage;
import org.jmusixmatch.entity.track.MusicGenreList;
import org.jmusixmatch.entity.track.Track;
import org.jmusixmatch.entity.track.TrackData;
import org.jmusixmatch.entity.track.get.TrackGetMessage;
import org.jmusixmatch.entity.track.search.TrackSeachMessage;
import org.jmusixmatch.http.MusixMatchRequest;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class MusixMatch {

    public static final String API_KEY = "f7a91fb6c509ef60f0afc520b1fdc595";
    public static final Random RAND = new Random();
    public static final Map<String, String> CHRISTIAN_REPLACE = ImmutableMap.<String, String>builder()
            .put("the son of god", "my baby")
            .put("the lord", "my baby")
            .put("of jesus", "of my baby")
            .put("of christ", "of my baby")
            .put("of god", "of my baby")
            .put("him", "my baby")
            .put("jesus", "baby")
            .put("christ", "baby")
            .put("god", "baby")
            .put("lord", "baby")
            .put("our savior", "my baby")
            .put("our saviour", "my baby")
            .put("savior", "baby")
            .put("saviour", "baby")
            .put("in heaven", "with my baby")
            .put("heaven", "my baby's house")
            .put("king", "queen")
            .put(" his ", " her ")
            .put(" he ", " she ")
            .build();

    private String apiKey;

    public MusixMatch() {
        if (Iterables.any(Collections.list(System.getProperties().propertyNames()), (n) -> n.equals("musixMatchKey"))) {
            apiKey = System.getProperty("musixMatchKey");
        } else {
            apiKey = API_KEY;
        }
    }

    public int getRandom() {
        boolean christian = RAND.nextBoolean();
        RockLyrics lyrics = null;
        do {
            try {
                List<Track> tracks = searchTracks(christian ? 1098 : 21, christian ? "jesus" : "baby");
                if (tracks.isEmpty()) continue;

                TrackData track = tracks.get(RAND.nextInt(tracks.size())).getTrack();
                lyrics = getLyrics(track);
            } catch (MusixMatchException mme) {
                throw new RuntimeException(mme);
            }
        } while (lyrics == null || !lyrics.getLyrics().contains("baby"));

        return lyrics.getTrackId();
    }

    public RockLyrics getLyrics(int trackId) {
        try {
            TrackData track = getTrack(trackId);
            return getLyrics(track);
        } catch (MusixMatchException mme) {
            throw new RuntimeException(mme);
        }
    }

    public boolean isChristianRock(int trackId) {
        try {
            TrackData track = getTrack(trackId);
            return isChristianRock(track);
        } catch (MusixMatchException mme) {
            throw new RuntimeException(mme);
        }
    }

    private RockLyrics getLyrics(TrackData track) throws MusixMatchException {
        String lyrics = Strings.nullToEmpty(getTrackLyrics(track.getTrackId()).getLyricsBody());

        lyrics = Splitter.on('\n').splitToList(lyrics).stream()
                .filter((s) -> !(s.equals("...") || s.startsWith("***") || s.matches("\\([0-9]*\\)")))
                .collect(Collectors.joining("\n"));
        lyrics = lyrics.toLowerCase(Locale.ENGLISH);

        if (isChristianRock(track)) {
            for (String key : CHRISTIAN_REPLACE.keySet()) {
                if (lyrics.contains(key)) {
                    lyrics = lyrics.replaceAll(key, CHRISTIAN_REPLACE.get(key));
                }
            }
            lyrics = lyrics.replaceAll("\\(.*[0-9]*.*\\)", "");
        }

        RockLyrics result = new RockLyrics();
        result.setLyrics(lyrics);
        result.setTrackId(track.getTrackId());
        result.setArtist(track.getArtistName());
        result.setTitle(track.getTrackName());
        return result;
    }

    private boolean isChristianRock(TrackData track) {
        List<MusicGenreList> genres = track.getPrimaryGenres().getMusicGenreList();
        return genres.stream().anyMatch((g) -> g.getMusicGenre().getMusicGenreParentId() == 22);
    }

    private TrackData getTrack(int trackId) throws MusixMatchException {
        Map<String, Object> params = Maps.newHashMap();
        params.put("apikey", apiKey);
        params.put("track_id", trackId);

        String response = MusixMatchRequest.sendRequest(Helper.getURLString("track.get", params));
        Gson gson = new Gson();
        try {
            TrackGetMessage message = gson.fromJson(response, TrackGetMessage.class);

            int statusCode = message.getTrackMessage().getHeader().getStatusCode();
            if (statusCode > 200) {
                throw new MusixMatchException("Status Code is not 200");
            } else {
                return message.getTrackMessage().getBody().getTrack();
            }
        } catch (JsonParseException jpe) {
            throw new MusixMatchException(response, jpe);
        }
    }

    private Lyrics getTrackLyrics(int trackId) throws MusixMatchException {
        Map<String, Object> params = Maps.newHashMap();
        params.put("apikey", apiKey);
        params.put("track_id", trackId);

        String response = MusixMatchRequest.sendRequest(Helper.getURLString("track.lyrics.get", params));
        Gson gson = new Gson();
        try {
            LyricsGetMessage message = gson.fromJson(response, LyricsGetMessage.class);

            int statusCode = message.getContainer().getHeader().getStatusCode();
            if (statusCode > 200) {
                throw new MusixMatchException("Status Code is not 200");
            } else {
                return message.getContainer().getBody().getLyrics();
            }
        } catch (JsonParseException jpe) {
            throw new MusixMatchException(response, jpe);
        }
    }

    private List<Track> searchTracks(int genre, String lyricsQuery) throws MusixMatchException {
        Map<String, Object> params = Maps.newHashMap();
        params.put("apikey", apiKey);
        params.put("page", RAND.nextInt(10) + 1);
        params.put("page_size", 100);
        params.put("f_music_genre_id", genre);
        params.put("f_has_lyrics", "1");
        params.put("f_lyrics_language", "en");
        params.put("q_lyrics", Strings.nullToEmpty(lyricsQuery));

        String response = MusixMatchRequest.sendRequest(Helper.getURLString("track.search", params));
        Gson gson = new Gson();
        try {
            TrackSeachMessage message = gson.fromJson(response, TrackSeachMessage.class);

            int statusCode = message.getTrackMessage().getHeader().getStatusCode();
            if (statusCode > 200) {
                throw new MusixMatchException("Status Code is not 200");
            } else {
                return message.getTrackMessage().getBody().getTrack_list();
            }
        } catch (JsonParseException jpe) {
            throw new MusixMatchException(response, jpe);
        }
    }
}
