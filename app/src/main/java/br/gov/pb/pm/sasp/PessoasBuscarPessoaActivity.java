package br.gov.pb.pm.sasp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

public class PessoasBuscarPessoaActivity extends SaspActivity {

    public static final int CODE_ACTIVITY_BUSCAR_PESSOA = 100;

    private RecyclerView recyclerView;
    private ArrayList<ListaPessoa> listaPessoas;
    private ListaPessoaAdapter listaPessoaAdapter;

    private DialogHelper dialogHelper;
    private SaspServer saspServer;

    private int index = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.pessoas_activity_buscar_pessoa);

        dialogHelper = new DialogHelper(this);
        saspServer = new SaspServer(this);

        findViewById(R.id.buttonMaisOpcoesDeBusca).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                findViewById(R.id.buttonMaisOpcoesDeBusca).setVisibility(View.GONE);
                findViewById(R.id.layoutMaisOpcoes).setVisibility(View.VISIBLE);
                findViewById(R.id.buttonMenosOpcoesDeBusca).setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.buttonMenosOpcoesDeBusca).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                ((CheckBox)findViewById(R.id.checkBoxAC)).setChecked(false);
                ((CheckBox)findViewById(R.id.checkBoxAL)).setChecked(false);
                ((CheckBox)findViewById(R.id.checkBoxAM)).setChecked(false);
                ((CheckBox)findViewById(R.id.checkBoxAP)).setChecked(false);
                ((CheckBox)findViewById(R.id.checkBoxBA)).setChecked(false);
                ((CheckBox)findViewById(R.id.checkBoxCE)).setChecked(false);
                ((CheckBox)findViewById(R.id.checkBoxDF)).setChecked(false);
                ((CheckBox)findViewById(R.id.checkBoxES)).setChecked(false);
                ((CheckBox)findViewById(R.id.checkBoxGO)).setChecked(false);
                ((CheckBox)findViewById(R.id.checkBoxMA)).setChecked(false);
                ((CheckBox)findViewById(R.id.checkBoxMG)).setChecked(false);
                ((CheckBox)findViewById(R.id.checkBoxMS)).setChecked(false);
                ((CheckBox)findViewById(R.id.checkBoxMT)).setChecked(false);
                ((CheckBox)findViewById(R.id.checkBoxPA)).setChecked(false);
                ((CheckBox)findViewById(R.id.checkBoxPB)).setChecked(false);
                ((CheckBox)findViewById(R.id.checkBoxPE)).setChecked(false);
                ((CheckBox)findViewById(R.id.checkBoxPI)).setChecked(false);
                ((CheckBox)findViewById(R.id.checkBoxPR)).setChecked(false);
                ((CheckBox)findViewById(R.id.checkBoxRJ)).setChecked(false);
                ((CheckBox)findViewById(R.id.checkBoxRN)).setChecked(false);
                ((CheckBox)findViewById(R.id.checkBoxRO)).setChecked(false);
                ((CheckBox)findViewById(R.id.checkBoxRR)).setChecked(false);
                ((CheckBox)findViewById(R.id.checkBoxRS)).setChecked(false);
                ((CheckBox)findViewById(R.id.checkBoxSC)).setChecked(false);
                ((CheckBox)findViewById(R.id.checkBoxSE)).setChecked(false);
                ((CheckBox)findViewById(R.id.checkBoxSP)).setChecked(false);
                ((CheckBox)findViewById(R.id.checkBoxTO)).setChecked(false);

                ((CheckBox)findViewById(R.id.check_furto)).setChecked(false);
                ((CheckBox)findViewById(R.id.check_roubo)).setChecked(false);
                ((CheckBox)findViewById(R.id.check_roubo_bancos)).setChecked(false);
                ((CheckBox)findViewById(R.id.check_trafico)).setChecked(false);
                ((CheckBox)findViewById(R.id.check_homicidio)).setChecked(false);
                ((CheckBox)findViewById(R.id.check_latrocinio)).setChecked(false);
                ((CheckBox)findViewById(R.id.check_estupro)).setChecked(false);
                ((CheckBox)findViewById(R.id.check_estelionato)).setChecked(false);
                ((CheckBox)findViewById(R.id.check_posse_porte)).setChecked(false);
                ((CheckBox)findViewById(R.id.check_receptacao)).setChecked(false);
                ((CheckBox)findViewById(R.id.check_contrabando)).setChecked(false);
                ((CheckBox)findViewById(R.id.check_outros)).setChecked(false);

                ((Spinner)findViewById(R.id.spinnerCrtCorPele)).setSelection(0);
                ((Spinner)findViewById(R.id.spinnerCrtCorOlhos)).setSelection(0);
                ((Spinner)findViewById(R.id.spinnerCrtCorCabelos)).setSelection(0);
                ((Spinner)findViewById(R.id.spinnerCrtTipoCabelos)).setSelection(0);
                ((Spinner)findViewById(R.id.spinnerCrtPorteFisico)).setSelection(0);
                ((Spinner)findViewById(R.id.spinnerCrtEstatura)).setSelection(0);
                ((Spinner)findViewById(R.id.spinnerCrtDeficiente)).setSelection(0);
                ((Spinner)findViewById(R.id.spinnerCrtPossuiTatuagem)).setSelection(0);

                findViewById(R.id.buttonMaisOpcoesDeBusca).setVisibility(View.VISIBLE);
                findViewById(R.id.layoutMaisOpcoes).setVisibility(View.GONE);
                findViewById(R.id.buttonMenosOpcoesDeBusca).setVisibility(View.GONE);
            }
        });

        findViewById(R.id.buttonBuscar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

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

                final int crtCorPele = ((Spinner)findViewById(R.id.spinnerCrtCorPele)).getSelectedItemPosition() + 1;
                final int crtCorOlhos = ((Spinner)findViewById(R.id.spinnerCrtCorOlhos)).getSelectedItemPosition() + 1;
                final int crtCorCabelos = ((Spinner)findViewById(R.id.spinnerCrtCorCabelos)).getSelectedItemPosition() + 1;
                final int crtTipoCabelos = ((Spinner)findViewById(R.id.spinnerCrtTipoCabelos)).getSelectedItemPosition() + 1;
                final int crtPorteFisico = ((Spinner)findViewById(R.id.spinnerCrtPorteFisico)).getSelectedItemPosition() + 1;
                final int crtEstatura = ((Spinner)findViewById(R.id.spinnerCrtEstatura)).getSelectedItemPosition() + 1;
                final int crtDeficiente = ((Spinner)findViewById(R.id.spinnerCrtDeficiente)).getSelectedItemPosition() + 1;
                final int crtTatuagem = ((Spinner)findViewById(R.id.spinnerCrtPossuiTatuagem)).getSelectedItemPosition() + 1;

                int check = 0;

                String nome_alcunha = ((EditText)findViewById(R.id.editTextNomeAlcunha)).getText().toString();

                if (areas_de_atuacao == 0) {

                    areas_de_atuacao = -1;

                    check++;
                }

                if (historico == 0) {

                    historico = -1;

                    check++;
                }

                if (crtCorPele == 1              &&
                        crtCorOlhos == 1         &&
                        crtCorCabelos == 1       &&
                        crtTipoCabelos == 1      &&
                        crtPorteFisico == 1      &&
                        crtEstatura == 1         &&
                        crtDeficiente == 1       &&
                        crtTatuagem == 1) {

                    check++;
                }

                if (findViewById(R.id.layoutMaisOpcoes).getVisibility() == View.VISIBLE) {

                    if (check >= 3 && nome_alcunha.length() < 2) {

                        dialogHelper.showError("Preencha pelo menos uma opção de busca.");

                        return;
                    }
                }
                else {

                    if (nome_alcunha.length() < 2) {

                        dialogHelper.showError("Digite o nome ou alcunha do suspeito.");

                        return;
                    }
                }

                if (nome_alcunha.length() < 2) {

                    nome_alcunha = "$%null%$";
                }

                DataHolder.getInstance().setBuscarPessoaData(nome_alcunha, areas_de_atuacao, historico, crtCorPele, crtCorOlhos, crtCorCabelos, crtTipoCabelos, crtPorteFisico, crtEstatura, crtDeficiente, crtTatuagem);
                hideKeyboard(PessoasBuscarPessoaActivity.this);

                setResult(1);
                finish();
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
}
