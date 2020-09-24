package com.example.exoplayerrecyclerdemo.util

import com.example.exoplayerrecyclerdemo.models.MediaObject

object Resources {
    val MEDIA_OBJECTS = arrayOf(
        MediaObject(
            title = "Sample 1",
            media_url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            thumbnail = "https://s3.ca-central-1.amazonaws.com/codingwithmitch/media/VideoPlayerRecyclerView/Sending+Data+to+a+New+Activity+with+Intent+Extras.png",
            description = "Description for media object #1"
        ),
        MediaObject(
            title = "Sample 2",
            media_url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
            thumbnail = "https://s3.ca-central-1.amazonaws.com/codingwithmitch/media/VideoPlayerRecyclerView/REST+API%2C+Retrofit2%2C+MVVM+Course+SUMMARY.png",
            description = "Description for media object #2"
        ),
        MediaObject(
            title = "Sample 3",
            media_url = "https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4",
            thumbnail = "https://s3.ca-central-1.amazonaws.com/codingwithmitch/media/VideoPlayerRecyclerView/mvvm+and+livedata.png",
            description = "Description for media object #3"
        ),
        MediaObject(
            title = "Big Buck Bunny",
            media_url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            thumbnail = "https://peach.blender.org/wp-content/uploads/bbb-splash.png?x28130",
            description = "Big Buck Bunny tells the story of a giant rabbit with a heart bigger than himself. When one sunny day three rodents rudely harass him, something snaps... and the rabbit ain't no bunny anymore! In the typical cartoon tradition he prepares the nasty rodents a comical revenge.\\n\\nLicensed under the Creative Commons Attribution license\\http://www.bigbuckbunny.org\",\n"
        )
    )
}