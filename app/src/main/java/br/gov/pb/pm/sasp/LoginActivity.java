package br.gov.pb.pm.sasp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONObject;

public class LoginActivity extends SaspActivity {

    private static final int CODE_PERMISSION_REQUEST_PHONE_STATE = 100;
    private static final int CODE_PERMISSION_REQUEST_WIFI_STATE = 101;
    private static final int CODE_PERMISSION_REQUEST_NETWORK_STATE = 102;
    private static final int CODE_PERMISSION_REQUEST_READ_EXTERNAL = 103;
    private static final int CODE_PERMISSION_REQUEST_WRITE_EXTERNAL = 104;
    private static final int CODE_PERMISSION_REQUEST_CAMERA = 105;
    private static final int CODE_PERMISSION_REQUEST_FINE_LOCATION = 106;

    public static final String messagePermissions = "Para continuar, você deve autorizar o SASP a utilizar algumas funções do seu aparelho. Deseja fazer isso agora?";
    public static final String messageRationale = "Você precisa habilitar as permissões do aplicativo diretamente nas configurações do Android.";

    private TextWatcher mascaraCPF;

    private EditText editTextCPF;
    private EditText editTextSenha;

    private DialogHelper dialogHelper;
    private SaspServer saspServer;

    private boolean askForLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        dialogHelper = new DialogHelper(this);
        saspServer = new SaspServer(this);

        SaspServer.startServiceUploadImages(getApplicationContext());

        editTextCPF = findViewById(R.id.editTextCPF);
        editTextSenha = findViewById(R.id.editTextSenha);

        mascaraCPF = MascaraCPF.insert("###.###.###-##", editTextCPF);
        editTextCPF.addTextChangedListener(mascaraCPF);

