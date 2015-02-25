package com.findmycoach.mentor;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by praka_000 on 2/24/2015.
 */
public class BackgroundService extends Service {
    public static final String TAG = "FMC";

    private static final String BROADCAST="com.google.android.c2dm.intent.RECEIVE";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "inside mentor BackgroundService");

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Log.d(TAG, "Inside mentor Background Service thread");
                    IntentFilter intentFilter = new IntentFilter(BROADCAST);
                    registerReceiver( new GcmBroadcastReceiver() , intentFilter);

                    Intent intent = new Intent(BROADCAST);
                    intent.putExtra("NOT_GCM","true");
                    sendBroadcast(intent);


                    try {
                        Thread.sleep(10000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();

        return START_STICKY;
    }

    public class MyBinder extends Binder {
        public BackgroundService getService() {
            return BackgroundService.this;
        }
    }

    private final IBinder mBinder = new MyBinder();

    /*private BackgroundService mBoundService;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mBoundService = ((MyBinder)service).getService();

            // Tell the user about this for our demo.
            Toast.makeText(Binding.this, R.string.local_service_connected,
                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mBoundService = null;
            Toast.makeText(Binding.this, R.string.local_service_disconnected,
                    Toast.LENGTH_SHORT).show();
        }
    };

    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        bindService(new Intent(Binding.this,
                LocalService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }
*/

}
