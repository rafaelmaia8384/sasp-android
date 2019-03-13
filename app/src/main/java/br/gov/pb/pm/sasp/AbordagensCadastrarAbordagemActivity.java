package br.gov.pb.pm.sasp;

import android.animation.Animator;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AbordagensCadastrarAbordagemActivity extends SaspActivity {

    public static final int CODE_ACTIVITY_CADASTRAR_ABORDAGEM = 102;
    
    private DialogHelper dialogHelper;
    private SaspServer saspServer;

    private PopupMenu pmLocal;
    private PopupMenu pmPessoa;
    private PopupMenu pmImagem;
    private PopupMenu pmMatricula;

    private Uri imgLocalUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.abordagens_activity_cadastrar_abordagem);

        dialogHelper = new DialogHelper(AbordagensCadastrarAbordagemActivity.this);
        saspServer = new SaspServer(AbordagensCadastrarAbordagemActivity.this);

        pmLocal = new PopupMenu(AbordagensCadastrarAbordagemActivity.this, findViewById(R.id.viewLocal));
        pmLocal.inflate(R.menu.menu_adicionar_local);
        pmPessoa = new PopupMenu(AbordagensCadastrarAbordagemActivity.this, findViewById(R.id.viewAddPessoa));
        pmPessoa.inflate(R.menu.menu_adicionar_pessoa);
        pmImagem = new PopupMenu(AbordagensCadastrarAbordagemActivity.this, findViewById(R.id.viewAddImage));
        pmImagem.inflate(R.menu.menu_adicionar_imagem);
        pmMatricula = new PopupMenu(AbordagensCadastrarAbordagemActivity.this, findViewById(R.id.viewAddMatricula));
        pmMatricula.inflate(R.menu.menu_adicionar_matricula);

        findViewById(R.id.buttonCadastrar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (imgLocalUri == null) {

                    dialogHelper.showError("Selecione o local da abordagem.");

                    return;
                }

                int imagensCount = ((ViewGroup)findViewById(R.id.layoutNewImage)).getChildCount();
                int pessoasCount = ((ViewGroup)findViewById(R.id.layoutNewPessoa)).getChildCount();
                int matriculasCount = ((ViewGroup)findViewById(R.id.layoutNewMatricula)).getChildCount();

                if (imagensCount == 0) {

                    dialogHelper.showError("Adicione pelo menos uma foto da abordagem.");

                    return;
                }

                if (pessoasCount == 0) {

                    dialogHelper.showError("Adicione pelo menos uma pessoa abordada.");

                    return;
                }

                if (matriculasCount < 2) {

                    dialogHelper.showError("Adicione pelo menos duas matrículas.");

                    return;
                }

                final String relato = AppUtils.formatarTexto(((EditText)findViewById(R.id.editTextRelato)).getText().toString());

                if (relato.length() < 4) {

                    dialogHelper.showError("Escreva o relato da abordagem.");

                    return;
                }

                dialogHelper.showProgress();

                AsyncTask.execute(new Runnable() {

                    @Override
                    public void run() {

                        final List<SaspImage> imageList = new ArrayList<>();
                        final List<String> pessoaList = new ArrayList<>();
                        final List<String> matriculaList = new ArrayList<>();

                        SaspImage saspImagePerfil = new SaspImage(AbordagensCadastrarAbordagemActivity.this);
                        saspImagePerfil.salvarImagem(imgLocalUri);

                        imageList.add(saspImagePerfil);

                        ViewGroup vg = (ViewGroup)findViewById(R.id.layoutNewImage);

                        for (int i = 0; i < vg.getChildCount(); i++) {

                            Uri imgUri = (Uri)vg.getChildAt(i).findViewById(R.id.imageNew).getTag();

                            SaspImage sp = new SaspImage(AbordagensCadastrarAbordagemActivity.this);
                            sp.salvarImagem(imgUri);

                            imageList.add(sp);
                        }

                        vg = (ViewGroup)findViewById(R.id.layoutNewPessoa);

                        for (int i = 0; i < vg.getChildCount(); i++) {

                            String id_pessoa = (String)vg.getChildAt(i).findViewById(R.id.pessoaNew).getTag();
                            pessoaList.add(id_pessoa);
                        }

                        vg = (ViewGroup)findViewById(R.id.layoutNewMatricula);

                        for (int i = 0; i < vg.getChildCount(); i++) {

                            String mat = (String)vg.getChildAt(i).findViewById(R.id.matriculaNew).getTag();
                            matriculaList.add(mat);
                        }

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                saspServer.abordagensCadastrar(imageList, pessoaList, matriculaList, relato, new SaspResponse(AbordagensCadastrarAbordagemActivity.this) {

                                    @Override
                                    void onSaspResponse(String error, String msg, JSONObject extra) {

                                        if (error.equals("1")) {

                                            dialogHelper.showError(msg);
                                        }
                                        else {

                                            for (int i = 0; i < imageList.size(); i++) {

                                                imageList.get(i).saveUploadObject(SaspImage.UPLOAD_OBJECT_MODULO_ABORDAGENS);
                                            }

                                            SaspServer.startServiceUploadImages(getApplicationContext());

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

    private void escolherLocal() {

        dialogHelper.showProgress();

        Intent i = new Intent(AbordagensCadastrarAbordagemActivity.this, LocationPickerActivity.class);
        startActivityForResult(i, LocationPickerActivity.LOCATION_PICKER_INTENT);
    }

    @Override
    void onPermissionsChange(boolean confirmed) {

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        dialogHelper.dismissProgress();

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {

                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                final ViewGroup vg = (ViewGroup)findViewById(R.id.layoutNewImage);
                final View child = LayoutInflater.from(AbordagensCadastrarAbordagemActivity.this).inflate(R.layout.layout_nova_imagem, null);

                child.findViewById(R.id.imageClick).setOnLongClickListener(new View.OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View view) {

                        PopupMenu pop = new PopupMenu(AbordagensCadastrarAbordagemActivity.this, view);
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
        else if (requestCode == LocationPickerActivity.LOCATION_PICKER_INTENT) {

            if (resultCode == 1) {

                imgLocalUri = Uri.fromFile((File)data.getExtras().get("imgLocal"));
                ((ImageView)findViewById(R.id.imagemGPS)).setImageURI(imgLocalUri);
            }

            dialogHelper.dismissProgress();
        }
        else if (requestCode == AdicionarPessoaActivity.CODE_ADICIONAR_PESSOA_ACTIVITY) {

            if (resultCode == 1) {

                final ViewGroup vg = (ViewGroup)findViewById(R.id.layoutNewPessoa);
                final View child = LayoutInflater.from(AbordagensCadastrarAbordagemActivity.this).inflate(R.layout.layout_nova_pessoa, null);

                child.findViewById(R.id.pessoaNew).setTag(DataHolder.getInstance().getAdicionarPessoaIdPessoa());
                child.findViewById(R.id.imageClick).setOnLongClickListener(new View.OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View view) {

                        PopupMenu pop = new PopupMenu(AbordagensCadastrarAbordagemActivity.this, view);
                        pop.inflate(R.menu.menu_excluir_pessoa);

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

                ImageView novaImagem = child.findViewById(R.id.pessoaNew);
                ImageLoader.getInstance().displayImage(SaspServer.getImageAddress(DataHolder.getInstance().getAdicionarPessoaImgBusca(), "pessoas", true), novaImagem);
            }
        }
        else if (requestCode == PessoasCadastrarPessoaActivity.CODE_ACTIVITY_CADASTRAR_PESSOA) {

            if (resultCode == 1) {

                final ViewGroup vg = (ViewGroup)findViewById(R.id.layoutNewPessoa);
                final View child = LayoutInflater.from(AbordagensCadastrarAbordagemActivity.this).inflate(R.layout.layout_nova_pessoa, null);

                child.findViewById(R.id.pessoaNew).setTag(DataHolder.getInstance().getAdicionarPessoaIdPessoa());
                child.findViewById(R.id.imageClick).setOnLongClickListener(new View.OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View view) {

                        PopupMenu pop = new PopupMenu(AbordagensCadastrarAbordagemActivity.this, view);
                        pop.inflate(R.menu.menu_excluir_pessoa);

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

                ((ImageView)child.findViewById(R.id.pessoaNew)).setImageURI(DataHolder.getInstance().getAdicionarPessoaImgUri());

                YoYo.with(Techniques.BounceIn)
                        .duration(500)
                        .playOn(child);
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

    public void novaPessoaSnack(View view) {

        Snackbar.make(findViewById(android.R.id.content), "Pressione e segure para excluir a pessoa.", 1000).show();
    }

    public void novaMatriculaSnack(View view) {

        Snackbar.make(findViewById(android.R.id.content), "Pressione e segure para excluir a matrícula.", 1000).show();
    }

    public void buttonAdicionarLocal(View view) {

        pmLocal.show();
    }

    public void buttonAdicionarImagem(View view) {

        pmImagem.show();
    }

    public void buttonAdicionarPessoa(View view) {

        pmPessoa.show();
    }

    public void buttonAdicionarMatricula(View view) {

        pmMatricula.show();
    }

    public void adicionarLocal(MenuItem item) {

        escolherLocal();
    }

    public void adicionarImagem(MenuItem item) {

        if (item.getOrder() == 1) {

            dialogHelper.showProgress();

            CropImage.activity()
                    .setCropMenuCropButtonTitle("Pronto")
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(AbordagensCadastrarAbordagemActivity.this);
        }
    }

    public void adicionarPessoa(MenuItem item) {

        switch (item.getOrder()) {

            case 1:

                dialogHelper.inputDialog("Buscar pessoa", "Nome completo ou alcunha:", InputType.TYPE_TEXT_FLAG_CAP_WORDS | InputType.TYPE_TEXT_VARIATION_PERSON_NAME, new MaterialDialog.InputCallback() {

                    @Override
                    public void onInput(@NonNull MaterialDialog materialDialog, CharSequence charSequence) {

                        DataHolder.getInstance().setBuscarPessoaDataSimple(charSequence.toString());

                        dialogHelper.showProgressDelayed(500, new Runnable() {

                            @Override
                            public void run() {

                                Intent i = new Intent(AbordagensCadastrarAbordagemActivity.this, AdicionarPessoaActivity.class);
                                startActivityForResult(i, AdicionarPessoaActivity.CODE_ADICIONAR_PESSOA_ACTIVITY);
                            }
                        });
                    }
                });

                break;

            case 2:

                dialogHelper.showProgressDelayed(500, new Runnable() {

                    @Override
                    public void run() {

                        Intent i = new Intent(AbordagensCadastrarAbordagemActivity.this, PessoasCadastrarPessoaActivity.class);
                        startActivityForResult(i, PessoasCadastrarPessoaActivity.CODE_ACTIVITY_CADASTRAR_PESSOA);
                    }
                });

                break;
        }
    }

    public void adicionarMatricula(MenuItem item) {

        if (item.getOrder() == 1) {

            dialogHelper.matriculaDialog(new MaterialDialog.InputCallback() {
                @Override
                public void onInput(@NonNull MaterialDialog materialDialog, CharSequence charSequence) {

                    String matr = charSequence.toString();

                    if (matr.length() < 9) {

                        dialogHelper.showError("Verifique a matrícula digitada.");
                    }
                    else {

                        final ViewGroup vg = (ViewGroup)findViewById(R.id.layoutNewMatricula);
                        final View child = LayoutInflater.from(AbordagensCadastrarAbordagemActivity.this).inflate(R.layout.layout_nova_matricula, null);

                        child.findViewById(R.id.matriculaNew).setTag(matr);
                        ((TextView)child.findViewById(R.id.matricula)).setText(matr);
                        child.findViewById(R.id.imageClick).setOnLongClickListener(new View.OnLongClickListener() {

                            @Override
                            public boolean onLongClick(View view) {

                                PopupMenu pop = new PopupMenu(AbordagensCadastrarAbordagemActivity.this, view);
                                pop.inflate(R.menu.menu_excluir_matricula);

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
                    }
                }
            });
        }
    }
}
