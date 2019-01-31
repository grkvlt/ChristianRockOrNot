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
package grkvlt.data;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RockLyrics {

    @JsonProperty
    private String lyrics;

    @JsonProperty
    private int trackId;

    @JsonProperty
    private boolean christianRock;

    @JsonProperty
    private String title;

    @JsonProperty
    private String artist;

    @JsonGetter
    public String getLyrics() {
        return lyrics;
    }

    @JsonGetter
    public int getTrackId() {
        return trackId;
    }

    @JsonGetter
    public boolean isChristianRock() {
        return christianRock;
    }

    @JsonGetter
    public String getTitle() {
        return title;
    }

    @JsonGetter
    public String getArtist() {
        return artist;
    }

    @JsonSetter
    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    @JsonSetter
    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    @JsonSetter
    public void setChristianRock(boolean christianRock) {
        this.christianRock = christianRock;
    }

    @JsonSetter
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonSetter
    public void setArtist(String artist) {
        this.artist = artist;
    }
}
