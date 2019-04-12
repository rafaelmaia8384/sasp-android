package br.gov.pb.pm.sasp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PessoasFragmentMainActivityPessoas extends Fragment {

    public static final String id = "FRAGMENT_PESSOAS";

    private RecyclerView recyclerView;
    private ArrayList<ListaPessoas> listaPessoas;
    private ListaPessoasAdapter listaPessoaAdapter;
    private SwipeRefreshLayout refreshLayout;

    private String date_time = "9999-01-01 00:00:00";

    private PopupMenu pmOpcao;

    private int index = 1;

    private boolean loaded_data = false;
    private View view = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (view == null) {

            view = inflater.inflate(R.layout.pessoas_fragment_mainactivity_pessoas, container, false);
        }

        return view;
    }

    @Override
    public void onDestroyView() {

        if (view != null) {

            try {

                ((ViewGroup) view.getParent()).removeAllViews();
            }
            catch (Exception e) { }
        }

        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        pmOpcao = new PopupMenu(getActivity(), getActivity().findViewById(R.id.buttonBuscar));
        pmOpcao.inflate(R.menu.menu_buscar_pessoas);
        pmOpcao.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                if (menuItem.getOrder() == 1) {

                    MainActivity.dialogHelper.showProgressDelayed(500, new Runnable() {

                        @Override
                        public void run() {

                            Intent i = new Intent(getActivity(), PessoasBuscarPessoaActivity.class);
                            getActivity().startActivityForResult(i, PessoasBuscarPessoaActivity.CODE_ACTIVITY_BUSCAR_PESSOA);
                        }
                    });
                }
                else {

                    MainActivity.dialogHelper.showProgressDelayed(500, new Runnable() {

                        @Override
                        public void run() {

                            Intent i = new Intent(getActivity(), PessoasBuscarPessoaMarcaActivity.class);
                            getActivity().startActivityForResult(i, PessoasBuscarPessoaMarcaActivity.CODE_ACTIVITY_BUSCAR_PESSOA_MARCA);
                        }
                    });
                }

                return false;
            }
        });

        getActivity().findViewById(R.id.buttonBuscar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                pmOpcao.show();
            }
        });

        getActivity().findViewById(R.id.buttonCadastrar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                MainActivity.dialogHelper.showProgressDelayed(500, new Runnable() {

                    @Override
                    public void run() {

                        Intent i = new Intent(getActivity(), PessoasCadastrarPessoaActivity.class);
                        getActivity().startActivityForResult(i, PessoasCadastrarPessoaActivity.CODE_ACTIVITY_CADASTRAR_PESSOA);
                    }
                });
            }
        });

        getActivity().findViewById(R.id.buttonMeusCadastros).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                MainActivity.dialogHelper.showProgressDelayed(500, new Runnable() {

                    @Override
                    public void run() {

                        Intent i = new Intent(getActivity(), PessoasMeusCadastrosActivity.class);
                        startActivity(i);
                    }
                });
            }
        });

        refreshLayout = getActivity().findViewById(R.id.refreshLayout);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new PessoasFragmentMainActivityPessoas()).commitAllowingStateLoss();
            }
        });

        if (!loaded_data) {

            processData();

            loaded_data = true;
        }
    }

    private void processData() {

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());

        recyclerView = getActivity().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(llm);

        listaPessoas = new ArrayList<>();
        listaPessoaAdapter = new ListaPessoasAdapter(getActivity(), MainActivity.dialogHelper, MainActivity.saspServer, recyclerView, listaPessoas);

        listaPessoaAdapter.setOnLoadMoreListener(new ListaPessoasAdapter.OnLoadMoreListener() {

            @Override
            public void onLoadMore() {

                listaPessoas.add(null);
                listaPessoaAdapter.notifyItemInserted(listaPessoas.size() - 1);

                MainActivity.saspServer.pessoasUltimosCadastros(index, date_time, new SaspResponse(getActivity()) {

                    @Override
                    void onSaspResponse(String error, String msg, JSONObject extra) {

                        if (!isVisible()) return;

                        int position = listaPessoas.size();

                        try {

                            JSONArray jsonArray = extra.getJSONArray("Resultado");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject json = jsonArray.getJSONObject(i);

                                listaPessoas.add(new ListaPessoas(json.getString("img_principal").toString(), json.getString("img_busca").toString(), json.getString("id_pessoa").toString(), json.getString("nome_alcunha").toString(), json.getString("areas_de_atuacao").toString(), json.getString("data_registro").toString()));
                            }

                            listaPessoaAdapter.notifyDataSetChanged();
                        }
                        catch (JSONException e) {

                            if (AppUtils.DEBUG_MODE) Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();

                            return;
                        }

                        if (listaPessoas.size() == 0) return;

                        listaPessoas.remove(position - 1);
                        listaPessoaAdapter.notifyItemRemoved(position);

                        index++;
                    }

                    @Override
                    void onResponse(String error) {

                        if (!isVisible()) return;

                        if (listaPessoas.size() == 0) return;

                        listaPessoas.remove(listaPessoas.size() - 1);
                        listaPessoaAdapter.notifyItemRemoved(listaPessoas.size());
                    }

                    @Override
                    void onNoResponse(String error) {

                        if (!isVisible()) return;

                        if (listaPessoas.size() == 0) return;

                        listaPessoas.remove(listaPessoas.size() - 1);
                        listaPessoaAdapter.notifyItemRemoved(listaPessoas.size());
                    }

                    @Override
                    void onPostResponse() {

                        if (!isVisible()) return;

                        listaPessoaAdapter.setLoaded();
                    }
                });
            }
        });

        recyclerView.setAdapter(listaPessoaAdapter);

        MainActivity.saspServer.saspServerDateTime(new SaspResponse(getActivity()) {

            @Override
            void onSaspResponse(String error, String msg, JSONObject extra) {

                if (!isVisible()) return;

                try {

                    date_time = extra.getString("date_time");
                }
                catch (Exception e) { }

                MainActivity.saspServer.pessoasUltimosCadastros(index, date_time, new SaspResponse(getActivity()) {

                    @Override
                    void onSaspResponse(String error, String msg, JSONObject extra) {

                        if (!isVisible()) return;

                        if (error.equals("1")) {

                            getActivity().findViewById(R.id.textError).setVisibility(View.VISIBLE);
                            ((TextView)getActivity().findViewById(R.id.textError)).setText(msg);

                            return;
                        }

                        try {

                            JSONArray jsonArray = extra.getJSONArray("Resultado");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject json = jsonArray.getJSONObject(i);

                                listaPessoas.add(new ListaPessoas(json.getString("img_principal"), json.getString("img_busca"), json.getString("id_pessoa"), json.getString("nome_alcunha"), json.getString("areas_de_atuacao"), json.getString("data_registro")));
                            }

                            listaPessoaAdapter.notifyDataSetChanged();
                            listaPessoaAdapter.setLoaded();

                            getActivity().findViewById(R.id.progress).setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                        catch (JSONException e) {

                            if (AppUtils.DEBUG_MODE) Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();

                            return;
                        }

                        index++;
                    }

                    @Override
                    void onResponse(String error) {

                        if (!isVisible()) return;

                        getActivity().findViewById(R.id.textError).setVisibility(View.VISIBLE);
                        ((TextView)getActivity().findViewById(R.id.textError)).setText("Erro de conexão com o servidor.");
                    }

                    @Override
                    void onNoResponse(String error) {

                        if (!isVisible()) return;

                        getActivity().findViewById(R.id.textError).setVisibility(View.VISIBLE);
                        ((TextView)getActivity().findViewById(R.id.textError)).setText("Erro de conexão com o servidor.");
                    }

                    @Override
                    void onPostResponse() {

                        if (!isVisible()) return;

                        getActivity().findViewById(R.id.progress).setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            void onResponse(String error) {

                if (!isVisible()) return;

                getActivity().findViewById(R.id.textError).setVisibility(View.VISIBLE);
                ((TextView)getActivity().findViewById(R.id.textError)).setText("Erro de conexão com o servidor.");
                getActivity().findViewById(R.id.progress).setVisibility(View.GONE);
            }

            @Override
            void onNoResponse(String error) {

                if (!isVisible()) return;

                getActivity().findViewById(R.id.textError).setVisibility(View.VISIBLE);
                ((TextView)getActivity().findViewById(R.id.textError)).setText("Erro de conexão com o servidor.");
                getActivity().findViewById(R.id.progress).setVisibility(View.GONE);
            }

            @Override
            void onPostResponse() {

            }
        });
    }
}
