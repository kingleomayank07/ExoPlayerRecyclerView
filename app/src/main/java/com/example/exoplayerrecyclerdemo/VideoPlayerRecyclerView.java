package com.example.exoplayerrecyclerdemo;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.example.exoplayerrecyclerdemo.models.MediaObject;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.exoplayerrecyclerdemo.VideoPlayerRecyclerAdapter.VideoPlayerViewHolder;

public class VideoPlayerRecyclerView extends RecyclerView {

    private static final String TAG = "VideoPlayerRecyclerView";

    private enum VolumeState {ON, OFF}

    //region ui
    private ImageView thumbnail, volumeControl;
    private ProgressBar progressBar;
    private View viewHolderParent;
    private FrameLayout frameLayout;
    private PlayerView videoSurfaceView;
    private int targetPosition = 0;
    private SimpleExoPlayer videoPlayer;
    private boolean IsFirstVideo = true;
    //endregion

    //region vars
    private ArrayList<MediaObject> mediaObjects = new ArrayList<>();
    private int videoSurfaceDefaultHeight = 0;
    public static MutableLiveData<Boolean> hide = new MutableLiveData<>();
    private int screenDefaultHeight = 0;
    private int playPosition = -1;
    private final int MIN_BUFFER = 65536;
    private final int MAX_BUFFER = 131072;
    private final int BUFFER_PLAYBACK = 8064;
    private final int BUFFER_PLAYBACK_RE_BUFFER = 8064;
    private final int BACK_BUFFER_PLAYBACK = 80064;
    private int NEXT_FETCH_LIMIT = 3;
    private int CURRENT_FETCH_LIMIT = 0;
    private boolean isVideoViewAdded;
    private RequestManager requestManager;
    //    private CacheDataSourceFactory cacheDataSourceFactory;
    private DefaultDataSourceFactory defaultDataSourceFactory;
    private ConcatenatingMediaSource concatenatingMediaSource;
    //endregion

    //region controlling playback state
    private VolumeState volumeState;
    //endregion

    public VideoPlayerRecyclerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public VideoPlayerRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        hide.postValue(false);
        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point point = new Point();
        display.getRealSize(point);
        videoSurfaceDefaultHeight = point.x;
        screenDefaultHeight = point.y;

