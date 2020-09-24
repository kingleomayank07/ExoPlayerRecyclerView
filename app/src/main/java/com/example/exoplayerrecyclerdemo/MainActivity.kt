package com.example.exoplayerrecyclerdemo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.example.exoplayerrecyclerdemo.VideoPlayerRecyclerView.hide
import com.example.exoplayerrecyclerdemo.util.Resources.MEDIA_OBJECTS
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        recycler_view.playPlayer()
    }

    override fun onPause() {
        super.onPause()
        recycler_view.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        recycler_view.releasePlayer()
    }

    private fun initRecyclerView() {
        val layoutManager = PreloadLinearLayoutManager(this)
        layoutManager.initialPrefetchItemCount = 6
        layoutManager.isItemPrefetchEnabled = true
        layoutManager.setPreloadItemCount(6)
        recycler_view.layoutManager = layoutManager
        recycler_view.setMediaObjects(arrayListOf(*MEDIA_OBJECTS))

        val adapter = initGlide()?.let {
            VideoPlayerRecyclerAdapter(
                arrayListOf(*MEDIA_OBJECTS),
                it
            )
        }

        recycler_view.adapter = adapter
        hide.observe(this, {
            if (it) {
                pg.visibility = View.GONE
                recycler_view.visibility = View.VISIBLE
            } else {
                pg.visibility = View.VISIBLE
                recycler_view.visibility = View.INVISIBLE
            }
        })

    }

    private fun initGlide(): RequestManager? {
        val options: RequestOptions = RequestOptions()
            .placeholder(R.drawable.white_background)
            .error(R.drawable.white_background)
        return Glide.with(this)
            .setDefaultRequestOptions(options)
    }

}