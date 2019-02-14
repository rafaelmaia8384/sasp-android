package br.gov.pb.pm.sasp;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BuscarPessoaActivity extends SaspActivity {

    private RecyclerView recyclerView;
    private ArrayList<ListaPessoa> listaPessoas;
    private ListaPessoaAdapter listaPessoaAdapter;

    private DialogHelper dialogHelper;
    private SaspServer saspServer;

    private int index = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_pessoa);

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
    }

    @Override
    void onPermissionsChange(boolean confirmed) {

    }

    public void fecharJanela(View view) {

        finish();
    }
}
