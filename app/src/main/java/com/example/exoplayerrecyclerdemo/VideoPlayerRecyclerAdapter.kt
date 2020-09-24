package com.example.exoplayerrecyclerdemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.RequestManager
import com.example.exoplayerrecyclerdemo.models.MediaObject
import java.util.*

class VideoPlayerRecyclerAdapter(
    private val mediaObjects: ArrayList<MediaObject>,
    private val requestManager: RequestManager
) : RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        return VideoPlayerViewHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.layout_video_list_item, viewGroup, false)
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        (viewHolder as VideoPlayerViewHolder).onBind(mediaObjects[i], requestManager)
    }

    override fun getItemCount(): Int {
        return mediaObjects.size
    }

    class VideoPlayerViewHolder(var parent: View) : ViewHolder(parent) {
        var mediaContainer: FrameLayout = itemView.findViewById(R.id.media_container)
        var title: TextView? = null
        var thumbnail: ImageView = itemView.findViewById(R.id.thumbnail)
        var volumeControl: ImageView = itemView.findViewById(R.id.volume_control)
        var progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        var requestManager: RequestManager? = null
        fun onBind(mediaObject: MediaObject, requestManager: RequestManager?) {
            this.requestManager = requestManager
            parent.tag = this
            this.requestManager?.load(mediaObject.thumbnail)
                ?.into(thumbnail)
        }
    }
}