        videoSurfaceView = new PlayerView(context);
        videoSurfaceView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);

        TrackSelector trackSelector = new DefaultTrackSelector(context);

        DefaultLoadControl loadControl = new DefaultLoadControl.Builder()
                .setBufferDurationsMs(MIN_BUFFER, MAX_BUFFER,
                        BUFFER_PLAYBACK, BUFFER_PLAYBACK_RE_BUFFER)
                .setBackBuffer(BACK_BUFFER_PLAYBACK, true)
                .setPrioritizeTimeOverSizeThresholds(true)
                .build();

        videoPlayer = new SimpleExoPlayer.Builder(context)
                .experimentalSetThrowWhenStuckBuffering(true)
                .setLoadControl(loadControl)
                .setTrackSelector(trackSelector)
                .setBandwidthMeter(new DefaultBandwidthMeter
                        .Builder(context)
                        .build())
                .setHandleAudioBecomingNoisy(true)
                .build();

        videoSurfaceView.setUseController(true);
        videoSurfaceView.setPlayer(videoPlayer);

        setVolumeControl(VolumeState.ON);

        defaultDataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, getContext().getString(R.string.app_name)));

        concatenatingMediaSource = new ConcatenatingMediaSource();

        addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (thumbnail != null) {
                        thumbnail.setVisibility(VISIBLE);
                    }
                    if (!recyclerView.canScrollVertically(1)) {
                        playVideo(true);
                    } else {
                        playVideo(false);
                    }
                }
            }

            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (IsFirstVideo) {
                    playVideo(false);
                    IsFirstVideo = false;
                }
            }
        });

        addOnChildAttachStateChangeListener(new OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NotNull View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(@NotNull View view) {
                if (viewHolderParent != null && viewHolderParent.equals(view)) {
                    resetVideoView();
                }

            }
        });

        videoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                videoPlayer.seekTo(targetPosition, 0);
            }

            @Override
            public void onIsLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_BUFFERING) {
                    if (progressBar != null) {
                        progressBar.setVisibility(VISIBLE);
                    }
                } else if (state == Player.STATE_ENDED) {
                    videoPlayer.seekTo(0);
                } else if (state == Player.STATE_READY) {
                    if (progressBar != null) {
                        hide.postValue(true);
                        progressBar.setVisibility(GONE);
                    }
                    if (!isVideoViewAdded) {
                        addVideoView();
                    }
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }
        });
    }

    private void setList() {
        try {
            while (CURRENT_FETCH_LIMIT <= NEXT_FETCH_LIMIT) {
                ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(defaultDataSourceFactory)
                        .createMediaSource(new MediaItem.Builder().setUri(mediaObjects.get(CURRENT_FETCH_LIMIT).getMediaUrl()).build());
                concatenatingMediaSource.addMediaSource(mediaSource);
                CURRENT_FETCH_LIMIT++;
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        /* for (int i = 0; i < mediaObjects.size(); i++) {
            while (i == LIST_ADD)
                if (i == LIST_ADD) {

                }
        }*/
        videoPlayer.setMediaSource(concatenatingMediaSource);
        videoPlayer.prepare();
    }

    public void playVideo(boolean isEndOfList) {
        if (!isEndOfList) {
            int startPosition = ((LinearLayoutManager) Objects.requireNonNull(getLayoutManager())).findFirstVisibleItemPosition();
            int endPosition = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();

            if (endPosition - startPosition > 1) {
                endPosition = startPosition + 1;
            }

            if (startPosition < 0 || endPosition < 0) {
                return;
            }

            if (startPosition != endPosition) {
                int startPositionVideoHeight = getVisibleVideoSurfaceHeight(startPosition);
                int endPositionVideoHeight = getVisibleVideoSurfaceHeight(endPosition);

                targetPosition = startPositionVideoHeight > endPositionVideoHeight ? startPosition : endPosition;
            } else {
                targetPosition = startPosition;
            }
        } else {
            targetPosition = mediaObjects.size() - 1;
        }

        if (targetPosition == playPosition) {
            return;
        }

        playPosition = targetPosition;
        if (videoSurfaceView == null) {
            return;
        }

        removeVideoView(videoSurfaceView);

        int currentPosition = targetPosition - ((LinearLayoutManager)
                Objects.requireNonNull(getLayoutManager())).findFirstVisibleItemPosition();

        View child = getChildAt(currentPosition);
        if (child == null) {
            return;
        }

        VideoPlayerViewHolder holder = (VideoPlayerViewHolder) child.getTag();

        if (holder == null) {
            playPosition = -1;
            return;
        }

        thumbnail = holder.getThumbnail();
        progressBar = holder.getProgressBar();
        volumeControl = holder.getVolumeControl();
        viewHolderParent = holder.itemView;
        requestManager = holder.getRequestManager();
        frameLayout = holder.itemView.findViewById(R.id.media_container);

        videoSurfaceView.setPlayer(videoPlayer);
        viewHolderParent.setOnClickListener(videoViewClickListener);

        String mediaUrl = mediaObjects.get(targetPosition).getMediaUrl();

        if (mediaUrl != null) {
            PlayTargetPosition(targetPosition);
        }
    }

    private OnClickListener videoViewClickListener = v -> toggleVolume();

    private int getVisibleVideoSurfaceHeight(int playPosition) {
        int at = playPosition - ((LinearLayoutManager) Objects.requireNonNull(getLayoutManager())).findFirstVisibleItemPosition();

        View child = getChildAt(at);
        if (child == null) {
            return 0;
        }

        int[] location = new int[2];
        child.getLocationInWindow(location);

        if (location[1] < 0) {
            return location[1] + videoSurfaceDefaultHeight;
        } else {
            return screenDefaultHeight - location[1];
        }
    }

    private void removeVideoView(PlayerView videoView) {
        ViewGroup parent = (ViewGroup) videoView.getParent();
        if (parent == null) {
            return;
        }

        int index = parent.indexOfChild(videoView);
        if (index >= 0) {
            parent.removeViewAt(index);
            isVideoViewAdded = false;
            viewHolderParent.setOnClickListener(null);
        }
    }

    private void addVideoView() {
        frameLayout.addView(videoSurfaceView);
        isVideoViewAdded = true;
        videoSurfaceView.requestFocus();
        videoSurfaceView.setVisibility(VISIBLE);
        videoSurfaceView.setAlpha(1);
        thumbnail.setVisibility(GONE);
    }

    private void resetVideoView() {
        if (isVideoViewAdded) {
            removeVideoView(videoSurfaceView);
            playPosition = -1;
            videoSurfaceView.setVisibility(INVISIBLE);
            thumbnail.setVisibility(VISIBLE);
        }
    }

    public void releasePlayer() {

        if (videoPlayer != null) {
            videoPlayer.release();
            videoPlayer = null;
        }

        viewHolderParent = null;
    }

    public void pausePlayer() {

        if (videoPlayer != null) {
            videoPlayer.setPlayWhenReady(false);
        }
    }

    public void playPlayer() {

        if (videoPlayer != null) {
            videoPlayer.setPlayWhenReady(true);
        }
    }

    private void toggleVolume() {
        if (videoPlayer != null) {
            if (volumeState == VolumeState.OFF) {
                Log.d(TAG, "togglePlaybackState: enabling volume.");
                setVolumeControl(VolumeState.ON);

            } else if (volumeState == VolumeState.ON) {
                Log.d(TAG, "togglePlaybackState: disabling volume.");
                setVolumeControl(VolumeState.OFF);

            }
        }
    }

    private void PlayTargetPosition(final int targetPosition) {
        videoPlayer.seekTo(targetPosition, 0);
        videoPlayer.setPlayWhenReady(true);
    }

    private void setVolumeControl(VolumeState state) {
        volumeState = state;
        if (state == VolumeState.OFF) {
            videoPlayer.setVolume(0f);
            animateVolumeControl();
        } else if (state == VolumeState.ON) {
            videoPlayer.setVolume(1f);
            animateVolumeControl();
        }
    }

    private void animateVolumeControl() {
        if (volumeControl != null) {
            volumeControl.bringToFront();
            if (volumeState == VolumeState.OFF) {
                requestManager.load(R.drawable.ic_volume_off_grey_24dp)
                        .into(volumeControl);
            } else if (volumeState == VolumeState.ON) {
                requestManager.load(R.drawable.ic_volume_up_grey_24dp)
                        .into(volumeControl);
            }
            volumeControl.animate().cancel();

            volumeControl.setAlpha(1f);

            volumeControl.animate()
                    .alpha(0f)
                    .setDuration(600).setStartDelay(1000);
        }
    }

    public void setMediaObjects(ArrayList<MediaObject> mediaObjects) {
        this.mediaObjects = mediaObjects;
        setList();
    }

}



























