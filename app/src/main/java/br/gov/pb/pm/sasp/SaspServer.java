package br.gov.pb.pm.sasp;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.snatik.storage.Storage;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SaspServer {

    public static final String VERSAO = "1.0";

    //public static final String HOST_WEBSERVICE = "app-v" + VERSAO + "/";

//    public static final String HOST_WEBSERVICE = "";
//    public static final String HOST_BASE = "http://10.0.2.2/sasp-php/";
//    public static final String HOST_BASE_DATA = "http://10.0.2.2/DATA/";

    public static final String HOST_WEBSERVICE = "";
    public static final String HOST_BASE = "http://172.16.150.26/sasp-php/";
    public static final String HOST_BASE_DATA = "http://172.16.150.26/DATA/";

    public static final String HOST_EXECUTAR = HOST_BASE + HOST_WEBSERVICE + "executar.php";

    private static final int OPT_SERVER_USER_IP = 11;
    private static final int OPT_SERVER_DATETIME = 12;
    private static final int OPT_SERVER_UPLOAD_OBJECT = 13;

    private static final int OPT_USUARIO_LOGIN = 101;
    private static final int OPT_USUARIO_CADASTRAR = 102;

    private static final int OPT_PESSOAS_ULTIMOS_CADASTROS = 201;
    private static final int OPT_PESSOAS_PERFIL = 202;
    private static final int OPT_PESSOAS_BUSCAR = 203;
    private static final int OPT_PESSOAS_CADASTRAR = 204;
    private static final int OPT_PESSOAS_MEUS_CADASTROS = 205;
    private static final int OPT_PESSOAS_BUSCAR_PESSOA = 206;
    private static final int OPT_PESSOAS_BUSCAR_PESSOA_SIMPLE = 207;
    private static final int OPT_PESSOAS_ATUALIZAR_PERFIL = 208;
    private static final int OPT_PESSOAS_BUSCAR_PESSOA_MARCA = 209;

    private static final int OPT_ABORDAGENS_ULTIMOS_CADASTROS = 301;
    private static final int OPT_ABORDAGENS_CADASTRAR = 302;
    private static final int OPT_ABORDAGENS_PERFIL = 303;
    private static final int OPT_ABORDAGENS_BUSCAR = 304;
    private static final int OPT_ABORDAGENS_MEUS_CADASTROS = 305;

    private static final int OPT_VEICULOS_CADASTRAR = 401;
    private static final int OPT_VEICULOS_PERFIL = 402;

    private static final int OPT_INFORMES_CADASTRAR = 501;
    private static final int OPT_INFORMES_MEUS_CADASTROS = 502;
    private static final int OPT_INFORMES_PERFIL = 503;

    private Storage storage;

    private static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
    private static Context context;

    public SaspServer(Context context) {

        this.context = context;
        this.storage = new Storage(context);

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

            if (AppUtils.DEBUG_MODE) Toast.makeText(context, "Erro ao carregar imagens.", Toast.LENGTH_SHORT).show();

            return;
        }

        globalRequest(OPT_USUARIO_CADASTRAR, params, responseHandler);
    }

    public void pessoasUltimosCadastros(int index, String date_time_max, SaspResponse responseHandler) {

        RequestParams params = new RequestParams();

        params.put("index", index);
        params.put("date_time_max", date_time_max);

        globalRequest(OPT_PESSOAS_ULTIMOS_CADASTROS, params, responseHandler);
    }

    public void pessoasPerfil(String id_pessoa, SaspResponse responseHandler) {

        RequestParams params = new RequestParams();

        params.put("id_pessoa", id_pessoa);

        globalRequest(OPT_PESSOAS_PERFIL, params, responseHandler);
    }

    public void pessoasMeusCadastros(int index, SaspResponse responseHandler) {

        RequestParams params = new RequestParams();

        params.put("index", index);

        globalRequest(OPT_PESSOAS_MEUS_CADASTROS, params, responseHandler);
    }

    public void pessoasBuscarPessoa(int index, SaspResponse responseHandler) {

        String[] data = DataHolder.getInstance().getBuscarPessoaData();

        RequestParams params = new RequestParams();

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

    public void pessoasBuscarPessoaMarca(int index, SaspResponse responseHandler) {

        String[] data = DataHolder.getInstance().getBuscarPessoaMarcaData();

        RequestParams params = new RequestParams();

        params.put("index", index);

        params.put("tipo_marca", data[0]);
        params.put("descricao", data[1]);
        params.put("parte_corpo", data[2]);

        globalRequest(OPT_PESSOAS_BUSCAR_PESSOA_MARCA, params, responseHandler);
    }

    public void pessoasBuscarPessoaSimple(int index, SaspResponse responseHandler) {

        String[] data = DataHolder.getInstance().getbuscarPessoaDataSimple();

        RequestParams params = new RequestParams();

        params.put("index", index);
        params.put("cpf_pessoa", AppUtils.limparCPF(data[0]));
        params.put("nome_completo", data[1]);

        globalRequest(OPT_PESSOAS_BUSCAR_PESSOA_SIMPLE, params, responseHandler);
    }

    public void pessoasAtualizarPerfil(String id_pessoa, String alcunha, String nome_completo, String nome_da_mae, String cpf, String rg, String data_nascimento, SaspResponse responseHandler) {

        RequestParams params = new RequestParams();

        params.put("id_pessoa", id_pessoa);
        params.put("alcunha", alcunha);
        params.put("nome_completo", nome_completo);
        params.put("nome_da_mae", nome_da_mae);
        params.put("cpf_pessoa", AppUtils.limparCPF(cpf));
        params.put("rg_pessoa", rg);
        params.put("data_nascimento", data_nascimento);


        globalRequest(OPT_PESSOAS_ATUALIZAR_PERFIL, params, responseHandler);
    }

    public void cadastrarPessoa(List<SaspImage> imageList, List<SaspImage> marcaList, List<String> marcaInfo, SaspResponse responseHandler) {

        RequestParams params = new RequestParams();

        DataHolder dh = DataHolder.getInstance();

        String[] infoPessoa = dh.getCadastrarPessoaData();

        params.put("nome_alcunha", infoPessoa[0]);
        params.put("nome_completo", infoPessoa[1]);
        params.put("crt_cor_pele", infoPessoa[2]);
        params.put("crt_cor_olhos", infoPessoa[3]);
        params.put("crt_cor_cabelos", infoPessoa[4]);
        params.put("crt_tipo_cabelos", infoPessoa[5]);
        params.put("crt_porte_fisico", infoPessoa[6]);
        params.put("crt_estatura", infoPessoa[7]);
        params.put("crt_deficiente", infoPessoa[8]);
        params.put("crt_tatuagem", infoPessoa[9]);
        params.put("relato", infoPessoa[10]);
        params.put("historico_criminal", infoPessoa[11]);
        params.put("areas_de_atuacao", infoPessoa[12]);
        params.put("nome_da_mae", infoPessoa[13]);
        params.put("cpf_pessoa", infoPessoa[14]);
        params.put("rg_pessoa", infoPessoa[15]);
        params.put("data_nascimento", infoPessoa[16]);

        params.put("img_busca", imageList.get(0).getImgBusca().getName());
        params.put("img_principal", imageList.get(0).getImgPrincipal().getName());

        if (imageList.size() > 1) {

            for (int i = 1; i < imageList.size(); i++) {

                SaspImage si = imageList.get(i);

                String param_busca = String.format("imagens[%d][img_busca]", i-1);
                params.put(param_busca, si.getImgBusca().getName());

                String param_principal = String.format("imagens[%d][img_principal]", i-1);
                params.put(param_principal, si.getImgPrincipal().getName());
            }
        }

        if (marcaList.size() > 0) {

            for (int i = 0; i < marcaList.size(); i++) {

                SaspImage si = marcaList.get(i);
                String[] marca_info = marcaInfo.get(i).split("#%#");

                String param_busca = String.format("marcas[%d][img_busca]", i);
                params.put(param_busca, si.getImgBusca().getName());

                String param_principal = String.format("marcas[%d][img_principal]", i);
                params.put(param_principal, si.getImgPrincipal().getName());

                String param_marca_tipo = String.format("marcas[%d][marca_tipo]", i);
                params.put(param_marca_tipo, marca_info[0]);

                String param_tatuagem_tipo = String.format("marcas[%d][descricao]", i);
                params.put(param_tatuagem_tipo, marca_info[1]);

                String param_parte_corpo = String.format("marcas[%d][parte_corpo]", i);
                params.put(param_parte_corpo, marca_info[2]);
            }
        }

        globalRequest(OPT_PESSOAS_CADASTRAR, params, responseHandler);
    }

    public void abordagensUltimosCadastros(int index, String date_time_max, SaspResponse responseHandler) {

        RequestParams params = new RequestParams();

        params.put("index", index);
        params.put("date_time_max", date_time_max);

        globalRequest(OPT_ABORDAGENS_ULTIMOS_CADASTROS, params, responseHandler);
    }

    public void abordagensCadastrar(List<SaspImage> imageList, List<String> pessoaList, List<String> veiculoList, List<String> matriculaList, String relato, SaspResponse responseHandler) {

        String lat = DataHolder.getInstance().getCadastroAbordagemLatitude();
        String lon = DataHolder.getInstance().getCadastroAbordagemLongitude();
        String cpf = DataHolder.getInstance().getLoginDataItem("cpf");

        RequestParams params = new RequestParams();

        params.put("cpf_usuario", AppUtils.limparCPF(cpf));
        params.put("latitude", lat);
        params.put("longitude", lon);
        params.put("relato", relato);
        params.put("img_busca", imageList.get(0).getImgBusca().getName());
        params.put("img_principal", imageList.get(0).getImgPrincipal().getName());

        if (imageList.size() > 1) {

            for (int i = 1; i < imageList.size(); i++) {

                SaspImage si = imageList.get(i);

                String param_busca = String.format("imagens[%d][img_busca]", i-1);
                params.put(param_busca, si.getImgBusca().getName());

                String param_principal = String.format("imagens[%d][img_principal]", i-1);
                params.put(param_principal, si.getImgPrincipal().getName());
            }
        }

        if (pessoaList.size() > 0) {

            for (int i = 0; i < pessoaList.size(); i++) {

                String p1= String.format("pessoas[%d][id_pessoa]", i);
                params.put(p1, pessoaList.get(i));
            }
        }

        if (veiculoList.size() > 0) {

            for (int i = 0; i < veiculoList.size(); i++) {

                String p1= String.format("veiculos[%d][id_veiculo]", i);
                params.put(p1, veiculoList.get(i));
            }
        }

        if (matriculaList.size() > 0) {

            for (int i = 0; i < matriculaList.size(); i++) {

                String p1= String.format("matriculas[%d][matricula]", i);
                params.put(p1, AppUtils.limparMatricula(matriculaList.get(i)));
            }
        }

        globalRequest(OPT_ABORDAGENS_CADASTRAR, params, responseHandler);
    }

    public void informesCadastrar(String usuario_latitude, String usuario_longitude, int natureza, int area_opm, String municipio, List<SaspImage> imageList, List<String> pessoaList, List<String> veiculoList, String informe, String senha, SaspResponse responseHandler) {

        String lat = DataHolder.getInstance().getCadastroAbordagemLatitude();
        String lon = DataHolder.getInstance().getCadastroAbordagemLongitude();

        String cpf = DataHolder.getInstance().getLoginDataItem("cpf");

        RequestParams params = new RequestParams();

        params.put("cpf_usuario", AppUtils.limparCPF(cpf));
        params.put("natureza", natureza);
        params.put("area_opm", area_opm);
        params.put("municipio", municipio);
        params.put("latitude", lat);
        params.put("longitude", lon);
        params.put("usuario_latitude", usuario_latitude);
        params.put("usuario_longitude", usuario_longitude);
        params.put("informe", informe);
        params.put("senha", senha);

        if (imageList.size() > 0) {

            for (int i = 0; i < imageList.size(); i++) {

                SaspImage si = imageList.get(i);

                String param_busca = String.format("imagens[%d][img_busca]", i);
                params.put(param_busca, si.getImgBusca().getName());

                String param_principal = String.format("imagens[%d][img_principal]", i);
                params.put(param_principal, si.getImgPrincipal().getName());
            }
        }

        if (pessoaList.size() > 0) {

            for (int i = 0; i < pessoaList.size(); i++) {

                if (pessoaList.get(i).contains("#%#")) {

                    String[] parts = pessoaList.get(i).split("#%#");

                    String p1 = String.format("pessoas[%d][id_pessoa]", i);
                    params.put(p1, "0");
                    String p2 = String.format("pessoas[%d][servidor_estadual]", i);
                    params.put(p2, "1");
                    String p3 = String.format("pessoas[%d][servidor_matricula]", i);
                    params.put(p3, AppUtils.limparMatricula(parts[0]));
                    String p4 = String.format("pessoas[%d][servidor_nome]", i);
                    params.put(p4, parts[1]);
                    String p5 = String.format("pessoas[%d][servidor_municipio]", i);
                    params.put(p5, parts[2]);
                }
                else {

                    String p1 = String.format("pessoas[%d][id_pessoa]", i);
                    params.put(p1, pessoaList.get(i));
                }
            }
        }

        if (veiculoList.size() > 0) {

            for (int i = 0; i < veiculoList.size(); i++) {

                String p1= String.format("veiculos[%d][id_veiculo]", i);
                params.put(p1, veiculoList.get(i));
            }
        }

        globalRequest(OPT_INFORMES_CADASTRAR, params, responseHandler);
    }

    public void abordagensMeusCadastros(int index, SaspResponse responseHandler) {

        RequestParams params = new RequestParams();

        params.put("index", index);

        globalRequest(OPT_ABORDAGENS_MEUS_CADASTROS, params, responseHandler);
    }

    public void informesMeusCadastros(int index, String senha, SaspResponse responseHandler) {

        RequestParams params = new RequestParams();

        params.put("index", index);
        params.put("senha", senha);

        globalRequest(OPT_INFORMES_MEUS_CADASTROS, params, responseHandler);
    }

    public void informesPerfil(String id_informe, SaspResponse responseHandler) {

        RequestParams params = new RequestParams();

        params.put("id_informe", id_informe);

        globalRequest(OPT_INFORMES_PERFIL, params, responseHandler);
    }

    public void abordagensPerfil(String id_abordagem, SaspResponse responseHandler) {

        RequestParams params = new RequestParams();

        params.put("id_abordagem", id_abordagem);

        globalRequest(OPT_ABORDAGENS_PERFIL, params, responseHandler);
    }

    public void abordagensPerfilVeiculo(String id_veiculo, SaspResponse responseHandler) {

        RequestParams params = new RequestParams();

        params.put("id_veiculo", id_veiculo);

        globalRequest(OPT_VEICULOS_PERFIL, params, responseHandler);
    }

    public void abordagensBuscarAbordagem(SaspResponse responseHandler) {

        String[] buscaData = DataHolder.getInstance().getBuscarAbordagemData();

        RequestParams params = new RequestParams();

        params.put("latitude", buscaData[0]);
        params.put("longitude", buscaData[1]);
        params.put("nome_alcunha", buscaData[2]);
        params.put("distancia_maxima", buscaData[3]);

        globalRequest(OPT_ABORDAGENS_BUSCAR, params, responseHandler);
    }

    public void abordagensCadastrarVeiculo(String placa, String tipo_placa, String categoria, List<SaspImage> imageList, String descricao, SaspResponse responseHandler) {

        RequestParams params = new RequestParams();

        params.put("placa", placa);
        params.put("tipo_placa", tipo_placa);
        params.put("categoria", categoria);
        params.put("descricao", descricao);

        if (imageList.size() > 0) {

            for (int i = 0; i < imageList.size(); i++) {

                SaspImage si = imageList.get(i);

                String param_busca = String.format("imagens[%d][img_busca]", i);
                params.put(param_busca, si.getImgBusca().getName());

                String param_principal = String.format("imagens[%d][img_principal]", i);
                params.put(param_principal, si.getImgPrincipal().getName());
            }
        }

        globalRequest(OPT_VEICULOS_CADASTRAR, params, responseHandler);
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

    public List<File> getUploadObjects() {

        return storage.getFiles(storage.getInternalFilesDirectory() + File.separator + SaspImage.UPLOAD_OBJECT_FOLDER, ".*\\.json$");
    }

    public void uploadObject(String modulo, File imgBusca, File imgPrincipal, SaspResponse responseHandler) {

        if (!storage.isFileExist(imgBusca.getPath()) || !storage.isFileExist(imgPrincipal.getPath())) {

            return;
        }

        RequestParams params = new RequestParams();

        params.put("modulo", modulo);

        try {

            params.put("img_busca", imgBusca);
            params.put("img_principal", imgPrincipal);
        }
        catch (Exception e) {

            if (AppUtils.DEBUG_MODE) Toast.makeText(context, "Erro ao carregar imagens.", Toast.LENGTH_SHORT).show();

            return;
        }

        globalRequest(OPT_SERVER_UPLOAD_OBJECT, params, responseHandler);
    }

    private void globalRequest(int demanda, RequestParams params, AsyncHttpResponseHandler responseHandler) {

        params.put("plataforma", "Android");
        params.put("device", getAparelhoId());
        params.put("network", getNetworkType());
        params.put("versao", VERSAO);
        params.put("demanda", demanda);

        if (demanda > 200) {

            params.put("token", DataHolder.getInstance().getLoginDataItem("token"));
        }

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

    public static void startServiceUploadImages(Context ctx) {

        Intent i = new Intent(ctx, SaspServiceUploadImages.class);
        ctx.startService(i);
    }
}
