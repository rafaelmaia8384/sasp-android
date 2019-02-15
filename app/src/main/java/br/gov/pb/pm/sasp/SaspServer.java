package br.gov.pb.pm.sasp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.File;

public class SaspServer {

    public static final String VERSAO = "1.0";
    //public static final String HOST_WEBSERVICE = "app-v" + VERSAO + "/";
    public static final String HOST_WEBSERVICE = "";
    public static final String HOST_BASE = "http://10.0.2.2/sasp-php/";
    public static final String HOST_BASE_DATA = "http://10.0.2.2/DATA/";

    public static final String HOST_EXECUTAR = HOST_BASE + HOST_WEBSERVICE + "executar.php";

    private static final String OPT_SERVER_USER_IP = "11";
    private static final String OPT_SERVER_DATETIME = "12";

    private static final String OPT_USUARIO_LOGIN = "101";
    private static final String OPT_USUARIO_CADASTRAR = "102";

    private static final String OPT_PESSOAS_ULTIMOS_CADASTROS = "201";
    private static final String OPT_PESSOAS_PERFIL = "202";
    private static final String OPT_PESSOAS_PERFIL_IMAGENS = "203";
    private static final String OPT_PESSOAS_PERFIL_COMENTARIOS = "204";
    private static final String OPT_PESSOAS_BUSCAR = "205";
    private static final String OPT_PESSOAS_CADASTRAR = "206";
    private static final String OPT_PESSOAS_MEUS_CADASTROS = "207";
    private static final String OPT_PESSOAS_BUSCAR_PESSOA = "208";

    private static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
    private static Context context;

    public SaspServer(Context context) {

        this.context = context;
        client.setUserAgent(getUserAgent());
    }

    public void usuarioLogin(String cpf, String senha, String imei, String mac, AsyncHttpResponseHandler responseHandler) {

        RequestParams params = new RequestParams();

        params.put("cpf", AppUtils.limparCPF(cpf));
        params.put("senha", senha);
        params.put("imei", imei);
        params.put("mac", mac);

        globalRequest(OPT_USUARIO_LOGIN, params, responseHandler);
    }

    public void usuarioCadastrar(String cpf, String matricula, String email, String telefone, String senha, String id_aparelho, String imei, String mac, SaspImage saspImage, SaspResponse responseHandler) {

        RequestParams params = new RequestParams();

        params.put("cpf", AppUtils.limparCPF(cpf));
        params.put("matricula", AppUtils.limparMatricula(matricula));
        params.put("email", email);
        params.put("telefone", telefone);
        params.put("senha", senha);
        params.put("id_aparelho", id_aparelho);
        params.put("imei", imei);
        params.put("mac", mac);

        try {

            params.put("img_busca", saspImage.getImgBusca());
            params.put("img_principal", saspImage.getImgPrincipal());
        }
        catch (Exception e) {

            Toast.makeText(context, "Erro ao carregar imagens.", Toast.LENGTH_SHORT).show();

            return;
        }

        globalRequest(OPT_USUARIO_CADASTRAR, params, responseHandler);
    }

    public void pessoasUltimosCadastros(int index, String date_time_max, SaspResponse responseHandler) {

        RequestParams params = new RequestParams();

        params.put("token", DataHolder.getInstance().getLoginDataItem("token"));
        params.put("index", index);
        params.put("date_time_max", date_time_max);

        globalRequest(OPT_PESSOAS_ULTIMOS_CADASTROS, params, responseHandler);
    }

    public void pessoasPerfil(String id_pessoa, SaspResponse responseHandler) {

        RequestParams params = new RequestParams();

        params.put("token", DataHolder.getInstance().getLoginDataItem("token"));
        params.put("id_pessoa", id_pessoa);

        globalRequest(OPT_PESSOAS_PERFIL, params, responseHandler);
    }

