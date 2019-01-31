MusicMatch REST API Requests
============================

# Playground

> https://playground.musixmatch.com/#!/Track/get_track_search

# Search Tracks

`track.search`

- q_lyrics="baby"
- f_music_genre_id="21" // or "22", "1098" etc.
- f_lyrics_language="en"
- f_has_lyrics="1"
- quorum_factor="1" // remove?
- page_size="100" // max
- page="1" // limit is 100

> curl -X GET --header 'Accept: text/plain' 'https://api.musixmatch.com/ws/1.1/track.search?format=jsonp&callback=callback&q_lyrics=baby&f_music_genre_id=21&f_lyrics_language=en&f_has_lyrics=1&quorum_factor=1&page_size=100&page=1&apikey=f7a91fb6c509ef60f0afc520b1fdc595'

> https://api.musixmatch.com/ws/1.1/track.search?format=jsonp&callback=callback&q_lyrics=baby&f_music_genre_id=21&f_lyrics_language=en&f_has_lyrics=1&quorum_factor=1&page_size=100&page=1&apikey=f7a91fb6c509ef60f0afc520b1fdc595

# Get Track Lyrics

`track.lyrics.get`

- track_id="8809056" // etc.

> curl -X GET --header 'Accept: text/plain' 'https://api.musixmatch.com/ws/1.1/track.lyrics.get?format=jsonp&callback=callback&track_id=8809056&apikey=f7a91fb6c509ef60f0afc520b1fdc595'

> https://api.musixmatch.com/ws/1.1/track.lyrics.get?format=jsonp&callback=callback&track_id=8809056&apikey=f7a91fb6c509ef60f0afc520b1fdc595
