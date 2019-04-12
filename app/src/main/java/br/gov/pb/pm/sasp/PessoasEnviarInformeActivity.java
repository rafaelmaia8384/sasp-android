package br.gov.pb.pm.sasp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

public class PessoasEnviarInformeActivity extends SaspActivity {

    public static DialogHelper dialogHelper;
    public static SaspServer saspServer;

    private int index = 1;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.pessoas_activity_enviar_informe);

        dialogHelper = new DialogHelper(this);
        saspServer = new SaspServer(this);

        Fragment fragment = new PessoasFragmentEnviarInformeActivityPessoas();

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
}
