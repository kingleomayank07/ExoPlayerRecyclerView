package com.example.exoplayerrecyclerdemo.models

class MediaObject {
    var title: String? = null
    var mediaUrl: String? = null
    var thumbnail: String? = null
    var description: String? = null

    constructor(title: String?, media_url: String?, thumbnail: String?, description: String?) {
        this.title = title
        this.mediaUrl = media_url
        this.thumbnail = thumbnail
        this.description = description
    }

    constructor() {}
}