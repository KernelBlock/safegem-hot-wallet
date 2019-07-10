package com.bankledger.safegem.utils;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.bankledger.safegem.R;
import com.bankledger.safegem.app.SafeGemApplication;

/**
 * @author bankledger
 * @time 2018/10/24 14:33
 */
public class RingManager {

    private final SoundPool soundPool;

    private static RingManager INSTANCE;
    private int beepId;
    private int crystalId;
    private boolean beepIsComplete = false;
    private boolean crystalIsComplete = false;


    private SoundPool.OnLoadCompleteListener loadCompleteListener = new SoundPool.OnLoadCompleteListener() {
        @Override
        public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
            soundPool.play(sampleId, 1, 1, 0, 0, 1);
            if (sampleId == beepId) {
                beepIsComplete = true;
            } else if (sampleId == crystalId) {
                crystalIsComplete = true;
            } else {
            }
        }
    };

    private RingManager() {
        if (Build.VERSION.SDK_INT >= 21) {
            SoundPool.Builder builder = new SoundPool.Builder();
            //传入音频的数量
            builder.setMaxStreams(1);
            //AudioAttributes是一个封装音频各种属性的类
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            //设置音频流的合适属性
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
            builder.setAudioAttributes(attrBuilder.build());
            soundPool = builder.build();
        } else {
            //第一个参数是可以支持的声音数量，第二个是声音类型，第三个是声音品质
            soundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 5);
        }

    }

    public static RingManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RingManager();
        }
        return INSTANCE;
    }

    public void playBeep() {
        if (beepIsComplete) {
            soundPool.play(beepId, 1, 1, 0, 0, 1);
        }else {
            beepId = soundPool.load(SafeGemApplication.getInstance(), R.raw.beep, 1);
            soundPool.setOnLoadCompleteListener(loadCompleteListener);
        }
    }

    public void playCrystal() {
        if (crystalIsComplete) {
            soundPool.play(crystalId, 1, 1, 0, 0, 1);
        }else {
            crystalId = soundPool.load(SafeGemApplication.getInstance(), R.raw.crystal_ring, 1);
            soundPool.setOnLoadCompleteListener(loadCompleteListener);
        }
    }
}