    public void pessoasPerfilImagens(String id_pessoa, SaspResponse responseHandler) {

        RequestParams params = new RequestParams();

        params.put("token", DataHolder.getInstance().getLoginDataItem("token"));
        params.put("id_pessoa", id_pessoa);

        globalRequest(OPT_PESSOAS_PERFIL_IMAGENS, params, responseHandler);
    }

    public void pessoasPerfilComentarios(int index, String id_pessoa, SaspResponse responseHandler) {

        RequestParams params = new RequestParams();

        params.put("token", DataHolder.getInstance().getLoginDataItem("token"));
        params.put("index", index);
        params.put("id_pessoa", id_pessoa);

        globalRequest(OPT_PESSOAS_PERFIL_COMENTARIOS, params, responseHandler);
    }

    public void pessoasMeusCadastros(int index, SaspResponse responseHandler) {

        RequestParams params = new RequestParams();

        params.put("token", DataHolder.getInstance().getLoginDataItem("token"));
        params.put("index", index);

        globalRequest(OPT_PESSOAS_MEUS_CADASTROS, params, responseHandler);
    }

    public void pessoasBuscarPessoa(int index, SaspResponse responseHandler) {

        String[] data = DataHolder.getInstance().getBuscarPessoaData();

        RequestParams params = new RequestParams();

        params.put("token", DataHolder.getInstance().getLoginDataItem("token"));
        params.put("index", index);

        params.put("nome_alcunha", data[0]);
        params.put("areas_de_atuacao", data[1]);
        params.put("historico_criminal", data[2]);
        params.put("crt_cor_pele", data[3]);
        params.put("crt_cor_olhos", data[4]);
        params.put("crt_cor_cabelos", data[5]);
        params.put("crt_tipo_cabelos", data[6]);
        params.put("crt_porte_fisico", data[7]);
        params.put("crt_estatura", data[8]);
        params.put("crt_deficiente", data[9]);
        params.put("crt_tatuagem", data[10]);

        globalRequest(OPT_PESSOAS_BUSCAR_PESSOA, params, responseHandler);
    }

    public void saspServerDateTime(SaspResponse responseHandler) {

        RequestParams params = new RequestParams();

        params.put("null", 0);

        globalRequest(OPT_SERVER_DATETIME, params, responseHandler);
    }

    public void saspServerUserIP(SaspResponse responseHandler) {

        RequestParams params = new RequestParams();

        params.put("null", 0);

        globalRequest(OPT_SERVER_USER_IP, params, responseHandler);
    }

    private void globalRequest(String demanda, RequestParams params, AsyncHttpResponseHandler responseHandler) {

        params.put("plataforma", "Android");
        params.put("device", getAparelhoId());
        params.put("network", getNetworkType());
        params.put("versao", VERSAO);
        params.put("demanda", demanda);

        client.post(HOST_EXECUTAR, params, responseHandler);
    }

    private String getNetworkType() {

        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null) {

            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {

                return "Wifi";
            }
            else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {

                return "Mobile";
            }
        }

        return "Unknown";
    }

    public String getServerImagePath(String modulo, String imgName, boolean isBusca) {

        return HOST_BASE + "data/sasp-img/" + modulo + (isBusca ? "/busca/" : "/principal/") + imgName;
    }

    public String getDeviceIMEI() {

        String imei;

        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                imei = tm.getImei();
            }
            else {

                imei = tm.getDeviceId();
            }
        }
        else {

            imei = "000000000000000";
        }

        return imei;
    }

    private String getUserAgent() {

        return "SASP - Sistema de Apoio ao Serviço Policial - PMPB.";
    }

    public static String getAparelhoId() {

        String id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        if (id == null) {

            id = "ID-APARELHO-NULL";
        }

        return id;
    }

    public static String getImageAddress(String img, String modulo, boolean isBusca) {

        return HOST_BASE_DATA + "sasp-img/" + modulo + (isBusca ? "/busca/" : "/principal/") + img;
    }
}
