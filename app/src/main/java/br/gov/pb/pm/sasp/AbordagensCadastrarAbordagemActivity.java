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

import java.io.File;

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

        pmLocal = new PopupMenu(AbordagensCadastrarAbordagemActivity.this, findViewById(R.id.viewLocal));
        pmLocal.inflate(R.menu.menu_adicionar_local);
        pmPessoa = new PopupMenu(AbordagensCadastrarAbordagemActivity.this, findViewById(R.id.viewAddPessoa));
        pmPessoa.inflate(R.menu.menu_adicionar_pessoa);
        pmImagem = new PopupMenu(AbordagensCadastrarAbordagemActivity.this, findViewById(R.id.viewAddImage));
        pmImagem.inflate(R.menu.menu_adicionar_imagem);
        pmMatricula = new PopupMenu(AbordagensCadastrarAbordagemActivity.this, findViewById(R.id.viewAddMatricula));
        pmMatricula.inflate(R.menu.menu_adicionar_matricula);
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

                child.setTag(DataHolder.getInstance().getAdicionarPessoaIdPessoa());
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

                        child.setTag(matr);
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
