package br.gov.pb.pm.sasp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class PessoasBuscarPessoaMarcaActivity extends SaspActivity {

    public static final int CODE_ACTIVITY_BUSCAR_PESSOA_MARCA = 139;

    private DialogHelper dialogHelper;
    private SaspServer saspServer;

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
}
