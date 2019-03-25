package br.gov.pb.pm.sasp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;

import java.io.File;
import java.util.ArrayList;

public class AbordagensBuscarAbordagemActivity extends SaspActivity {

    public static final int CODE_ACTIVITY_BUSCAR_ABORDAGEM = 872;

    private DialogHelper dialogHelper;
    private SaspServer saspServer;
    private PopupMenu pmLocal;
    private Uri imgLocalUri;

    private int index = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.abordagens_activity_buscar_abordagem);

        dialogHelper = new DialogHelper(this);
        saspServer = new SaspServer(this);

        pmLocal = new PopupMenu(AbordagensBuscarAbordagemActivity.this, findViewById(R.id.viewLocal));
        pmLocal.inflate(R.menu.menu_adicionar_local);
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

    public void buttonAdicionarLocal(View view) {

        pmLocal.show();
    }

    public void adicionarLocal(MenuItem item) {

        escolherLocal();
    }

    private void escolherLocal() {

        dialogHelper.showProgress();

        Intent i = new Intent(AbordagensBuscarAbordagemActivity.this, LocationPickerActivity.class);
        i.putExtra("buscarAbordagem", true);
        startActivityForResult(i, LocationPickerActivity.LOCATION_PICKER_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LocationPickerActivity.LOCATION_PICKER_INTENT) {

            if (resultCode == 1) {

                findViewById(R.id.spinnerDistanciaMaxima).setVisibility(View.VISIBLE);

                imgLocalUri = Uri.fromFile((File)data.getExtras().get("imgLocal"));
                ((ImageView)findViewById(R.id.imagemGPS)).setImageURI(imgLocalUri);
            }

            dialogHelper.dismissProgress();
        }
    }

    public void buttonBuscarAbordagem(View view) {

        String nome_alcunha = ((EditText)findViewById(R.id.editTextNomeAlcunha)).getText().toString();

        if (nome_alcunha.length() > 0) {

            if (nome_alcunha.length() < 2) {

                dialogHelper.showError("Verifique o nome digitado.");

                return;
            }
        }
        else {

            nome_alcunha = "-1";
        }

        String dist = "-1";

        String latitude = "-1";
        String longitude = "-1";

        if (imgLocalUri != null) {

            dist = Integer.toString(((Spinner)findViewById(R.id.spinnerDistanciaMaxima)).getSelectedItemPosition());

            if (dist.equals("0")) {

                dialogHelper.showError("Selecione a distância máxima.");

                return;
            }

            latitude = DataHolder.getInstance().getCadastroAbordagemLatitude();
            longitude = DataHolder.getInstance().getCadastroAbordagemLongitude();
        }
        else {

            if (nome_alcunha.equals("-1")) {

                dialogHelper.showError("Informe o local ou o nome do indivíduo para realizar a busca.");

                return;
            }
        }

        DataHolder.getInstance().setBuscarAbordagemData(latitude, longitude, nome_alcunha, dist);

        setResult(1);
        finish();
    }
}
