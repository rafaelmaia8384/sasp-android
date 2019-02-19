package br.gov.pb.pm.sasp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SaspBootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        SaspServer.startServiceUploadImages(context);
    }
}