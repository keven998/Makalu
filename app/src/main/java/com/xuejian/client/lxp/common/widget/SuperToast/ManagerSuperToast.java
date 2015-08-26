package com.xuejian.client.lxp.common.widget.SuperToast;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by yibiao.qin on 2015/8/26.
 */
public class ManagerSuperToast extends Handler {
    private static final String TAG = "ManagerSuperToast";
    private static ManagerSuperToast mManagerSuperToast;
    private final Queue<SuperToast> mQueue = new LinkedBlockingQueue();

    private ManagerSuperToast() {
    }

    protected static synchronized ManagerSuperToast getInstance() {
        if(mManagerSuperToast != null) {
            return mManagerSuperToast;
        } else {
            mManagerSuperToast = new ManagerSuperToast();
            return mManagerSuperToast;
        }
    }

    protected void add(SuperToast superToast) {
        this.mQueue.add(superToast);
        this.showNextSuperToast();
    }

    private void showNextSuperToast() {
        if(!this.mQueue.isEmpty()) {
            SuperToast superToast = (SuperToast)this.mQueue.peek();
            if(!superToast.isShowing()) {
                Message message = this.obtainMessage(4281172);
                message.obj = superToast;
                this.sendMessage(message);
            } else {
                this.sendMessageDelayed(superToast, 4477780, this.getDuration(superToast));
            }

        }
    }

    private void sendMessageDelayed(SuperToast superToast, int messageId, long delay) {
        Message message = this.obtainMessage(messageId);
        message.obj = superToast;
        this.sendMessageDelayed(message, delay);
    }

    private long getDuration(SuperToast superToast) {
        long duration = (long)superToast.getDuration();
        duration += 1000L;
        return duration;
    }

    public void handleMessage(Message message) {
        SuperToast superToast = (SuperToast)message.obj;
        switch(message.what) {
            case 4281172:
                this.displaySuperToast(superToast);
                break;
            case 4477780:
                this.showNextSuperToast();
                break;
            case 5395284:
                this.removeSuperToast(superToast);
                break;
            default:
                super.handleMessage(message);
        }

    }

    private void displaySuperToast(SuperToast superToast) {
        if(!superToast.isShowing()) {
            WindowManager windowManager = superToast.getWindowManager();
            View toastView = superToast.getView();
            WindowManager.LayoutParams params = superToast.getWindowManagerParams();
            if(windowManager != null) {
                windowManager.addView(toastView, params);
            }

            this.sendMessageDelayed(superToast, 5395284, (long)(superToast.getDuration() + 500));
        }
    }

    protected void removeSuperToast(SuperToast superToast) {
        WindowManager windowManager = superToast.getWindowManager();
        View toastView = superToast.getView();
        if(windowManager != null) {
            this.mQueue.poll();
            windowManager.removeView(toastView);
            this.sendMessageDelayed(superToast, 4477780, 500L);
            if(superToast.getOnDismissListener() != null) {
                superToast.getOnDismissListener().onDismiss(superToast.getView());
            }
        }

    }

    protected void cancelAllSuperToasts() {
        this.removeMessages(4281172);
        this.removeMessages(4477780);
        this.removeMessages(5395284);
        Iterator i$ = this.mQueue.iterator();

        while(i$.hasNext()) {
            SuperToast superToast = (SuperToast)i$.next();
            if(superToast.isShowing()) {
                superToast.getWindowManager().removeView(superToast.getView());
            }
        }

        this.mQueue.clear();
    }

    private static final class Messages {
        private static final int DISPLAY_SUPERTOAST = 4477780;
        private static final int ADD_SUPERTOAST = 4281172;
        private static final int REMOVE_SUPERTOAST = 5395284;

        private Messages() {
        }
    }
}

