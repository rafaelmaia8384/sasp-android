package br.gov.pb.pm.sasp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

public class PessoasBuscarPessoaMarcaActivity extends SaspActivity {

    public static final int CODE_ACTIVITY_BUSCAR_PESSOA_MARCA = 139;

    private DialogHelper dialogHelper;
    private SaspServer saspServer;
    private boolean frente = true;
    private RadioGroup radioGroup;
    private int parteCorpo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.pessoas_activity_buscar_pessoa_marca);

        dialogHelper = new DialogHelper(this);
        saspServer = new SaspServer(this);

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

    public static void hideKeyboard(Activity activity) {

        View view = activity.findViewById(android.R.id.content);

        if (view != null) {

            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void buttonBuscar(View view) {

        int tipoMarca = ((Spinner)findViewById(R.id.spinnerTipoMarca)).getSelectedItemPosition();

        String tipoTatuagem = "null";

        if (tipoMarca == 0) {

            dialogHelper.showError("Selecione o tipo de marca corporal.");

            return;
        }
        else if (tipoMarca == 1) {

            tipoTatuagem = ((EditText)findViewById(R.id.editTextTipoTatuagem)).getText().toString();

            if (tipoTatuagem.length() == 0) {

                tipoTatuagem = "null";
            }
        }

        if (parteCorpo == 0) {

            dialogHelper.showError("Selecione a parte do corpo.");

            return;
        }

        DataHolder.getInstance().setBuscarPessoaMarcaData(tipoMarca, tipoTatuagem, parteCorpo);

        dialogHelper.showProgressDelayed(500, new Runnable() {

            @Override
            public void run() {

                setResult(1);

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
