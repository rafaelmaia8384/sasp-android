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
import android.widget.TextView;

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
    private boolean frente = true;
    private int parteCorpo = 0;
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

        final TabLayout tabLayout = findViewById(R.id.tabLayout);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == 0) {

                    frente = true;

                    parteCorpo = 0;

                    findViewById(R.id.imagemCorpo).setBackgroundResource(R.drawable.parte_frente);
                    ((TextView)findViewById(R.id.textParteCorpo)).setText("");
                }
                else {

                    frente = false;

                    parteCorpo = 0;

                    findViewById(R.id.imagemCorpo).setBackgroundResource(R.drawable.parte_costas);
                    ((TextView)findViewById(R.id.textParteCorpo)).setText("");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

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

    public void buttonCabecaPescoco(View view) {

        if (frente) {

            parteCorpo = 1;

            findViewById(R.id.imagemCorpo).setBackgroundResource(R.drawable.parte_frente_cabeca_pescoco);
            ((TextView)findViewById(R.id.textParteCorpo)).setText("Cabeça ou pescoço");
        }
        else {

            parteCorpo = 2;

            findViewById(R.id.imagemCorpo).setBackgroundResource(R.drawable.parte_costas_cabeca_pescoco);
            ((TextView)findViewById(R.id.textParteCorpo)).setText("Cabeça ou pescoço");
        }
    }

    public void buttonMembroSuperior1(View view) {

        if (frente) {

            parteCorpo = 3;

            findViewById(R.id.imagemCorpo).setBackgroundResource(R.drawable.parte_frente_membro_superior1);
            ((TextView)findViewById(R.id.textParteCorpo)).setText("Membro superior direito");
        }
        else {

            parteCorpo = 4;

            findViewById(R.id.imagemCorpo).setBackgroundResource(R.drawable.parte_costas_membro_superior1);
            ((TextView)findViewById(R.id.textParteCorpo)).setText("Membro superior esquerdo");
        }
    }

    public void buttonTroncoCostas(View view) {

        if (frente) {

            parteCorpo = 5;

            findViewById(R.id.imagemCorpo).setBackgroundResource(R.drawable.parte_frente_tronco_costas);
            ((TextView)findViewById(R.id.textParteCorpo)).setText("Tórax e abdómen");
        }
        else {

            parteCorpo = 6;

            findViewById(R.id.imagemCorpo).setBackgroundResource(R.drawable.parte_costas_tronco_costas);
            ((TextView)findViewById(R.id.textParteCorpo)).setText("Costas");
        }
    }

    public void buttonMembroSuperior2(View view) {

        if (frente) {

            parteCorpo = 7;

            findViewById(R.id.imagemCorpo).setBackgroundResource(R.drawable.parte_frente_membro_superior2);
            ((TextView)findViewById(R.id.textParteCorpo)).setText("Membro superior esquerdo");
        }
        else {

            parteCorpo = 8;

            findViewById(R.id.imagemCorpo).setBackgroundResource(R.drawable.parte_costas_membro_superior2);
            ((TextView)findViewById(R.id.textParteCorpo)).setText("Membro superior direito");
        }
    }

    public void buttonMembroInferior1(View view) {

        if (frente) {

            parteCorpo = 9;

            findViewById(R.id.imagemCorpo).setBackgroundResource(R.drawable.parte_frente_membro_inferior1);
            ((TextView)findViewById(R.id.textParteCorpo)).setText("Membro inferior direito");
        }
        else {

            parteCorpo = 10;

            findViewById(R.id.imagemCorpo).setBackgroundResource(R.drawable.parte_costas_membro_inferior1);
            ((TextView)findViewById(R.id.textParteCorpo)).setText("Membro inferior esquerdo");
        }
    }

    public void buttonMembroInferior2(View view) {

        if (frente) {

            parteCorpo = 11;

            findViewById(R.id.imagemCorpo).setBackgroundResource(R.drawable.parte_frente_membro_inferior2);
            ((TextView)findViewById(R.id.textParteCorpo)).setText("Membro inferior esquerdo");
        }
        else {

            parteCorpo = 12;

            findViewById(R.id.imagemCorpo).setBackgroundResource(R.drawable.parte_costas_membro_inferior2);
            ((TextView)findViewById(R.id.textParteCorpo)).setText("Membro inferior direito");
        }
    }
}
