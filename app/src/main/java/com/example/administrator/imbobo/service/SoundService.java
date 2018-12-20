package com.example.administrator.imbobo.service;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Leon on 2018/6/2.
 * Functions: 音效服务
 */
public class SoundService {

    private Map<Integer,MediaPlayer> soundsCache = new HashMap<Integer,MediaPlayer>();
    private Context context;



    public SoundService(Context context) {
        this.context = context;
    }

    public MediaPlayer getSoundFromCache(int soundResId) {
        MediaPlayer mp = soundsCache.get(soundResId);
        if (mp == null) {
            mp = MediaPlayer.create(context, soundResId);

            soundsCache.put(soundResId, mp);
        }
        return mp;
    }


    public void clearCacheMap(){
        soundsCache.clear();
    }


    public void play(int resId) {

        MediaPlayer mp = getSoundFromCache(resId);
        mp.start();
    }

    public void stop(int resId) {

        MediaPlayer mp = getSoundFromCache(resId);
        if (mp == null) {
            return;
        }

        mp.pause();

        //解决先关闭声音玩两把在打开声音没有背景音乐的问题
        clearCacheMap();
        mp.release();
    }

    public void release() {
        for (Map.Entry<Integer, MediaPlayer> entry : soundsCache.entrySet()) {
            MediaPlayer mediaPlayer = entry.getValue();
            soundsCache.remove(entry.getKey());
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
            }
        }
    }


}




