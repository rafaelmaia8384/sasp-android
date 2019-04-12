package br.gov.pb.pm.sasp;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PessoasCadastrarMarcaCorporalActivity extends SaspActivity {

    public static final int CODE_ACTIVITY_CADASTRAR_MARCA_CORPORAL = 610;

    private Uri marcaUri;
    private RadioGroup radioGroup;
    private int parteCorpo;
    private DialogHelper dialogHelper;
    private SaspServer saspServer;
    private PopupMenu pmImagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.pessoas_activity_cadastrar_marca_corporal);

        dialogHelper = new DialogHelper(this);
        saspServer = new SaspServer(this);

        parteCorpo = 0;

        pmImagem = new PopupMenu(PessoasCadastrarMarcaCorporalActivity.this, findViewById(R.id.viewPerfil));
        pmImagem.inflate(R.menu.menu_selecionar_imagem_perfil);
        pmImagem.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                if (menuItem.getOrder() == 1) {

                    dialogHelper.showProgress();

                    CropImage.activity()
                            .setCropMenuCropButtonTitle("Pronto")
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setOutputCompressQuality(90)
                            .setRequestedSize(840, 840)
                            .start(PessoasCadastrarMarcaCorporalActivity.this);
                }

                return false;
            }
        });

        ((Spinner)findViewById(R.id.spinnerTipoMarca)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 1) {

                    findViewById(R.id.layoutMarcaTatuagem).setVisibility(View.VISIBLE);
                }
                else {

                    findViewById(R.id.layoutMarcaTatuagem).setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        radioGroup = findViewById(R.id.radioGroup);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                if (i == R.id.radio1) {

                    parteCorpo = 1;

                    findViewById(R.id.imgParteNenhum).setVisibility(View.GONE);
                    findViewById(R.id.imgCabecaPescoco).setVisibility(View.VISIBLE);
                    findViewById(R.id.imgMembroSuperior).setVisibility(View.GONE);
                    findViewById(R.id.imgTronco).setVisibility(View.GONE);
                    findViewById(R.id.imgMembroInferior).setVisibility(View.GONE);
                }
                else if (i == R.id.radio2) {

                    parteCorpo = 2;

                    findViewById(R.id.imgParteNenhum).setVisibility(View.GONE);
                    findViewById(R.id.imgCabecaPescoco).setVisibility(View.GONE);
                    findViewById(R.id.imgMembroSuperior).setVisibility(View.VISIBLE);
                    findViewById(R.id.imgTronco).setVisibility(View.GONE);
                    findViewById(R.id.imgMembroInferior).setVisibility(View.GONE);
                }
                else if (i == R.id.radio3) {

                    parteCorpo = 3;

                    findViewById(R.id.imgParteNenhum).setVisibility(View.GONE);
                    findViewById(R.id.imgCabecaPescoco).setVisibility(View.GONE);
                    findViewById(R.id.imgMembroSuperior).setVisibility(View.GONE);
                    findViewById(R.id.imgTronco).setVisibility(View.VISIBLE);
                    findViewById(R.id.imgMembroInferior).setVisibility(View.GONE);
                }
                else {

                    parteCorpo = 4;

                    findViewById(R.id.imgParteNenhum).setVisibility(View.GONE);
                    findViewById(R.id.imgCabecaPescoco).setVisibility(View.GONE);
                    findViewById(R.id.imgMembroSuperior).setVisibility(View.GONE);
                    findViewById(R.id.imgTronco).setVisibility(View.GONE);
                    findViewById(R.id.imgMembroInferior).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    void onPermissionsChange(boolean confirmed) {

    }

    public void fecharJanela(View view) {

        finish();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            dialogHelper.dismissProgress();

            if (resultCode == RESULT_OK) {

                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                marcaUri = result.getUri();

                findViewById(R.id.imagemPerfil).setTag(result.getUri());
                ((ImageView) findViewById(R.id.imagemPerfil)).setImageURI(marcaUri);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void buttonSelecionarImagem(View view) {

        pmImagem.show();
    }

    public void buttonCadastrarMarcaCorporal(View view) {

        if (marcaUri == null) {

            dialogHelper.showError("Selecione a foto da marca corporal.");

            return;
        }

        int tipoMarca = ((Spinner)findViewById(R.id.spinnerTipoMarca)).getSelectedItemPosition();
        String tipoTatuagem = "";

        if (tipoMarca == 0) {

            dialogHelper.showError("Selecione o tipo de marca corporal.");

            return;
        }
        else if (tipoMarca == 1) {

            tipoTatuagem = ((EditText)findViewById(R.id.editTextTipoTatuagem)).getText().toString();

            if (tipoTatuagem.length() < 2) {

                dialogHelper.showError("Escreva o tipo de tatuagem.");

                return;
            }
        }
        else {

            tipoTatuagem = "null";
        }

        if (parteCorpo == 0) {

            dialogHelper.showError("Selecione a parte do corpo.");

            return;
        }

        DataHolder.getInstance().setAdicionarPessoaMarca(tipoMarca + "#%#" + tipoTatuagem + "#%#" + parteCorpo);

        dialogHelper.showProgressDelayed(500, new Runnable() {

            @Override
            public void run() {

                setResult(1, getIntent().putExtra("marcaUri", marcaUri));

                finish();
            }
        });
    }
}
