# YouTubeAnalyser
A Spring Boot application with a simple web frontend that analyses YouTube channels and videos using the YouTube Data API v3.

1. What does the project do?
- this program fetches data about YouTube channels and videos and calculates statistics that YouTube itself does not show directly. Every fetched channel and video is stored in a database, and once a day a scheduler saves a new snapshot of the views, likes, comments and subscribers. Over time these snapshots show how a channel or a video is growing.

Features:
- channel lookup by channel id (UC...) or handle (@name), including a history of the saved snapshots
- video lookup by video id or video link
- channel insights: average video length, on which days and hours the channel uploads, and how much of the total views comes from the top 10% most watched videos
- video insights: views increase since the last snapshot, average growth of views, likes and comments per day, momentum and whether the video has more views than the average video of the channel
- most watched, least watched, most recent, oldest and rising videos of a channel
- best and worst performers of a channel, based on my own score: total watch time (views * video length), weighted by the like and comment rate, plus a bonus if the video has paid product placement
- estimate of how many days a video needs to reach a target view count
- trending videos for a region and optionally for a category
- comments of a video or of a whole channel
- search for videos and channels


2. Getting started
- get your own YouTube Data API v3 key in the Google Cloud Console: https://console.cloud.google.com (create a project, enable "YouTube Data API v3" and create an API key)

- set the key as an environment variable
  export YOUTUBE_API_KEY=your_key

- run the app
  ./mvnw spring-boot:run

- open http://localhost:8080 in the browser for the frontend. The REST API is available under /api.

- the data is saved in an H2 file database in the folder ./data, so it is not lost after a restart. The snapshot interval can be changed with snapshot.refresh-interval-ms in the application.properties (default: once a day).

- the free quota of the YouTube Data API is limited per day. If the quota is used up or the API key is wrong, the API returns 502 Bad Gateway.


API ENDPOINTS:

CHANNELS (/api/channels)
- /{channelInput} : GET method that gives the data of a channel
- /multiple_search?channelInputs= : GET method that gives the data of multiple channels
- /{channelInput}/history : GET method that gives all saved snapshots of a channel
- /search/{searchQuery} : GET method that searches for channels

VIDEOS (/api/videos)
- /{videoInput} : GET method that gives the data of a video
- /{videoInput}/video_insights : GET method that gives the insights of a video
- /{videoInput}/growth : GET method that gives all saved snapshots of a video
- /{videoInput}/days_to_milestone?target= : GET method that gives the expected number of days until the video reaches the target views
- /multiple_search?videoInputs= : GET method that gives the data of multiple videos
- /{channelInput}/all : GET method that gives all videos of a channel
- /{channelInput}/insights : GET method that gives the insights of a channel
- /{channelInput}/most_watched?limit= : GET method that gives the most watched videos of a channel
- /{channelInput}/least_watched?limit= : GET method that gives the least watched videos of a channel
- /{channelInput}/recent?limit= : GET method that gives the most recent videos of a channel
- /{channelInput}/oldest?limit= : GET method that gives the oldest videos of a channel
- /{channelInput}/best_performers?limit= : GET method that gives the best performing videos of a channel
- /{channelInput}/worst_performers?limit= : GET method that gives the worst performing videos of a channel
- /{channelInput}/rising_videos?limit= : GET method that gives the videos with the highest momentum
- /{channelInput}/published_before?date= : GET method that gives the videos published before a date
- /{channelInput}/published_after?date= : GET method that gives the videos published after a date
- /{channelInput}/published_between?start_date=&end_date= : GET method that gives the videos published between two dates
- /trending?region= : GET method that gives the trending videos for a region, e.g. US
- /trending?region=&category= : GET method that gives the trending videos for a region and a category
- /search/{searchQuery} : GET method that searches for videos

COMMENTS (/api/comments)
- /{videoInput}/all : GET method that gives the comments of a video
- /{channelInput}/channel_comments : GET method that gives the comments of a channel

Dates use the ISO-8601 format, e.g. 2026-01-01T00:00:00Z
