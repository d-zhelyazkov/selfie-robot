package xrc7331.selfierobot_camera2.gui;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by XRC_7331 on 4/12/2016.
 */
public abstract class MainThreadExecutor {
    private Handler mainThread = new Handler(Looper.getMainLooper());

    public void executeInMainThread(){
        mainThread.post(getExecutionBody());
    }

    protected abstract Runnable getExecutionBody();
}
