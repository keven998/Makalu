package com.lv.im;

import android.util.Log;

import com.lv.Listener.DequeueListener;
import com.lv.Listener.FetchListener;
import com.lv.utils.Config;
import com.lv.bean.Message;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;


public class LazyQueue {
    long max_time = 500;
    private static LazyQueue instance;
    static Map<String, SortList> LazyMap = new ConcurrentHashMap<>();
    static Map<String, SortList> TempMap = new ConcurrentHashMap<>();
//  static Map<String, SortList> LazyMap = new  HashMap<>();
//  static Map<String, SortList> TempMap = new  HashMap<>();
    boolean isRunning;
    DequeueListener listener;
    Timer timer;
    boolean isTempDequeue;
    private LazyQueue() {
    }

    public static LazyQueue getInstance() {
        if (instance == null) {
            instance = new LazyQueue();
        }
        return instance;
    }

    public void setDequeueListener(DequeueListener listener) {
        this.listener = listener;
    }

    public void begin() {
        if (Config.isDebug) {
            Log.i(Config.TAG, "isRunning " + isRunning);
        }
        if (isRunning) {
            if (Config.isDebug) {
                Log.i(Config.TAG, "return ");
            }
            return;
        }
        isRunning = true;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                isRunning = false;
                new DequeueThread().start();
            }
        }, new Date(System.currentTimeMillis() + max_time));
    }

    class DequeueThread extends Thread {
        @Override
        public void run() {
            Dequeue();
        }
    }

    /**
     * 消息重组后出队
     */
    private void Dequeue() {
        for (Map.Entry<String, SortList> entry : LazyMap.entrySet()) {
            SortList list = entry.getValue();
            while (list.size() > 0) {
                if (Config.isDebug) {
                    Log.i(Config.TAG, "size :" + list.size());
                }
                try {
                    Message message = list.deleteFirst();
                    if (IMClient.getInstance().isBLOCK()) {
                        if (Config.isDebug) {
                            Log.i(Config.TAG, "dequeue block " + message.getContents());
                        }
                        add2Temp(message.getConversation(), message);
                    } else {
                        if (checkOrder(message)) {
                            listener.onDequeueMsg(message);
                        } else {
                            add2Temp(message.getConversation(), message);
                            IMClient.getInstance().setBLOCK(true);
                            IMClient.getInstance().ackAndFetch(flistener);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (Config.isDebug) {
            Log.i(Config.TAG, "Dequeue() ");
        }
        if (!isTempDequeue){
            TempDequeue();
        }else {
           Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                   while (true){
                       if (!isTempDequeue){
                           TempDequeue();
                           break;
                       }
                   }
                }
            }, new Date(System.currentTimeMillis() + 1000));
        }
    }

    FetchListener flistener = (list) -> {
        for (Message msg : list) {
            add2Temp(msg.getConversation(), msg);
        }
        TempDequeue();
    };

    /**
     * Block队列出队
     */
    public void TempDequeue() {
        isTempDequeue = true;
        for (Map.Entry<String, SortList> entry : TempMap.entrySet()) {
            SortList list = entry.getValue();
            while (list.size() > 0) {
                Message message = list.deleteFirst();
                if (message != null) {
                    if (Config.isDebug) {
                        Log.i(Config.TAG, "list size : " +
                                "" + list.size() + "" +
                                " tempDequeue block " + message.getContents());
                    }
                    listener.onDequeueMsg(message);
                }
            }
        }
        if (Config.isDebug) {
            Log.i(Config.TAG, "TempDequeue()");
        }
        IMClient.getInstance().setBLOCK(false);
        isTempDequeue=false;
    }

    public void fetchDequeue(List<Message>list ){
        while (true){
            if (!IMClient.getInstance().isBLOCK()){
                for (Message message : list) {
                    add2Temp(message.getConversation(),message);
                }
                TempDequeue();
                break;
            }
        }
    }

    /**
     * 加入队列
     * @param conversation key
     * @param messageBean 消息体
     */
    public void addMsg(String conversation, Message messageBean) {

        if (!LazyMap.containsKey(conversation)) {
            LazyMap.put(conversation, new SortList());
        }
        LazyMap.get(conversation).insert(messageBean);
        begin();
    }

    /**
     * 加入Block队列
     * @param conversation key
     * @param messageBean 消息体
     */
    public void add2Temp(String conversation, Message messageBean) {

        if (!TempMap.containsKey(conversation)) {
            TempMap.put(conversation, new SortList());
        }
        TempMap.get(conversation).insert(messageBean);
    }

    /**
     * 丢失消息检查
     * @param messageBean 消息体
     * @return 是否连续
     */
    private boolean checkOrder(Message messageBean) {
        int lastid = IMClient.getInstance().getLastMsg(messageBean.getConversation());
        if (Config.isDebug) {
            Log.i(Config.TAG, "lastId  " + lastid + " messageBean: " + messageBean.getMsgId());
        }
        if (lastid == -1) {
            if (Config.isDebug) {
                Log.i(Config.TAG, "checkOrder:first msg ");
            }
            return true;
        } else if (messageBean.getMsgId() - 1 == lastid||messageBean.getMsgId()==lastid) {
            if (Config.isDebug) {
                Log.i(Config.TAG, "checkOrder:正序 ");
            }
            return true;
        } else {
            if (Config.isDebug) {
                Log.i(Config.TAG, "checkOrder:乱序 ");
            }
            return false;
        }
    }
}
