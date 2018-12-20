package  com.example.administrator.imbobo.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.example.administrator.imbobo.R;
import com.example.administrator.imbobo.utils.Constant;



/**
 * Created by Leon on 2018/6/3.
 * Functions:  音频服务类
 */
public class MediaService extends Service {

    SoundService soundService;

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        if (soundService == null) {
            soundService = new SoundService(this);
        }

        boolean pause;
        int resId;
        if (intent == null){
            pause = true;
            resId = R.raw.level_up;
        }else {
            pause = intent.getBooleanExtra(Constant.KEY_PAUSE_MUSIC, false);
            resId = intent.getIntExtra(Constant.KEY_SOUND_RES_ID_IN_INTENT, 0);
        }

        if (pause) {
            soundService.stop(resId);
        } else {
            if (resId != 0) {
                soundService.play(resId);
            }
        }
    }

    public static void play(Context context, int resId) {
        Intent intent = new Intent(context, MediaService.class);
        intent.putExtra(Constant.KEY_SOUND_RES_ID_IN_INTENT, resId);
        intent.putExtra(Constant.KEY_PAUSE_MUSIC, false);
        context.startService(intent);
    }

    public static void pause(Context context, int resId) {
        Intent intent = new Intent(context, MediaService.class);
        intent.putExtra(Constant.KEY_SOUND_RES_ID_IN_INTENT, resId);
        intent.putExtra(Constant.KEY_PAUSE_MUSIC, true);
        context.startService(intent);
    }

    @Override
    public void onDestroy() {
        if (soundService != null) {
            soundService.release();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
