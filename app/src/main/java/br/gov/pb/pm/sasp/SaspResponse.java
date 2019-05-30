package br.gov.pb.pm.sasp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;

public abstract class SaspResponse extends AsyncHttpResponseHandler {

    private Context context;

    protected SaspResponse(Context context) {

        this.context = context;
    }

    @Override
    public void onPreProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {

        try {

            Thread.sleep(500);
        }
        catch (Exception e) { }

        super.onPreProcessResponse(instance, response);
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

        String response = "";
        JSONObject json = null;

        try {

            response = new String(responseBody, "UTF-8");
            json = new JSONObject(response);

            if (json.getString("error").equals("1") && json.getString("msg").equals("Sessão expirada.")) {

                if (!DataHolder.getInstance().isLoginActivityVisible()) {

                    Intent i = new Intent(context, LoginActivity.class);
                    i.putExtra("SESSION_EXPIRED", "TRUE");
                    context.startActivity(i);

                    ((Activity)context).finishAffinity();

                }

                return;
            }

            onSaspResponse(json.getString("error"), json.getString("msg"), json.getString("extra").equals("null") ? null : new JSONObject(json.getString("extra")));
        }
        catch (Exception e) {

            if (AppUtils.DEBUG_MODE) {

                onResponse(response + "\n\n" + e.getLocalizedMessage());
            }
            else {

                onResponse(response);
            }
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

        onNoResponse("Não foi possível conectar com o servidor." + (AppUtils.DEBUG_MODE ? "\n\n" + error : ""));
    }

    @Override
    public void onFinish() {

        onPostResponse();

        super.onFinish();
    }

    abstract void onSaspResponse(String error, String msg, JSONObject extra);
    abstract void onResponse(String error);
    abstract void onNoResponse(String error);
    abstract void onPostResponse();
}