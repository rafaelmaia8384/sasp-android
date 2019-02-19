package br.gov.pb.pm.sasp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.snatik.storage.Storage;

import org.json.JSONObject;

import java.io.File;
import java.util.List;

public class SaspServiceUploadImages extends Service {

    private final static int TIME_INTERVAL = 5000; // 5 segundos

    SaspServer saspServer;
    Storage storage;

    Handler handler;
    Runnable runnable;

    @Override
    public void onCreate() {

        saspServer = new SaspServer(getApplicationContext());
        storage = new Storage(getApplicationContext());

        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {

            @Override
            public void run() {

                if (1 == 1) return;

                final List<File> list = saspServer.saspServerGetUploadObjects();

                if (list != null && list.size() > 0) {

                    for (int i = 0; i < 1/*list.size()*/; i++) {

                        String content = storage.readTextFile(list.get(i).getPath());

                        try {

                            final JSONObject json = new JSONObject(content);

                            final int tentativas = json.getInt("tentativas");
                            final String modulo = json.getString("modulo");
                            final String img_busca = json.getString("img_busca");
                            final String img_principal = json.getString("img_principal");

                            if (tentativas > 5) {

                                File imgBusca = new File(list.get(i).getParent() + File.separator + img_busca);
                                File imgPrincipal = new File(list.get(i).getParent() + File.separator + img_principal);

                                if (imgBusca.exists()) imgBusca.delete();
                                if (imgPrincipal.exists()) imgPrincipal.delete();

                                list.get(i).delete();
                            }
                            else {

                                final int a = i;

                                saspServer.saspServerUploadObject(modulo, img_busca, img_principal, new SaspResponse(getApplicationContext()) {

                                    @Override
                                    void onSaspResponse(String error, String msg, JSONObject extra) {

                                        if (error.equals("1")) {

                                            //erro no envio, acrescentar o número de tentativas

                                            try {

                                                json.put("tentativas", tentativas + 1);

                                                storage.createFile(list.get(a).getPath(), json.toString());
                                            }
                                            catch (Exception e) {}

                                            Toast.makeText(getApplicationContext(), "" + tentativas, Toast.LENGTH_SHORT).show();
                                        }
                                        else {

                                            //enviado com sucesso, deletar arquivos

                                            File imgBusca = new File(list.get(a).getParent() + File.separator + img_busca);
                                            File imgPrincipal = new File(list.get(a).getParent() + File.separator + img_principal);

                                            if (imgBusca.exists()) imgBusca.delete();
                                            if (imgPrincipal.exists()) imgPrincipal.delete();

                                            list.get(a).delete();
                                        }
                                    }

                                    @Override
                                    void onResponse(String error) {

                                    }

                                    @Override
                                    void onNoResponse(String error) {

                                    }

                                    @Override
                                    void onPostResponse() {

                                    }
                                });
                            }
                        }
                        catch (Exception e) { }
                    }
                }

                handler.postDelayed(this, TIME_INTERVAL);
            }
        };

        handler.postDelayed(runnable, TIME_INTERVAL);

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }
}
