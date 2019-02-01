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

import grkvlt.client.MusixMatch;
import grkvlt.data.RockLyrics;

public class Harness {

    private MusixMatch musixMatch;

    private Harness() {
        musixMatch = new MusixMatch();
    }

    private RockLyrics lyrics() {
        return musixMatch.getLyrics();
    }

    public static void main(String...argv) throws Exception {
        Harness harness = new Harness();
        RockLyrics lyrics = harness.lyrics();

        System.out.println(lyrics.getLyrics());
        System.out.println();
        System.out.println("==========");
        System.out.println();
        System.out.printf("Track:\t%s\n", lyrics.getTitle());
        System.out.printf("Artist:\t%s\n", lyrics.getArtist());
        System.out.println(lyrics.isChristianRock() ? "Christian Rock" : "Not");
        System.out.println();
    }
}