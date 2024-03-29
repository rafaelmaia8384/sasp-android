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

import com.snatik.storage.Storage;

import org.json.JSONObject;

import java.io.File;
import java.util.List;

public class SaspServiceUploadImages extends Service {

    private final static int TIME_INTERVAL = 10 * 1000; //Checar objetos para upload a cada 10 segundos
    private final static int TENTATIVAS_MAX = 10;       //Tentar enviar no máximo 10 vezes, senão excluir.

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

                final List<File> list = saspServer.getUploadObjects();

                if (list != null && list.size() > 0) {

                    for (int i = 0; i < list.size(); i++) {

                        String content = storage.readTextFile(list.get(i).getPath());

                        try {

                            final JSONObject json = new JSONObject(content);

                            final boolean enviando = json.getBoolean("enviando");
                            final int tentativas = json.getInt("tentativas");
                            final String modulo = json.getString("modulo");
                            final String img_busca = json.getString("img_busca");
                            final String img_principal = json.getString("img_principal");

                            if (enviando) {

                                continue;
                            }

                            final File imgBusca = new File(list.get(i).getParent() + File.separator + img_busca);
                            final File imgPrincipal = new File(list.get(i).getParent() + File.separator + img_principal);

                            if (tentativas >= TENTATIVAS_MAX) {

                                if (imgBusca.exists()) imgBusca.delete();
                                if (imgPrincipal.exists()) imgPrincipal.delete();

                                list.get(i).delete();
                            }
                            else {

                                final int a = i;

                                json.put("enviando", true);
                                storage.createFile(list.get(a).getPath(), json.toString());

                                saspServer.uploadObject(modulo, imgBusca, imgPrincipal, new SaspResponse(getApplicationContext()) {

                                    @Override
                                    void onSaspResponse(String error, String msg, JSONObject extra) {

                                        if (error.equals("1")) {

                                            //erro no envio, acrescentar o número de tentativas

                                            try {

                                                json.put("enviando", false);
                                                json.put("tentativas", tentativas + 1);
                                                storage.createFile(list.get(a).getPath(), json.toString());
                                            }
                                            catch (Exception e) { }

                                            if (AppUtils.DEBUG_MODE) Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                        }
                                        else {

                                            //enviado com sucesso, deletar arquivos

                                            if (imgBusca.exists()) imgBusca.delete();
                                            if (imgPrincipal.exists()) imgPrincipal.delete();

                                            list.get(a).delete();
                                        }
                                    }

                                    @Override
                                    void onResponse(String error) {

                                        try {

                                            json.put("enviando", false);
                                            storage.createFile(list.get(a).getPath(), json.toString());
                                        }
                                        catch (Exception e) { }

                                        if (AppUtils.DEBUG_MODE) Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    void onNoResponse(String error) {

                                        try {

                                            json.put("enviando", false);
                                            storage.createFile(list.get(a).getPath(), json.toString());
                                        }
                                        catch (Exception e) { }

                                        if (AppUtils.DEBUG_MODE) Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    void onPostResponse() {

                                    }
                                });
                            }
                        }
                        catch (Exception e) {

                            if (AppUtils.DEBUG_MODE) Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
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
