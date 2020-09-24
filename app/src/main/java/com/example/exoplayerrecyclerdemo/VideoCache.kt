package com.example.exoplayerrecyclerdemo

import android.content.Context
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import java.io.File

object VideoCache {
    private var sDownloadCache: SimpleCache? = null

    @JvmStatic
    fun getInstance(context: Context): SimpleCache? {
        if (sDownloadCache == null) sDownloadCache =
            SimpleCache(
                File(context.cacheDir, "exoPlayerRecycler"),
                LeastRecentlyUsedCacheEvictor(200 * 1024 * 1024)
            )
        return sDownloadCache
    }
}
