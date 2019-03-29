package br.gov.pb.pm.sasp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONObject;

public class MainActivity extends SaspActivity {

    private DrawerLayout drawer;
    private JSONObject loginData;

    public static DialogHelper dialogHelper;
    public static SaspServer saspServer;

    private boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialogHelper = new DialogHelper(MainActivity.this);
        saspServer = new SaspServer(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.actionbar_layout, null);

        actionBar.setCustomView(v);

        drawer = findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (!ImageLoader.getInstance().isInited()) {

            DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                    .bitmapConfig(Bitmap.Config.ARGB_8888)
                    .cacheOnDisk(true)
                    .cacheInMemory(true)
                    .build();

            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                    .defaultDisplayImageOptions(imageOptions)
                    .diskCacheExtraOptions(640, 640, null)
                    .memoryCacheSize(16 * 1024 * 1024) // 16MB
                    .diskCacheSize(128 * 1024 * 1024)  // 128MB
                    .imageDownloader(new SaspImageDownloader())
                    .build();

            ImageLoader.getInstance().init(config);
        }

        loginData = DataHolder.getInstance().getLoginData();

        NavigationView navigationView = findViewById(R.id.nav_view);

        try {

            /*String nivel_acesso = loginData.getString("nivel_de_acesso");

            if (nivel_acesso.equals("1")) {

                nivel_acesso = "Nível de acesso: 1 - Padrão";
            }
            else if (nivel_acesso.equals("2")) {

                nivel_acesso = "Nível de acesso: 2 - Agente de Inteligência";

                navigationView.getHeaderView(0).findViewById(R.id.layoutNivel2).setVisibility(View.VISIBLE);
            }
            else if (nivel_acesso.equals("3")) {

                nivel_acesso = "Nível de acesso: 3 - Chefe de NI";

                navigationView.getHeaderView(0).findViewById(R.id.layoutNivel2).setVisibility(View.VISIBLE);
                navigationView.getHeaderView(0).findViewById(R.id.layoutNivel3).setVisibility(View.VISIBLE);
            }
            else if (nivel_acesso.equals("4")) {

                nivel_acesso = "Nível de acesso: 4 - Chefe de DRI";

                navigationView.getHeaderView(0).findViewById(R.id.layoutNivel2).setVisibility(View.VISIBLE);
                navigationView.getHeaderView(0).findViewById(R.id.layoutNivel3).setVisibility(View.VISIBLE);
                navigationView.getHeaderView(0).findViewById(R.id.layoutNivel4).setVisibility(View.VISIBLE);
            }
            else {

                nivel_acesso = "Nível de acesso: 5 - Coordenador";

                navigationView.getHeaderView(0).findViewById(R.id.layoutNivel2).setVisibility(View.VISIBLE);
                navigationView.getHeaderView(0).findViewById(R.id.layoutNivel3).setVisibility(View.VISIBLE);
                navigationView.getHeaderView(0).findViewById(R.id.layoutNivel4).setVisibility(View.VISIBLE);
                navigationView.getHeaderView(0).findViewById(R.id.layoutNivel5).setVisibility(View.VISIBLE);
            }*/

            ImageLoader.getInstance().displayImage(SaspServer.getImageAddress(loginData.getString("img_busca"), "usuarios", true), (ImageView)navigationView.getHeaderView(0).findViewById(R.id.imagemPerfil));

            ((TextView)navigationView.getHeaderView(0).findViewById(R.id.perfilNomeFuncional)).setText(loginData.getString("nome_funcional"));
            ((TextView)navigationView.getHeaderView(0).findViewById(R.id.perfilGrauHierarquico)).setText(loginData.getString("grau_hierarquico"));
            ((TextView)navigationView.getHeaderView(0).findViewById(R.id.perfilLotacao)).setText(loginData.getString("lotacao_atual").replace("|", "-"));
            ((TextView)navigationView.getHeaderView(0).findViewById(R.id.perfilCPF)).setText(AppUtils.formatarCPF(loginData.getString("cpf")));
            ((TextView)navigationView.getHeaderView(0).findViewById(R.id.perfilMatricula)).setText(AppUtils.formatarMatricula(loginData.getString("matricula")));
            ((TextView)navigationView.getHeaderView(0).findViewById(R.id.perfilPrimeiroAcesso)).setText(AppUtils.formatarData(loginData.getString("data_registro")));
            ((TextView)navigationView.getHeaderView(0).findViewById(R.id.perfilUltimoAcesso)).setText(AppUtils.formatarData(loginData.getString("ultimo_login")));
        }
        catch (Exception e) {

            if (AppUtils.DEBUG_MODE) Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();

        }

        Fragment fragment = new FragmentMainActivityMenu();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
        ft.replace(R.id.frameLayout, fragment, FragmentMainActivityMenu.id);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {

            drawer.closeDrawer(GravityCompat.START);
        }
        else {

            Fragment frag = getSupportFragmentManager().findFragmentByTag(FragmentMainActivityMenu.id);

            if (frag != null && frag.isResumed()) {

                if (!exit) {

                    exit = true;

                    Snackbar.make(findViewById(android.R.id.content), "Pressione novamente para sair.", 1000).show();

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {

                            exit = false;
                        }
                    }, 1000);
                }
                else {

                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i);

                    finishAffinity();
                }
            }
            else {

                super.onBackPressed();
            }
        }
    }

    @Override
    void onPermissionsChange(boolean confirmed) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PessoasBuscarPessoaActivity.CODE_ACTIVITY_BUSCAR_PESSOA) {

            if (resultCode == 1) {

                Fragment fragment = new PessoasFragmentMainActivityResultadoBusca();

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                ft.replace(R.id.frameLayout, fragment);
                ft.addToBackStack(null);
                ft.commitAllowingStateLoss();

            }
        }
        else if (requestCode == PessoasCadastrarPessoaActivity.CODE_ACTIVITY_CADASTRAR_PESSOA) {

            if (resultCode == 1) {

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        dialogHelper.showProgressDelayed(1000, new Runnable() {

                            @Override
                            public void run() {

                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {

                                        dialogHelper.showSuccess("Pessoa cadastrada.\n\nEm instantes o cadastro estará disponível no banco de dados.");
                                    }
                                });
                            }
                        });
                    }
                }, 500);
            }
        }
        else if (requestCode == AbordagensCadastrarAbordagemActivity.CODE_ACTIVITY_CADASTRAR_ABORDAGEM) {

            if (resultCode == 1) {

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        dialogHelper.showProgressDelayed(1000, new Runnable() {

                            @Override
                            public void run() {

                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {

                                        dialogHelper.showSuccess("Abordagem cadastrada.\n\nEm instantes o cadastro estará disponível no banco de dados.");
                                    }
                                });
                            }
                        });
                    }
                }, 500);
            }
        }
        else if (requestCode == AbordagensBuscarAbordagemActivity.CODE_ACTIVITY_BUSCAR_ABORDAGEM) {

            if (resultCode == 1) {

                Fragment fragment = new AbordagensFragmentMainActivityResultadoBusca();

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                ft.replace(R.id.frameLayout, fragment);
                ft.addToBackStack(null);
                ft.commitAllowingStateLoss();
            }
        }
    }
}
