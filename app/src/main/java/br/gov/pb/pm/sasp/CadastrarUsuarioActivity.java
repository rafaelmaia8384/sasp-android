package br.gov.pb.pm.sasp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

public class CadastrarUsuarioActivity extends SaspActivity {

    public static final int CODE_PERMISSION_REQUEST_CAMERA = 100;
    public static final int CODE_PERMISSION_REQUEST_STORAGE = 200;

    private DialogHelper dialogHelper;
    private SaspServer saspServer;

    private TextWatcher mascaraCPF;
    private TextWatcher mascaraMatricula;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cadastrar_usuario);

        dialogHelper = new DialogHelper(this);
        saspServer = new SaspServer(this);

        EditText editTextCPF = (EditText)findViewById(R.id.editTextCPF);
        EditText editTextMatricula = (EditText)findViewById(R.id.editTextMatricula);

        mascaraCPF = MascaraCPF.insert("###.###.###-##", editTextCPF);
        mascaraMatricula = MascaraCPF.insert("###.###-#", editTextMatricula);

        editTextCPF.addTextChangedListener(mascaraCPF);
        editTextMatricula.addTextChangedListener(mascaraMatricula);

        DataHolder dh = DataHolder.getInstance();

        if (dh.getUserIP() != null || dh.getUserIMEI() != null || !dh.getUserMAC().equals("02:00:00:00:00:00")) {

            findViewById(R.id.layoutInfoAparelho).setVisibility(View.VISIBLE);

            if (dh.getUserIP() != null) {

                findViewById(R.id.layoutInfoIP).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.textEnderecoIP)).setText(dh.getUserIP());
            }

            if (dh.getUserIMEI() != null) {

                findViewById(R.id.layoutInfoIMEI).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.textIMEI)).setText(dh.getUserIMEI());
            }

            if (!dh.getUserMAC().equals("02:00:00:00:00:00")) {

                findViewById(R.id.layoutInfoMAC).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.textMAC)).setText(dh.getUserMAC());
            }
        }

        findViewById(R.id.buttonImagemPerfil).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                buttonImagemPerfil();
            }
        });

        findViewById(R.id.buttonSolicitarAcesso).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                enviarSolicitacao();
            }
        });

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                dialogHelper.showProgressDelayed(1000, new Runnable() {

                    @Override
                    public void run() {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                new MaterialDialog.Builder(CadastrarUsuarioActivity.this)
                                        .customView(R.layout.layout_aviso_sasp_solicitacao_fraude, true)
                                        .positiveText("OK")
                                        .canceledOnTouchOutside(false)
                                        .cancelable(false)
                                        .show();
                            }
                        });
                    }
                });
            }
        }, 500);
    }

    private void buttonImagemPerfil() {

        dialogHelper.showProgress();

        CropImage.activity()
                .setCropMenuCropButtonTitle("Finalizar")
                .setGuidelines(CropImageView.Guidelines.ON)
                .setOutputCompressQuality(90)
                .setRequestedSize(840, 840)
                .start(CadastrarUsuarioActivity.this);
    }

    public void buttonTermosDeUso(View view) {

        dialogHelper.showProgressDelayed(1000, new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MaterialDialog termos = new MaterialDialog.Builder(CadastrarUsuarioActivity.this)
                                .title("Termos de Uso")
                                .content("Leia os Termos de Uso do sistema diretamente no Website da PMPB.\n\nDeseja continuar?")
                                .positiveText("OK")
                                .negativeText("Cancelar")
                                .canceledOnTouchOutside(true)
                                .cancelable(true)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                        dialogHelper.showSuccess("Abrir Url web");
                                    }
                                })
                                .build();

                        termos.getContentView().setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        termos.show();
                    }
                });
            }
        });
    }

    public void buttonClose(View view) {

        finish();
    }

    private void enviarSolicitacao() {

        SaspImage saspImage = new SaspImage(CadastrarUsuarioActivity.this);
        saspImage.salvarImagem(imageUri);

        saspServer.saspServerSaveUploadObject(saspImage, SaspServer.MODULO_UPLOAD_OBJECT_PESSOAS);

        dialogHelper.showSuccess("OK!");

        if (1 == 1) return;

        final String cpf = ((EditText)findViewById(R.id.editTextCPF)).getText().toString();
        final String email = ((EditText)findViewById(R.id.editTextEmail)).getText().toString();
        final String matricula = ((EditText)findViewById(R.id.editTextMatricula)).getText().toString();
        final String ddd = ((EditText)findViewById(R.id.editTextDDD)).getText().toString();
        final String telefone = ((EditText)findViewById(R.id.editTextTelefone)).getText().toString();
        final String senha1 = ((EditText)findViewById(R.id.editTextSenha1)).getText().toString();
        final String senha2 = ((EditText)findViewById(R.id.editTextSenha2)).getText().toString();

        if (findViewById(R.id.imagemPerfil).getTag() == null) {

            dialogHelper.showError("Selecione a imagem para a foto de perfil.");

            return;
        }

        if (!AppUtils.validarCPF(cpf)) {

            dialogHelper.showError("Verifique o CPF.");

            return;
        }

        if (!AppUtils.validarEmail(email)) {

            dialogHelper.showError("Verifique o email.");

            return;
        }

        if (matricula.length() < 9) {

            dialogHelper.showError("Verifique sua matrícula.");

            return;
        }

        if (ddd.length() < 2) {

            dialogHelper.showError("Verifique o DDD do telefone.");

            return;
        }

        if (telefone.length() < 8) {

            dialogHelper.showError("Verifique o número de telefone.");

            return;
        }

        if (!senha1.equals(senha2)) {

            dialogHelper.showError("As senhas digitadas não conferem.");

            return;
        }

        if (senha1.length() < 6 || senha1.length() > 20) {

            dialogHelper.showError("A senha deve conter entre 6 e 20 dígitos.");

            return;
        }

        if (!((CheckBox)findViewById(R.id.checkTermosDeUso)).isChecked()) {

            dialogHelper.showError("Você deve aceitar os Termos de Uso do sistema para continuar.");

            return;
        }

        dialogHelper.showProgress();

        AsyncTask.execute(new Runnable() {

            @Override
            public void run() {

                final SaspImage saspImage = new SaspImage(CadastrarUsuarioActivity.this);
                final boolean done = saspImage.salvarImagem(imageUri);

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        if (done) {

                            saspServer.usuarioCadastrar(cpf, matricula, email, ddd + telefone, senha1, SaspServer.getAparelhoId(), DataHolder.getInstance().getUserIMEI(), DataHolder.getInstance().getUserMAC(), saspImage, new SaspResponse(CadastrarUsuarioActivity.this) {

                                @Override
                                void onSaspResponse(String error, String msg, JSONObject extra) {

                                    if (error.equals("1")) {

                                        dialogHelper.showError(msg);
                                    }
                                    else {

                                        setResult(1);
                                        finish();
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
                                }
                            });
                        }
                        else {

                            dialogHelper.dismissProgress();

                            dialogHelper.showError("Erro ao salvar imagem.\n\nTente enviar uma imagem de tamanho menor.");
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {

                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                imageUri = result.getUri();

                findViewById(R.id.imagemPerfil).setTag("changed");
                ((ImageView)findViewById(R.id.imagemPerfil)).setImageURI(imageUri);
            }
        }

        dialogHelper.dismissProgress();
    }

    @Override
    void onPermissionsChange(boolean confirmed) {

        if (confirmed) {

            dialogHelper.dismissBlock();
        }
        else {

            dialogHelper.showBlock("Ative as permissões do aplicativo nas configurações do Android.");
        }
    }
}
