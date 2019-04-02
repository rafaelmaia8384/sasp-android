package br.gov.pb.pm.sasp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

public class AbordagensMeusCadastrosActivity extends SaspActivity {

    public static DialogHelper dialogHelper;
    public static SaspServer saspServer;

    private int index = 1;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.abordagens_activity_meus_cadastros);

        dialogHelper = new DialogHelper(this);
        saspServer = new SaspServer(this);

        Fragment fragment = new AbordagensFragmentMeusCadastrosActivityMeusCadastros();

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
