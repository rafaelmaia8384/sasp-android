package br.gov.pb.pm.sasp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONObject;

import java.util.ArrayList;

public class AdicionarPessoaActivity extends SaspActivity {

    public static final int CODE_ADICIONAR_PESSOA_ACTIVITY = 322;

    private RecyclerView recyclerView;
    private ArrayList<ListaPessoas> listaPessoas;
    private ListaPessoasAdapter listaPessoaAdapter;
    private SwipeRefreshLayout refreshLayout;

    public static DialogHelper dialogHelper;
    public static SaspServer saspServer;

    private int index = 1;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_pessoas);

        dialogHelper = new DialogHelper(this);
        saspServer = new SaspServer(this);

        Fragment fragment = new FragmentAdicionarPessoaActivity();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
        ft.replace(R.id.frameLayout, fragment, PessoasFragmentMeusCadastrosActivityMeusCadastros.id);
        ft.commitAllowingStateLoss();
    }

    @Override
    void onPermissionsChange(boolean confirmed) {

    }

    public void fecharJanela(View view) {

        finish();
    }

    public void adicionarPessoa(MenuItem item) {

        switch (item.getOrder()) {

            case 1:

                setResult(1);
                finish();

                break;

            case 2:

                String id_pessoa = DataHolder.getInstance().getAdicionarPessoaIdPessoa();

                dialogHelper.showProgress();

                saspServer.pessoasPerfil(id_pessoa, new SaspResponse(AdicionarPessoaActivity.this) {

                    @Override
                    void onSaspResponse(String error, String msg, JSONObject extra) {

                        DataHolder.getInstance().setPessoaData(extra);

                        Intent i = new Intent(AdicionarPessoaActivity.this, PessoasPerfilPessoaActivity.class);
                        startActivityForResult(i, 400);
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

                        dialogHelper.dismissProgress();
                    }
                });

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PessoasPerfilPessoaActivity.CODE_PESSOAS_PERFIL_PESSOA) {

            if (resultCode == 1) {

                setResult(1);
                finish();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
