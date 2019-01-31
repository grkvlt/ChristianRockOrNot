ChristianRockOrNot
==================

A web application that presents rock lyrics, with christian terms changed to secular ones, and challenges the viewer to decide if they are from a christian rock track or not. A running count of correct guesses is maintained for each track, and is presented after the viewer has made their choice.

Uses the [MusixMatch API](https://developer.musixmatch.com/) to download lyrics, and [Amazon DynamoDB](https://aws.amazon.com/dynamodb/) to store the results of guesses by users.

---
Copyright 2019 by [Andrew Donald Kennedy](mailto:andrew.international+christianrockornot@gmail.com) and
Licensed under the [Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)