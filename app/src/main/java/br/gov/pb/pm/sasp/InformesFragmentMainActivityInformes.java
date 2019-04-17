package br.gov.pb.pm.sasp;

import android.animation.Animator;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import im.delight.android.location.SimpleLocation;


public class InformesFragmentMainActivityInformes extends Fragment {

    public static final String id = "FRAGMENT_INFORMES";

    private PopupMenu pmLocal;
    private PopupMenu pmImagem;
    private PopupMenu pmPessoa;
    private PopupMenu pmVeiculo;

    private SimpleLocation simpleLocation;

    private Uri imgLocalUri;

    private List<String> listaMunicipios;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.informes_fragment_mainactivity_informe, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        simpleLocation = new SimpleLocation(getActivity());

        listaMunicipios = Arrays.asList(getResources().getStringArray(R.array.municipios));

        AutoCompleteTextView textMunicipio = getActivity().findViewById(R.id.editTextMunicipio);
        textMunicipio.setAdapter(new IgnoreAccentsArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listaMunicipios));

        pmLocal = new PopupMenu(getActivity(), getActivity().findViewById(R.id.viewLocal));
        pmLocal.inflate(R.menu.menu_adicionar_local);
        pmLocal.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                escolherLocal();

                return false;
            }
        });

        getActivity().findViewById(R.id.viewLocal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pmLocal.show();
            }
        });

        pmImagem = new PopupMenu(getActivity(), getActivity().findViewById(R.id.viewAddImage));
        pmImagem.inflate(R.menu.menu_adicionar_imagem);
        pmImagem.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                if (menuItem.getOrder() == 1) {

                    MainActivity.dialogHelper.showProgress();

                    CropImage.activity()
                            .setCropMenuCropButtonTitle("Pronto")
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setOutputCompressQuality(90)
                            .setRequestedSize(840, 840)
                            .start(getContext(), InformesFragmentMainActivityInformes.this);
                }

                return false;
            }
        });

        getActivity().findViewById(R.id.viewAddImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pmImagem.show();
            }
        });

        pmPessoa = new PopupMenu(getActivity(), getActivity().findViewById(R.id.viewAddPessoa));
        pmPessoa.inflate(R.menu.menu_adicionar_pessoa_informe);
        pmPessoa.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                if (menuItem.getOrder() == 1) {

                    MaterialDialog.SingleButtonCallback dialogCallback = new MaterialDialog.SingleButtonCallback() {

                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog, @NonNull final DialogAction dialogAction) {

                            String cpf = ((EditText) materialDialog.getCustomView().findViewById(R.id.editTextCPF)).getText().toString();
                            String nome_completo = ((EditText) materialDialog.getCustomView().findViewById(R.id.editTextNomeCompleto)).getText().toString();

                            nome_completo = AppUtils.formatarTexto(nome_completo);

                            if (cpf.length() == 14) {

                                if (!AppUtils.validarCPF(cpf)) {

                                    MainActivity.dialogHelper.showError("Verifique o CPF.");

                                    return;
                                }
                            } else if (cpf.length() == 0) {

                                cpf = "-1";
                            }

                            if (nome_completo.length() == 0) {

                                nome_completo = "-1";
                            } else if (nome_completo.split(" ").length < 2) {

                                MainActivity.dialogHelper.showError("Verifique o nome completo do indivíduo.");

                                return;
                            }

                            if (cpf.equals("-1") && nome_completo.equals("-1")) {

                                MainActivity.dialogHelper.showError("Preencha pelo menos um campo para continuar.");

                                return;
                            }

                            DataHolder.getInstance().setBuscarPessoaDataSimple(cpf, nome_completo);

                            MainActivity.dialogHelper.showProgress();

                            MainActivity.saspServer.pessoasBuscarPessoaSimple(1, new SaspResponse(getActivity()) {

                                @Override
                                void onSaspResponse(String error, String msg, JSONObject extra) {

                                    if (error.equals("0")) {

                                        Intent i = new Intent(getActivity(), AdicionarPessoaActivity.class);
                                        startActivityForResult(i, AdicionarPessoaActivity.CODE_ADICIONAR_PESSOA_ACTIVITY);
                                    } else {

                                        DataHolder.getInstance().setBuscarPessoaDataSimple("", "");

                                        Intent i = new Intent(getActivity(), PessoasCadastrarPessoaActivity.class);
                                        startActivityForResult(i, PessoasCadastrarPessoaActivity.CODE_ACTIVITY_CADASTRAR_PESSOA);
                                    }
                                }

                                @Override
                                void onResponse(String error) {

                                }

                                @Override
                                void onNoResponse(String error) {

                                }

                                @Override
                                void onPostResponse() {

                                    if (!isVisible()) return;

                                    MainActivity.dialogHelper.dismissProgress();
                                }
                            });
                        }
                    };

                    MaterialDialog adicionarPessoaDialog = new MaterialDialog.Builder(getActivity())
                            .title("Dados")
                            .customView(R.layout.layout_abordagens_adicionar_pessoa, true)
                            .positiveText("OK")
                            .negativeText("Cancelar")
                            .canceledOnTouchOutside(true)
                            .cancelable(true)
                            .onPositive(dialogCallback)
                            .build();

                    EditText editCPF = (EditText) adicionarPessoaDialog.getCustomView().findViewById(R.id.editTextCPF);
                    TextWatcher mascaraMatricula = MascaraCPF.insert("###.###.###-##", editCPF);
                    editCPF.addTextChangedListener(mascaraMatricula);

                    adicionarPessoaDialog.show();
                } else if (menuItem.getOrder() == 2) {

                    MaterialDialog.SingleButtonCallback dialogCallback = new MaterialDialog.SingleButtonCallback() {

                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog, @NonNull final DialogAction dialogAction) {

                            String matricula = ((EditText) materialDialog.getCustomView().findViewById(R.id.editTextMatricula)).getText().toString();
                            String nome_completo = ((EditText) materialDialog.getCustomView().findViewById(R.id.editTextNomeCompleto)).getText().toString();
                            String municipio = ((AutoCompleteTextView) materialDialog.getCustomView().findViewById(R.id.editTextMunicipio)).getText().toString();

                            nome_completo = AppUtils.formatarTexto(nome_completo);

                            if (matricula.length() == 0 || !AppUtils.validarMatricula(matricula)) {

                                MainActivity.dialogHelper.showError("Verifique a matrícula.");

                                return;
                            }

                            if (nome_completo.length() == 0 || nome_completo.split(" ").length < 2) {

                                MainActivity.dialogHelper.showError("Verifique o nome completo do servidor.");

                                return;
                            }

                            if (municipio.length() < 3) {

                                MainActivity.dialogHelper.showError("Verifique o município.");

                                return;
                            }

                            final ViewGroup vg = (ViewGroup) getActivity().findViewById(R.id.layoutNewPessoa);
                            final View child = LayoutInflater.from(getActivity()).inflate(R.layout.layout_nova_pessoa_servidor, null);

                            child.findViewById(R.id.pessoaNew).setTag(matricula + "#%#" + nome_completo + "#%#" + municipio);
                            child.findViewById(R.id.imageClick).setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View view) {

                                    PopupMenu pop = new PopupMenu(getActivity(), view);
                                    pop.inflate(R.menu.menu_informes_servidor);

                                    pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                        @Override
                                        public boolean onMenuItemClick(MenuItem menuItem) {

                                            YoYo.with(Techniques.ZoomOut)
                                                    .duration(500)
                                                    .onEnd(new YoYo.AnimatorCallback() {

                                                        @Override
                                                        public void call(Animator animator) {

                                                            vg.removeView(child);
                                                        }
                                                    })
                                                    .playOn(child);

                                            return false;
                                        }
                                    });

                                    pop.show();
                                }
                            });

                            vg.addView(child, 0);

                            YoYo.with(Techniques.BounceIn)
                                    .duration(500)
                                    .playOn(child);
                        }
                    };

                    MaterialDialog adicionarPessoaDialog = new MaterialDialog.Builder(getActivity())
                            .title("Dados")
                            .customView(R.layout.layout_informes_adicionar_servidor_estadual, true)
                            .positiveText("OK")
                            .negativeText("Cancelar")
                            .canceledOnTouchOutside(true)
                            .cancelable(true)
                            .onPositive(dialogCallback)
                            .build();

                    EditText editMatricula = (EditText) adicionarPessoaDialog.getCustomView().findViewById(R.id.editTextMatricula);
                    TextWatcher mascaraMatricula = MascaraCPF.insert("###.###-#", editMatricula);
                    editMatricula.addTextChangedListener(mascaraMatricula);

                    AutoCompleteTextView textMunicipio = adicionarPessoaDialog.getCustomView().findViewById(R.id.editTextMunicipio);
                    textMunicipio.setAdapter(new IgnoreAccentsArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listaMunicipios));

                    adicionarPessoaDialog.show();
                }

                return false;
            }
        });

        getActivity().findViewById(R.id.viewAddPessoa).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pmPessoa.show();
            }
        });

        pmVeiculo = new PopupMenu(getActivity(), getActivity().findViewById(R.id.viewAddVeiculo));
        pmVeiculo.inflate(R.menu.menu_adicionar_veiculo);
        pmVeiculo.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                MainActivity.dialogHelper.showProgressDelayed(500, new Runnable() {

                    @Override
                    public void run() {

                        Intent i = new Intent(getActivity(), VeiculosCadastrarVeiculoActivity.class);
                        startActivityForResult(i, VeiculosCadastrarVeiculoActivity.CODE_ACTIVITY_CADASTRAR_VEICULO);
                    }
                });

                return false;
            }
        });

        getActivity().findViewById(R.id.viewAddVeiculo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pmVeiculo.show();
            }
        });

        getActivity().findViewById(R.id.buttonMeusCadastros).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                MainActivity.dialogHelper.showProgressDelayed(500, new Runnable() {

                    @Override
                    public void run() {

                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                MaterialDialog.SingleButtonCallback singleButtonCallback = new MaterialDialog.SingleButtonCallback() {

                                    @Override
                                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {

                                        String senha = ((EditText)materialDialog.getCustomView().findViewById(R.id.editTextSenha)).getText().toString();

                                        if (senha.length() < 6 || senha.length() > 20) {

                                            MainActivity.dialogHelper.showError("A senha deve conter entre 6 e 20 dígitos.");

                                            return;
                                        }

                                        DataHolder.getInstance().setInformesSenha(senha);

                                        MainActivity.dialogHelper.showProgressDelayed(500, new Runnable() {

                                            @Override
                                            public void run() {

                                                Intent i = new Intent(getActivity(), InformesMeusCadastrosActivity.class);
                                                startActivity(i);
                                            }
                                        });
                                    }
                                };

                                new MaterialDialog.Builder(getActivity())
                                        .customView(R.layout.layout_informes_abrir_informes, true)
                                        .title("Informes")
                                        .positiveText("OK")
                                        .onPositive(singleButtonCallback)
                                        .canceledOnTouchOutside(false)
                                        .cancelable(false)
                                        .show();
                            }
                        });
                    }
                });
            }
        });

        DataHolder.getInstance().setMapaLatitude("-1");
        DataHolder.getInstance().setMapaLongitude("-1");

        getActivity().findViewById(R.id.buttonEnviarInforme).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                final int natureza = ((Spinner) getActivity().findViewById(R.id.spinnerNatureza)).getSelectedItemPosition();
                final int area_opm = ((Spinner) getActivity().findViewById(R.id.spinnerOPM)).getSelectedItemPosition();
                final String municipio = ((AutoCompleteTextView) getActivity().findViewById(R.id.editTextMunicipio)).getText().toString();
                final String informe = AppUtils.formatarTexto(((EditText) getActivity().findViewById(R.id.editTextInforme)).getText().toString());

                final String senha1 = ((EditText) getActivity().findViewById(R.id.editTextSenha1)).getText().toString();
                final String senha2 = ((EditText) getActivity().findViewById(R.id.editTextSenha2)).getText().toString();

                if (natureza == 0) {

                    MainActivity.dialogHelper.showError("Selecione a natureza do informe.");

                    return;
                }

                if (area_opm == 0) {

                    MainActivity.dialogHelper.showError("Selecione a área do fato.");

                    return;
                }

                if (municipio.length() == 0) {

                    MainActivity.dialogHelper.showError("Verifique o município.");

                    return;
                }

                ViewGroup vg = (ViewGroup)getActivity().findViewById(R.id.layoutNewPessoa);

                if (vg.getChildCount() == 0) {

                    MainActivity.dialogHelper.showError("Adicione pelo menos uma pessoa relacionada ao informe.");

                    return;
                }

                if (informe.length() == 0) {

                    MainActivity.dialogHelper.showError("Digite seu informe.");

                    return;
                }
                else if (informe.split(" ").length < 2) {

                    MainActivity.dialogHelper.showError("Seu informe contém poucas informações.");

                    return;
                }

                if (!senha1.equals(senha2)) {

                    MainActivity.dialogHelper.showError("As senhas digitadas não conferem.");

                    return;
                }
                else if (senha1.length() < 6 || senha1.length() > 20) {

                    MainActivity.dialogHelper.showError("A senha deve conter entre 6 e 20 dígitos.");

                    return;
                }

                MainActivity.dialogHelper.showProgress();

                AsyncTask.execute(new Runnable() {

                    @Override
                    public void run() {

                        final List<SaspImage> imageList = new ArrayList<>();
                        final List<String> pessoaList = new ArrayList<>();
                        final List<String> veiculoList = new ArrayList<>();

                        ViewGroup vg = (ViewGroup)getActivity().findViewById(R.id.layoutNewImage);

                        for (int i = 0; i < vg.getChildCount(); i++) {

                            Uri imgUri = (Uri) vg.getChildAt(i).findViewById(R.id.imageNew).getTag();

                            SaspImage sp = new SaspImage(getActivity());
                            sp.salvarImagem(imgUri);

                            imageList.add(sp);
                        }

                        vg = (ViewGroup)getActivity().findViewById(R.id.layoutNewPessoa);

                        for (int i = 0; i < vg.getChildCount(); i++) {

                            String id_pessoa = (String) vg.getChildAt(i).findViewById(R.id.pessoaNew).getTag();
                            pessoaList.add(id_pessoa);
                        }

                        vg = (ViewGroup)getActivity().findViewById(R.id.layoutNewVeiculo);

                        for (int i = 0; i < vg.getChildCount(); i++) {

                            String id_pessoa = (String) vg.getChildAt(i).getTag();
                            veiculoList.add(id_pessoa);
                        }

                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                MainActivity.saspServer.informesCadastrar(Double.toString(simpleLocation.getLatitude()), Double.toString(simpleLocation.getLongitude()), natureza, area_opm, municipio, imageList, pessoaList, veiculoList, informe, senha1, new SaspResponse(getActivity()) {

                                    @Override
                                    void onSaspResponse(String error, String msg, JSONObject extra) {

                                        if (error.equals("1")) {

                                            if (!isVisible()) return;

                                            MainActivity.dialogHelper.showError(msg);
                                        } else {

                                            if (imageList.size() > 0) {

                                                for (int i = 0; i < imageList.size(); i++) {

                                                    imageList.get(i).saveUploadObject(SaspImage.UPLOAD_OBJECT_MODULO_INFORMES);
                                                }

                                                SaspServer.startServiceUploadImages(getActivity().getApplicationContext());
                                            }

                                            if (!isVisible()) return;

                                            //Se tiver sido chamado da MainActivity:
                                            getActivity().onBackPressed();
                                            MainActivity.dialogHelper.showSuccess("Seu informe foi enviado.\n\nAs informações enviadas serão analisadas pela Coordenadoria de Inteligência.");
                                        }
                                    }

                                    @Override
                                    void onResponse(String error) {

                                        if (!isVisible()) return;

                                        MainActivity.dialogHelper.showError(error);
                                    }

                                    @Override
                                    void onNoResponse(String error) {

                                        if (!isVisible()) return;

                                        MainActivity.dialogHelper.showError(error);
                                    }

                                    @Override
                                    void onPostResponse() {

                                        for (int a = 0; a < imageList.size(); a++) {

                                            imageList.get(a).delete();
                                        }

                                        if (!isVisible()) return;

                                        MainActivity.dialogHelper.dismissProgress();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                MainActivity.dialogHelper.showProgressDelayed(500, new Runnable() {

                    @Override
                    public void run() {

                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                new MaterialDialog.Builder(getActivity())
                                        .customView(R.layout.layout_aviso_sasp_envio_informe, true)
                                        .positiveText("OK")
                                        .canceledOnTouchOutside(false)
                                        .cancelable(false)
                                        .show();
                            }
                        });
                    }
                });
            }
        }, 500);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            if (resultCode == android.app.Activity.RESULT_OK) {

                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                final ViewGroup vg = (ViewGroup) getActivity().findViewById(R.id.layoutNewImage);
                final View child = LayoutInflater.from(getActivity()).inflate(R.layout.layout_nova_imagem, null);

                child.findViewById(R.id.imageClick).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        PopupMenu pop = new PopupMenu(getActivity(), view);
                        pop.inflate(R.menu.menu_abordagens_imagem);

                        pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {

                                if (menuItem.getOrder() == 1) {

                                    YoYo.with(Techniques.ZoomOut)
                                            .duration(500)
                                            .onEnd(new YoYo.AnimatorCallback() {

                                                @Override
                                                public void call(Animator animator) {

                                                    vg.removeView(child);
                                                }
                                            })
                                            .playOn(child);
                                }

                                return false;
                            }
                        });

                        pop.show();
                    }
                });

                vg.addView(child, 0);

                YoYo.with(Techniques.BounceIn)
                        .duration(500)
                        .playOn(child);

                ImageView novaImagem = child.findViewById(R.id.imageNew);

                novaImagem.setTag(result.getUri());
                novaImagem.setImageURI(result.getUri());
            }
        } else if (requestCode == LocationPickerActivity.LOCATION_PICKER_INTENT) {

            if (resultCode == 1) {

                imgLocalUri = Uri.fromFile((File) data.getExtras().get("imgLocal"));
                ((ImageView) getActivity().findViewById(R.id.imagemGPS)).setImageURI(imgLocalUri);
            }
        } else if (requestCode == AdicionarPessoaActivity.CODE_ADICIONAR_PESSOA_ACTIVITY) {

            if (resultCode == 1) {

                final ViewGroup vg = (ViewGroup) getActivity().findViewById(R.id.layoutNewPessoa);
                final View child = LayoutInflater.from(getActivity()).inflate(R.layout.layout_nova_pessoa, null);

                child.findViewById(R.id.pessoaNew).setTag(DataHolder.getInstance().getAdicionarPessoaIdPessoa());
                child.findViewById(R.id.imageClick).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        PopupMenu pop = new PopupMenu(getActivity(), view);
                        pop.inflate(R.menu.menu_abordagens_pessoa);

                        pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {

                                if (menuItem.getOrder() == 1) {

                                    MainActivity.dialogHelper.showProgress();

                                    MainActivity.saspServer.pessoasPerfil((String) child.findViewById(R.id.pessoaNew).getTag(), new SaspResponse(getActivity()) {

                                        @Override
                                        void onSaspResponse(String error, String msg, JSONObject extra) {

                                            if (error.equals("0")) {

                                                Intent i = new Intent(getActivity(), PessoasPerfilPessoaActivity.class);
                                                DataHolder.getInstance().setPessoaData(extra);
                                                startActivity(i);
                                            } else {

                                                MainActivity.dialogHelper.showError(msg);
                                            }
                                        }

                                        @Override
                                        void onResponse(String error) {

                                            MainActivity.dialogHelper.showError(error);
                                        }

                                        @Override
                                        void onNoResponse(String error) {

                                            MainActivity.dialogHelper.showError(error);
                                        }

                                        @Override
                                        void onPostResponse() {

                                            if (!isVisible()) return;

                                            MainActivity.dialogHelper.dismissProgress();
                                        }
                                    });
                                } else if (menuItem.getOrder() == 2) {

                                    YoYo.with(Techniques.ZoomOut)
                                            .duration(500)
                                            .onEnd(new YoYo.AnimatorCallback() {

                                                @Override
                                                public void call(Animator animator) {

                                                    vg.removeView(child);
                                                }
                                            })
                                            .playOn(child);
                                }

                                return false;
                            }
                        });

                        pop.show();
                    }
                });

                vg.addView(child, 0);

                YoYo.with(Techniques.BounceIn)
                        .duration(500)
                        .playOn(child);

                ImageView novaImagem = child.findViewById(R.id.pessoaNew);
                ImageLoader.getInstance().displayImage(SaspServer.getImageAddress(DataHolder.getInstance().getAdicionarPessoaImgBusca(), "pessoas", true), novaImagem);
            } else if (resultCode == 2) {

                MainActivity.dialogHelper.showProgressDelayed(500, new Runnable() {
                    @Override
                    public void run() {

                        DataHolder.getInstance().setBuscarPessoaDataSimple("", "");

                        Intent i = new Intent(getActivity(), PessoasCadastrarPessoaActivity.class);
                        startActivityForResult(i, PessoasCadastrarPessoaActivity.CODE_ACTIVITY_CADASTRAR_PESSOA);
                    }
                });
            }
        } else if (requestCode == PessoasCadastrarPessoaActivity.CODE_ACTIVITY_CADASTRAR_PESSOA) {

            if (resultCode == 1) {

                final ViewGroup vg = (ViewGroup) getActivity().findViewById(R.id.layoutNewPessoa);
                final View child = LayoutInflater.from(getActivity()).inflate(R.layout.layout_nova_pessoa, null);

                child.findViewById(R.id.pessoaNew).setTag(DataHolder.getInstance().getAdicionarPessoaIdPessoa());
                child.findViewById(R.id.imageClick).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        PopupMenu pop = new PopupMenu(getActivity(), view);
                        pop.inflate(R.menu.menu_abordagens_pessoa);

                        pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {

                                if (menuItem.getOrder() == 1) {

                                    MainActivity.dialogHelper.showProgress();

                                    MainActivity.saspServer.pessoasPerfil((String) child.findViewById(R.id.pessoaNew).getTag(), new SaspResponse(getActivity()) {

                                        @Override
                                        void onSaspResponse(String error, String msg, JSONObject extra) {

                                            if (!isVisible()) return;

                                            if (error.equals("0")) {

                                                Intent i = new Intent(getActivity(), PessoasPerfilPessoaActivity.class);
                                                DataHolder.getInstance().setPessoaData(extra);
                                                startActivity(i);
                                            } else {

                                                MainActivity.dialogHelper.showError(msg);
                                            }
                                        }

                                        @Override
                                        void onResponse(String error) {

                                            if (!isVisible()) return;

                                            MainActivity.dialogHelper.showError(error);
                                        }

                                        @Override
                                        void onNoResponse(String error) {

                                            if (!isVisible()) return;

                                            MainActivity.dialogHelper.showError(error);
                                        }

                                        @Override
                                        void onPostResponse() {

                                            if (!isVisible()) return;

                                            MainActivity.dialogHelper.dismissProgress();
                                        }
                                    });
                                } else if (menuItem.getOrder() == 2) {

                                    YoYo.with(Techniques.ZoomOut)
                                            .duration(500)
                                            .onEnd(new YoYo.AnimatorCallback() {

                                                @Override
                                                public void call(Animator animator) {

                                                    vg.removeView(child);
                                                }
                                            })
                                            .playOn(child);
                                }

                                return false;
                            }
                        });

                        pop.show();
                    }
                });

                vg.addView(child, 0);

                ((ImageView) child.findViewById(R.id.pessoaNew)).setImageURI(DataHolder.getInstance().getAdicionarPessoaImgUri());

                YoYo.with(Techniques.BounceIn)
                        .duration(500)
                        .playOn(child);
            }
        } else if (requestCode == VeiculosCadastrarVeiculoActivity.CODE_ACTIVITY_CADASTRAR_VEICULO) {

            if (resultCode == 1) {

                final ViewGroup vg = (ViewGroup) getActivity().findViewById(R.id.layoutNewVeiculo);
                final View child = LayoutInflater.from(getActivity()).inflate(R.layout.layout_novo_veiculo, null);

                JSONObject json = DataHolder.getInstance().getAdicionarVeiculoInfo();

                try {

                    String id_veiculo = json.getString("id_veiculo");

                    child.setTag(id_veiculo);

                    if (json.getString("tipo_placa").equals("2")) {

                        child.findViewById(R.id.placaNormal).setVisibility(View.VISIBLE);
                        child.findViewById(R.id.placaMercosul).setVisibility(View.VISIBLE);
                    }

                    ((TextView) child.findViewById(R.id.placa)).setText(json.getString("placa"));

                    child.findViewById(R.id.imageClick).setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            PopupMenu pop = new PopupMenu(getActivity(), view);
                            pop.inflate(R.menu.menu_abordagens_veiculo);

                            pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem menuItem) {

                                    YoYo.with(Techniques.ZoomOut)
                                            .duration(500)
                                            .onEnd(new YoYo.AnimatorCallback() {

                                                @Override
                                                public void call(Animator animator) {

                                                    vg.removeView(child);
                                                }
                                            })
                                            .playOn(child);

                                    return false;
                                }
                            });

                            pop.show();
                        }
                    });

                    vg.addView(child, 0);

                    YoYo.with(Techniques.BounceIn)
                            .duration(500)
                            .playOn(child);
                } catch (Exception e) {

                    if (AppUtils.DEBUG_MODE)
                        Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        MainActivity.dialogHelper.dismissProgress();
    }

    private void escolherLocal() {

        MainActivity.dialogHelper.showProgress();

        Intent i = new Intent(getActivity(), LocationPickerActivity.class);
        i.putExtra("buscarAbordagem", true);
        startActivityForResult(i, LocationPickerActivity.LOCATION_PICKER_INTENT);
    }
}
