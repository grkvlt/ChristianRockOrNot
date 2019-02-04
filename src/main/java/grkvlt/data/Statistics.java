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
public class Statistics {

    @JsonProperty
    private int guesses;

    @JsonProperty
    private int tracks;

    @JsonGetter
    public int getGuesses() {
        return guesses;
    }

    @JsonGetter
    public int getTracks() {
        return tracks;
    }

    @JsonSetter
    public void setGuesses(int guesses) {
        this.guesses = guesses;
    }

    @JsonSetter
    public void setTracks(int tracks) {
        this.tracks = tracks;
    }
}