        ((Button)findViewById(R.id.buttonEntrar)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                askForLogin = true;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (!confirmAllPermissions()) {

                        dialogHelper.confirmDialog(false, "Solicitação de permissão", messagePermissions, "Não", new MaterialDialog.SingleButtonCallback() {

                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

                                    ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, CODE_PERMISSION_REQUEST_PHONE_STATE);
                                }
                                else if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {

                                    ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_WIFI_STATE}, CODE_PERMISSION_REQUEST_WIFI_STATE);
                                }
                                else if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {

                                    ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, CODE_PERMISSION_REQUEST_NETWORK_STATE);
                                }
                                else if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                                    ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CODE_PERMISSION_REQUEST_READ_EXTERNAL);
                                }
                                else if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                                    ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CODE_PERMISSION_REQUEST_WRITE_EXTERNAL);
                                }
                                else if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                                    ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.CAMERA}, CODE_PERMISSION_REQUEST_CAMERA);
                                }
                                else {

                                    ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, CODE_PERMISSION_REQUEST_FINE_LOCATION);
                                }
                            }
                        });
                    }
                    else {

                        solicitarLogin();
                    }
                }
                else {

                    solicitarLogin();
                }
            }
        });
    }

    private void solicitarLogin() {

        final String cpf = editTextCPF.getText().toString();
        final String senha = editTextSenha.getText().toString();

        if (!AppUtils.validarCPF(cpf)) {

            dialogHelper.showError("Verifique o CPF.");

            return;
        }

        if (senha.length() < 6 || senha.length() > 20) {

            dialogHelper.showError("A senha deve conter entre 6 e 20 dígitos.");

            return;
        }

        dialogHelper.showProgress();

        DataHolder.getInstance().setUserIMEI(saspServer.getDeviceIMEI());
        DataHolder.getInstance().setUserMAC(AppUtils.getMacAddr());

        saspServer.usuarioLogin(AppUtils.limparCPF(cpf), senha, DataHolder.getInstance().getUserIMEI(), DataHolder.getInstance().getUserMAC(), new SaspResponse(LoginActivity.this) {

            @Override
            void onSaspResponse(String error, String msg, JSONObject extra) {

                if (error.equals("0")) {

                    saspConfig.setConfigLoginCPF(cpf);

                    if (((CheckBox)findViewById(R.id.checkSalvarSenha)).isChecked()) {

                        saspConfig.setConfigLoginSenha(senha);
                    }
                    else {

                        saspConfig.removeConfigLoginSenha();
                    }

                    try {

                        DataHolder.getInstance().setLoginData(extra);
                    }
                    catch (Exception e) { }

                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
                else {

                    if (msg.equals("Solicitação de acesso em análise.")) {

                        saspConfig.setConfigLoginCPF(cpf);

                        if (((CheckBox)findViewById(R.id.checkSalvarSenha)).isChecked()) {

                            saspConfig.setConfigLoginSenha(senha);
                        }
                        else {

                            saspConfig.removeConfigLoginSenha();
                        }

                        new MaterialDialog.Builder(LoginActivity.this)
                                .customView(R.layout.layout_aviso_sasp_solicitacao_analise, true)
                                .positiveText("OK")
                                .canceledOnTouchOutside(false)
                                .cancelable(false)
                                .show();
                    }
                    else if (msg.equals("Solicitação de acesso negada.")) {

                        saspConfig.setConfigLoginCPF(cpf);

                        if (((CheckBox)findViewById(R.id.checkSalvarSenha)).isChecked()) {

                            saspConfig.setConfigLoginSenha(senha);
                        }
                        else {

                            saspConfig.removeConfigLoginSenha();
                        }

                        new MaterialDialog.Builder(LoginActivity.this)
                                .customView(R.layout.layout_aviso_sasp_solicitacao_negada, true)
                                .positiveText("OK")
                                .canceledOnTouchOutside(false)
                                .cancelable(false)
                                .show();
                    }
                    else {

                        dialogHelper.showError(msg);
                    }
                }
            }

            @Override
            void onResponse(String error) {

                dialogHelper.showError(error);
            }

            @Override
            void onNoResponse(String error) {

                dialogHelper.showError(error);
            }

            @Override
            void onPostResponse() {

                dialogHelper.dismissProgress();
            }
        });
    }

    public void buttonSolicitarAcesso(View view) {

        askForLogin = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!confirmAllPermissions()) {

                dialogHelper.confirmDialog(false, "Solicitação de permissão", messagePermissions, "Não", new MaterialDialog.SingleButtonCallback() {

                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, CODE_PERMISSION_REQUEST_PHONE_STATE);
                        }
                        else if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_WIFI_STATE}, CODE_PERMISSION_REQUEST_WIFI_STATE);
                        }
                        else if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, CODE_PERMISSION_REQUEST_NETWORK_STATE);
                        }
                        else if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CODE_PERMISSION_REQUEST_READ_EXTERNAL);
                        }
                        else if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CODE_PERMISSION_REQUEST_WRITE_EXTERNAL);
                        }
                        else if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.CAMERA}, CODE_PERMISSION_REQUEST_CAMERA);
                        }
                        else {

                            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, CODE_PERMISSION_REQUEST_FINE_LOCATION);
                        }
                    }
                });
            }
            else {

                solicitarAcesso();
            }
        }
        else {

            solicitarAcesso();
        }
    }

    private void solicitarAcesso() {

        DataHolder dh = DataHolder.getInstance();

        dh.setUserIP(null);
        dh.setUserIMEI(saspServer.getDeviceIMEI());
        dh.setUserMAC(AppUtils.getMacAddr());

        dialogHelper.showProgress();

        saspServer.saspServerUserIP(new SaspResponse(LoginActivity.this) {

            @Override
            void onSaspResponse(String error, String msg, JSONObject extra) {

                if (error.equals("0")) {

                    try {

                        DataHolder.getInstance().setUserIP(extra.getString("ip"));
                    }
                    catch (Exception e) { }
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

                dialogHelper.dismissProgress();

                Intent i = new Intent(LoginActivity.this, UsuariosCadastrarUsuarioActivity.class);
                startActivityForResult(i, UsuariosCadastrarUsuarioActivity.CODE_ACTIVITY_CADASTRAR_USUARIO);
            }
        });
    }

    @Override
    void onPermissionsChange(boolean confirmed) {

    }

    public void buttonRecuperarSenha(View view) {

        dialogHelper.showProgressDelayed(1000, new Runnable() {

            @Override
            public void run() {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        new MaterialDialog.Builder(LoginActivity.this)
                                .customView(R.layout.layout_recuperar_senha, true)
                                .positiveText("OK")
                                .show();
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {

        super.onResume();

        DataHolder.getInstance().setLoginActivityVisible();

        String cpf = saspConfig.getConfigLoginCPF();
        String senha = saspConfig.getConfigLoginSenha();

        if (cpf != null) {

            ((EditText)findViewById(R.id.editTextCPF)).setText(AppUtils.formatarCPF(cpf));
        }
        else {

            return;
        }

        if (senha != null) {

            ((CheckBox)findViewById(R.id.checkSalvarSenha)).setChecked(true);
            ((EditText)findViewById(R.id.editTextSenha)).setText(senha);
        }

        if (getIntent().hasExtra("SESSION_EXPIRED")) {

            dialogHelper.showError("Sessão expirada.\n\nFaça o login novamente para continuar.");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CODE_PERMISSION_REQUEST_PHONE_STATE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {

                if (!ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.READ_PHONE_STATE)) {

                    dialogHelper.showError(messageRationale);
                }
            }
            else {

                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_WIFI_STATE}, CODE_PERMISSION_REQUEST_WIFI_STATE);
            }
        }
        else if (requestCode == CODE_PERMISSION_REQUEST_WIFI_STATE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {

                if (!ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.ACCESS_WIFI_STATE)) {

                    dialogHelper.showError(messageRationale);
                }
            }
            else {

                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_WIFI_STATE}, CODE_PERMISSION_REQUEST_NETWORK_STATE);
            }
        }
        else if (requestCode == CODE_PERMISSION_REQUEST_NETWORK_STATE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {

                if (!ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.ACCESS_NETWORK_STATE)) {

                    dialogHelper.showError(messageRationale);
                }
            }
            else {

                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_WIFI_STATE}, CODE_PERMISSION_REQUEST_READ_EXTERNAL);
            }
        }
        else if (requestCode == CODE_PERMISSION_REQUEST_READ_EXTERNAL) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {

                if (!ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    dialogHelper.showError(messageRationale);
                }
            }
            else {

                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CODE_PERMISSION_REQUEST_WRITE_EXTERNAL);
            }
        }
        else if (requestCode == CODE_PERMISSION_REQUEST_WRITE_EXTERNAL) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {

                if (!ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    dialogHelper.showError(messageRationale);
                }
            }
            else {

                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.CAMERA}, CODE_PERMISSION_REQUEST_CAMERA);
            }
        }
        else if (requestCode == CODE_PERMISSION_REQUEST_CAMERA) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {

                if (!ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.CAMERA)) {

                    dialogHelper.showError(messageRationale);
                }
            }
            else {

                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, CODE_PERMISSION_REQUEST_FINE_LOCATION);
            }
        }
        else {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {

                if (!ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                    dialogHelper.showError(messageRationale);
                }
            }
            else {

                if (askForLogin) {

                    solicitarLogin();
                }
                else {

                    solicitarAcesso();
                }
            }
        }
    }

    @Override
    protected void onPause() {

        super.onPause();

        DataHolder.getInstance().setLoginActivityInvisible();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UsuariosCadastrarUsuarioActivity.CODE_ACTIVITY_CADASTRAR_USUARIO) {

            if (resultCode == 1) {

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        dialogHelper.showProgressDelayed(1000, new Runnable() {

                            @Override
                            public void run() {

                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {

                                        dialogHelper.showSuccess("Solicitação de acesso enviada.\n\nUtilize seu CPF e senha para mais informações.");
                                    }
                                });
                            }
                        });
                    }
                }, 500);
            }
        }
    }
}
