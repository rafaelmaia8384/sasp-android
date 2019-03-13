package br.gov.pb.pm.sasp;

import android.animation.Animator;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PessoasCadastrarPessoaActivity extends SaspActivity {

    public static final int CODE_ACTIVITY_CADASTRAR_PESSOA = 101;
    
    private DialogHelper dialogHelper;
    private SaspServer saspServer;

    private TextWatcher mascaraCPF;
    private TextWatcher mascaraNascimento;

    private PopupMenu pmSelecionarImagem;
    private PopupMenu pmImagem;

    private int imageSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.pessoas_activity_cadastrar_pessoa);

        dialogHelper = new DialogHelper(PessoasCadastrarPessoaActivity.this);
        saspServer = new SaspServer(PessoasCadastrarPessoaActivity.this);

        EditText editTextCPF = (EditText) findViewById(R.id.editTextCPF);
        final EditText editTextDataNascimento = (EditText) findViewById(R.id.editTextDataNascimento);

        pmSelecionarImagem = new PopupMenu(PessoasCadastrarPessoaActivity.this, findViewById(R.id.viewPerfil));
        pmSelecionarImagem.inflate(R.menu.menu_selecionar_imagem_perfil);
        pmImagem = new PopupMenu(PessoasCadastrarPessoaActivity.this, findViewById(R.id.viewAddImage));
        pmImagem.inflate(R.menu.menu_adicionar_imagem);

        mascaraCPF = MascaraCPF.insert("###.###.###-##", editTextCPF);
        mascaraNascimento = MascaraCPF.insert("##/##/####", editTextDataNascimento);

        editTextCPF.addTextChangedListener(mascaraCPF);
        editTextDataNascimento.addTextChangedListener(mascaraNascimento);

        findViewById(R.id.buttonContinuar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (findViewById(R.id.imagemPerfil).getTag() == null) {

                    dialogHelper.showError("Selecione a foto do perfil.");

                    return;
                }

                String nomeAlcunha = ((EditText)findViewById(R.id.editTextNomeAlcunha)).getText().toString();
                String nomeCompleto = ((EditText)findViewById(R.id.editTextNomeCompleto)).getText().toString();
                String relato = ((EditText)findViewById(R.id.editTextRelato)).getText().toString();

                if (nomeCompleto.length() == 0) {

                    nomeCompleto = "null";
                }

                relato = AppUtils.formatarTexto(relato);

                if (nomeAlcunha.length() < 2) {

                    dialogHelper.showError("Escreva o nome ou alcunha da pessoa.");

                    return;
                }

                final int crtCorPele = ((Spinner)findViewById(R.id.spinnerCrtCorPele)).getSelectedItemPosition();
                final int crtCorOlhos = ((Spinner)findViewById(R.id.spinnerCrtCorOlhos)).getSelectedItemPosition();
                final int crtCorCabelos = ((Spinner)findViewById(R.id.spinnerCrtCorCabelos)).getSelectedItemPosition();
                final int crtTipoCabelos = ((Spinner)findViewById(R.id.spinnerCrtTipoCabelos)).getSelectedItemPosition();
                final int crtPorteFisico = ((Spinner)findViewById(R.id.spinnerCrtPorteFisico)).getSelectedItemPosition();
                final int crtEstatura = ((Spinner)findViewById(R.id.spinnerCrtEstatura)).getSelectedItemPosition();
                final int crtDeficiente = ((Spinner)findViewById(R.id.spinnerCrtDeficiente)).getSelectedItemPosition();
                final int crtTatuagem = ((Spinner)findViewById(R.id.spinnerCrtPossuiTatuagem)).getSelectedItemPosition();

                int count = 0;

                if (crtCorPele == 0) count++;
                if (crtCorOlhos == 0) count++;
                if (crtCorCabelos == 0) count++;
                if (crtTipoCabelos == 0) count++;
                if (crtPorteFisico == 0) count++;
                if (crtEstatura == 0) count++;
                if (crtDeficiente == 0) count++;
                if (crtTatuagem == 0) count++;

                if (count > 0) {

                    dialogHelper.showError("Responda todas as características físicas para continuar.");

                    return;
                }

                count = 0;

                if (crtCorPele == 1) count++;
                if (crtCorOlhos == 1) count++;
                if (crtCorCabelos == 1) count++;
                if (crtTipoCabelos == 1) count++;
                if (crtPorteFisico == 1) count++;
                if (crtEstatura == 1) count++;
                if (crtDeficiente == 1) count++;
                if (crtTatuagem == 1) count++;

                if (count > 3) {

                    dialogHelper.showError("Selecione pelo menos 5 características físicas.");

                    return;
                }

                if (relato.length() == 0){

                    dialogHelper.showError("Escreva o relato sobre o indivíduo.");

                    return;
                }
                else if (relato.length() < 5) {

                    dialogHelper.showError("Escreva mais informações no relato.");

                    return;
                }

                boolean checkFutro = ((CheckBox)findViewById(R.id.check_furto)).isChecked();
                boolean checkRoubo = ((CheckBox)findViewById(R.id.check_roubo)).isChecked();
                boolean checkRouboBanco = ((CheckBox)findViewById(R.id.check_roubo_bancos)).isChecked();
                boolean checkTrafico = ((CheckBox)findViewById(R.id.check_trafico)).isChecked();
                boolean checkHomicidio = ((CheckBox)findViewById(R.id.check_homicidio)).isChecked();
                boolean checkLatrocinio = ((CheckBox)findViewById(R.id.check_latrocinio)).isChecked();
                boolean checkEstupro = ((CheckBox)findViewById(R.id.check_estupro)).isChecked();
                boolean checkEstelionato = ((CheckBox)findViewById(R.id.check_estelionato)).isChecked();
                boolean checkPossePorte = ((CheckBox)findViewById(R.id.check_posse_porte)).isChecked();
                boolean checkReceptacao = ((CheckBox)findViewById(R.id.check_receptacao)).isChecked();
                boolean checkContrabando = ((CheckBox)findViewById(R.id.check_contrabando)).isChecked();
                boolean checkOutros = ((CheckBox)findViewById(R.id.check_outros)).isChecked();

                int historico = 0;

                if (checkFutro) historico |= 1 << 0;
                if (checkRoubo) historico |= 1 << 1;
                if (checkRouboBanco) historico |= 1 << 2;
                if (checkTrafico) historico |= 1 << 3;
                if (checkHomicidio) historico |= 1 << 4;
                if (checkLatrocinio) historico |= 1 << 5;
                if (checkEstupro) historico |= 1 << 6;
                if (checkEstelionato) historico |= 1 << 7;
                if (checkPossePorte) historico |= 1 << 8;
                if (checkReceptacao) historico |= 1 << 9;
                if (checkContrabando) historico |= 1 << 10;
                if (checkOutros) historico |= 1 << 11;

                if (historico == 0) {

                    dialogHelper.showError("Selecione pelo menos uma atividade criminosa.");

                    return;
                }

                boolean checkAC = ((CheckBox)findViewById(R.id.checkBoxAC)).isChecked();
                boolean checkAL = ((CheckBox)findViewById(R.id.checkBoxAL)).isChecked();
                boolean checkAM = ((CheckBox)findViewById(R.id.checkBoxAM)).isChecked();
                boolean checkAP = ((CheckBox)findViewById(R.id.checkBoxAP)).isChecked();
                boolean checkBA = ((CheckBox)findViewById(R.id.checkBoxBA)).isChecked();
                boolean checkCE = ((CheckBox)findViewById(R.id.checkBoxCE)).isChecked();
                boolean checkDF = ((CheckBox)findViewById(R.id.checkBoxDF)).isChecked();
                boolean checkES = ((CheckBox)findViewById(R.id.checkBoxES)).isChecked();
                boolean checkGO = ((CheckBox)findViewById(R.id.checkBoxGO)).isChecked();
                boolean checkMA = ((CheckBox)findViewById(R.id.checkBoxMA)).isChecked();
                boolean checkMG = ((CheckBox)findViewById(R.id.checkBoxMG)).isChecked();
                boolean checkMS = ((CheckBox)findViewById(R.id.checkBoxMS)).isChecked();
                boolean checkMT = ((CheckBox)findViewById(R.id.checkBoxMT)).isChecked();
                boolean checkPA = ((CheckBox)findViewById(R.id.checkBoxPA)).isChecked();
                boolean checkPB = ((CheckBox)findViewById(R.id.checkBoxPB)).isChecked();
                boolean checkPE = ((CheckBox)findViewById(R.id.checkBoxPE)).isChecked();
                boolean checkPI = ((CheckBox)findViewById(R.id.checkBoxPI)).isChecked();
                boolean checkPR = ((CheckBox)findViewById(R.id.checkBoxPR)).isChecked();
                boolean checkRJ = ((CheckBox)findViewById(R.id.checkBoxRJ)).isChecked();
                boolean checkRN = ((CheckBox)findViewById(R.id.checkBoxRN)).isChecked();
                boolean checkRO = ((CheckBox)findViewById(R.id.checkBoxRO)).isChecked();
                boolean checkRR = ((CheckBox)findViewById(R.id.checkBoxRR)).isChecked();
                boolean checkRS = ((CheckBox)findViewById(R.id.checkBoxRS)).isChecked();
                boolean checkSC = ((CheckBox)findViewById(R.id.checkBoxSC)).isChecked();
                boolean checkSE = ((CheckBox)findViewById(R.id.checkBoxSE)).isChecked();
                boolean checkSP = ((CheckBox)findViewById(R.id.checkBoxSP)).isChecked();
                boolean checkTO = ((CheckBox)findViewById(R.id.checkBoxTO)).isChecked();

                int areas_de_atuacao = 0;

                if (checkAC) areas_de_atuacao |= 1 << 0;
                if (checkAL) areas_de_atuacao |= 1 << 1;
                if (checkAM) areas_de_atuacao |= 1 << 2;
                if (checkAP) areas_de_atuacao |= 1 << 3;
                if (checkBA) areas_de_atuacao |= 1 << 4;
                if (checkCE) areas_de_atuacao |= 1 << 5;
                if (checkDF) areas_de_atuacao |= 1 << 6;
                if (checkES) areas_de_atuacao |= 1 << 7;
                if (checkGO) areas_de_atuacao |= 1 << 8;
                if (checkMA) areas_de_atuacao |= 1 << 9;
                if (checkMG) areas_de_atuacao |= 1 << 10;
                if (checkMS) areas_de_atuacao |= 1 << 11;
                if (checkMT) areas_de_atuacao |= 1 << 12;
                if (checkPA) areas_de_atuacao |= 1 << 13;
                if (checkPB) areas_de_atuacao |= 1 << 14;
                if (checkPE) areas_de_atuacao |= 1 << 15;
                if (checkPI) areas_de_atuacao |= 1 << 16;
                if (checkPR) areas_de_atuacao |= 1 << 17;
                if (checkRJ) areas_de_atuacao |= 1 << 18;
                if (checkRN) areas_de_atuacao |= 1 << 19;
                if (checkRO) areas_de_atuacao |= 1 << 20;
                if (checkRR) areas_de_atuacao |= 1 << 21;
                if (checkRS) areas_de_atuacao |= 1 << 22;
                if (checkSC) areas_de_atuacao |= 1 << 23;
                if (checkSE) areas_de_atuacao |= 1 << 24;
                if (checkSP) areas_de_atuacao |= 1 << 25;
                if (checkTO) areas_de_atuacao |= 1 << 26;

                if (areas_de_atuacao == 0) {

                    dialogHelper.showError("Marque pelo menos uma Unidade da Federação.");

                    return;
                }

                String nome_da_mae = ((EditText)findViewById(R.id.editTextNomeDaMae)).getText().toString();
                String cpf = ((EditText)findViewById(R.id.editTextCPF)).getText().toString();
                String rg = ((EditText)findViewById(R.id.editTextRG)).getText().toString();
                String data_nascimento = ((EditText)findViewById(R.id.editTextDataNascimento)).getText().toString();

                String ok_nome_da_mae = "null";
                String ok_cpf = "null";
                String ok_rg = "null";
                String ok_data_nascimento = "null";

                if (nome_da_mae.length() > 0) {

                    if (nome_da_mae.length() < 3) {

                        dialogHelper.showError("Verifique o nome da mãe.");

                        return;
                    }
                    else {

                        ok_nome_da_mae = nome_da_mae;
                    }
                }

                if (cpf.length() > 0) {

                    if (!AppUtils.validarCPF(cpf)) {

                        dialogHelper.showError("Verifique o CPF.");

                        return;
                    }
                    else {

                        ok_cpf = AppUtils.limparCPF(cpf);
                    }
                }

                if (rg.length() > 0) {

                    if (rg.length() < 5) {

                        dialogHelper.showError("Verifique o RG digitado.");

                        return;
                    }
                    else {

                        ok_rg = rg;
                    }
                }

                if (data_nascimento.length() > 0) {

                    if (data_nascimento.length() < 10) {

                        dialogHelper.showError("Verifique a data de nascimento.");

                        return;
                    }
                    else {

                        String[] split = data_nascimento.split("/");

                        int a = Integer.parseInt(split[0]);
                        int b = Integer.parseInt(split[1]);
                        int c = Integer.parseInt(split[2]);

                        if (a == 0 || a > 31 || b == 0 || b > 12 || c < 1920) {

                            dialogHelper.showError("Verifique a data de nascimento.");

                            return;
                        }
                        else {

                            ok_data_nascimento = split[2] + "-" + split[1] + "-" + split[0];
                        }
                    }
                }

                DataHolder.getInstance().setCadastrarPessoaData(nomeAlcunha, nomeCompleto, crtCorPele, crtCorOlhos, crtCorCabelos, crtTipoCabelos, crtPorteFisico, crtEstatura, crtDeficiente, crtTatuagem, relato, historico, areas_de_atuacao, ok_nome_da_mae, ok_cpf, ok_rg, ok_data_nascimento);

                dialogHelper.showProgress();

                AsyncTask.execute(new Runnable() {

                    @Override
                    public void run() {

                        final List<SaspImage> imageList = new ArrayList<>();

                        SaspImage saspImagePerfil = new SaspImage(PessoasCadastrarPessoaActivity.this);
                        Uri imgPerfilUri = (Uri)findViewById(R.id.imagemPerfil).getTag();
                        saspImagePerfil.salvarImagem(imgPerfilUri);
                        DataHolder.getInstance().setAdicionarPessoaImgUri(imgPerfilUri);

                        imageList.add(saspImagePerfil);

                        ViewGroup vg = (ViewGroup)findViewById(R.id.layoutNewImage);

                        for (int i = 0; i < vg.getChildCount(); i++) {

                            Uri imgUri = (Uri)vg.getChildAt(i).findViewById(R.id.imageNew).getTag();

                            SaspImage sp = new SaspImage(PessoasCadastrarPessoaActivity.this);
                            sp.salvarImagem(imgUri);

                            imageList.add(sp);
                        }

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                saspServer.cadastrarPessoa(imageList, new SaspResponse(PessoasCadastrarPessoaActivity.this) {

                                    @Override
                                    void onSaspResponse(String error, String msg, JSONObject extra) {

                                        if (error.equals("1")) {

                                            dialogHelper.showError(msg);
                                        }
                                        else {

                                            for (int i = 0; i < imageList.size(); i++) {

                                                imageList.get(i).saveUploadObject(SaspImage.UPLOAD_OBJECT_MODULO_PESSOAS);
                                            }

                                            SaspServer.startServiceUploadImages(getApplicationContext());

                                            try {

                                                String id_pessoa = extra.getString("id_pessoa");

                                                DataHolder.getInstance().setAdicionarPessoaIdPessoa(id_pessoa);
                                            }
                                            catch (Exception e) { }

                                            setResult(1);
                                            finish();
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

                                        for (int a = 0; a < imageList.size(); a++) {

                                            imageList.get(a).delete();
                                        }

                                        dialogHelper.dismissProgress();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    void onPermissionsChange(boolean confirmed) {

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            dialogHelper.dismissProgress();

            if (resultCode == RESULT_OK) {

                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                if (imageSelect == 1) { //Selecionada a imagem de perfil

                    findViewById(R.id.imagemPerfil).setTag(result.getUri());
                    ((ImageView)findViewById(R.id.imagemPerfil)).setImageURI(result.getUri());
                }
                else { //Selecionada uma imagem extra

                    final ViewGroup vg = (ViewGroup)findViewById(R.id.layoutNewImage);

                    final View child = LayoutInflater.from(PessoasCadastrarPessoaActivity.this).inflate(R.layout.layout_nova_imagem, null);

                    child.findViewById(R.id.imageClick).setOnLongClickListener(new View.OnLongClickListener() {

                        @Override
                        public boolean onLongClick(View view) {

                            PopupMenu pop = new PopupMenu(PessoasCadastrarPessoaActivity.this, view);
                            pop.inflate(R.menu.menu_excluir_imagem);

                            pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem menuItem) {

                                    if (menuItem.getOrder() == 1) {

                                        YoYo.with(Techniques.ZoomOut)
                                                .duration(500)
                                                .onEnd(new YoYo.AnimatorCallback() {

                                                    @Override
                                                    public void call(Animator animator) {

                                                        vg.removeView(child);
                                                    }
                                                })
                                                .playOn(child);
                                    }

                                    return false;
                                }
                            });

                            pop.show();

                            return false;
                        }
                    });

                    vg.addView(child, 0);

                    YoYo.with(Techniques.BounceIn)
                            .duration(500)
                            .playOn(child);

                    ImageView novaImagem = child.findViewById(R.id.imageNew);

                    novaImagem.setTag(result.getUri());
                    novaImagem.setImageURI(result.getUri());
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void fecharJanela(View view) {

        finish();
    }

    public void novaImagemSnack(View view) {

        Snackbar.make(findViewById(android.R.id.content), "Pressione e segure para excluir a imagem.", 1000).show();
    }

    public void buttonAdicionarImagem(View view) {

        pmImagem.show();
    }

    public void adicionarImagem(MenuItem item) {

        if (item.getOrder() == 1) {

            imageSelect = 2;

            dialogHelper.showProgress();

            CropImage.activity()
                    .setCropMenuCropButtonTitle("Pronto")
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(PessoasCadastrarPessoaActivity.this);
        }
    }

    public void buttonSelecionarImagem(View view) {

        pmSelecionarImagem.show();
    }

    public void selecionarImagem(MenuItem item) {

        if (item.getOrder() == 1) {

            imageSelect = 1;

            dialogHelper.showProgress();

            CropImage.activity()
                    .setCropMenuCropButtonTitle("Pronto")
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(PessoasCadastrarPessoaActivity.this);
        }
    }
}
