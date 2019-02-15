package br.gov.pb.pm.sasp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class FragmentMainActivityPessoas extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<ListaPessoa> listaPessoas;
    private ListaPessoaAdapter listaPessoaAdapter;
    private SwipeRefreshLayout refreshLayout;

    private String date_time = "9999-01-01 00:00:00";

    private int index = 1;

    private boolean loaded_data = false;
    private View view = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (view == null) {

            view = inflater.inflate(R.layout.fragment_mainactivity_pessoas, container, false);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        getActivity().findViewById(R.id.buttonBuscarPessoa).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                MainActivity.dialogHelper.showProgressDelayed(500, new Runnable() {

                    @Override
                    public void run() {

                        Intent i = new Intent(getActivity(), BuscarPessoaActivity.class);
                        startActivity(i);
                    }
                });
            }
        });

        getActivity().findViewById(R.id.buttonCadastrarPessoa).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                MainActivity.dialogHelper.showProgressDelayed(500, new Runnable() {

                    @Override
                    public void run() {

                        Intent i = new Intent(getActivity(), CadastrarPessoaActivity.class);
                        startActivity(i);
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

                        Intent i = new Intent(getActivity(), MeusCadastrosPessoasActivity.class);
                        startActivity(i);
                    }
                });
            }
        });

        refreshLayout = getActivity().findViewById(R.id.refreshLayout);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                index = 1;

                recyclerView.setVisibility(View.GONE);
                getActivity().findViewById(R.id.textViewPessoas).setVisibility(View.GONE);
                getActivity().findViewById(R.id.progressPessoas).setVisibility(View.VISIBLE);

                refreshLayout.setRefreshing(false);

                processData();
            }
        });

        if (!loaded_data) {

            processData();

            loaded_data = true;
        }
    }

    private void processData() {

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());

        recyclerView = getActivity().findViewById(R.id.recyclerViewPessoas);
        recyclerView.setLayoutManager(llm);

        listaPessoas = new ArrayList<>();
        listaPessoaAdapter = new ListaPessoaAdapter(getActivity(), MainActivity.dialogHelper, MainActivity.saspServer, recyclerView, listaPessoas);

        listaPessoaAdapter.setOnLoadMoreListener(new ListaPessoaAdapter.OnLoadMoreListener() {

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

                                listaPessoas.add(new ListaPessoa(json.getString("img_principal").toString(), json.getString("img_busca").toString(), json.getString("id_pessoa").toString(), json.getString("nome_alcunha").toString(), json.getString("areas_de_atuacao").toString(), json.getString("data_registro").toString()));
                            }

                            listaPessoaAdapter.notifyDataSetChanged();
                        }
                        catch (JSONException e) {

                            return;
                        }

                        listaPessoas.remove(position - 1);
                        listaPessoaAdapter.notifyItemRemoved(position);

                        index++;
                    }

                    @Override
                    void onResponse(String error) {

                        if (!isVisible()) return;

                        listaPessoas.remove(listaPessoas.size() - 1);
                        listaPessoaAdapter.notifyItemRemoved(listaPessoas.size());
                    }

                    @Override
                    void onNoResponse(String error) {

                        if (!isVisible()) return;

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

                            getActivity().findViewById(R.id.textViewPessoas).setVisibility(View.VISIBLE);
                            ((TextView)getActivity().findViewById(R.id.textViewPessoas)).setText(msg);

                            return;
                        }

                        try {

                            JSONArray jsonArray = extra.getJSONArray("Resultado");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject json = jsonArray.getJSONObject(i);

                                listaPessoas.add(new ListaPessoa(json.getString("img_principal").toString(), json.getString("img_busca").toString(), json.getString("id_pessoa").toString(), json.getString("nome_alcunha").toString(), json.getString("areas_de_atuacao").toString(), json.getString("data_registro").toString()));
                            }

                            listaPessoaAdapter.notifyDataSetChanged();
                            listaPessoaAdapter.setLoaded();

                            getActivity().findViewById(R.id.progressPessoas).setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                        catch (JSONException e) {

                            return;
                        }

                        index++;
                    }

                    @Override
                    void onResponse(String error) {

                        if (!isVisible()) return;

                        getActivity().findViewById(R.id.textViewPessoas).setVisibility(View.VISIBLE);
                        ((TextView)getActivity().findViewById(R.id.textViewPessoas)).setText("Erro de conexão com o servidor.");
                    }

                    @Override
                    void onNoResponse(String error) {

                        if (!isVisible()) return;

                        getActivity().findViewById(R.id.textViewPessoas).setVisibility(View.VISIBLE);
                        ((TextView)getActivity().findViewById(R.id.textViewPessoas)).setText("Erro de conexão com o servidor.");
                    }

                    @Override
                    void onPostResponse() {

                        if (!isVisible()) return;

                        getActivity().findViewById(R.id.progressPessoas).setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            void onResponse(String error) {

                if (!isVisible()) return;

                getActivity().findViewById(R.id.textViewPessoas).setVisibility(View.VISIBLE);
                ((TextView)getActivity().findViewById(R.id.textViewPessoas)).setText("Erro de conexão com o servidor.");
                getActivity().findViewById(R.id.progressPessoas).setVisibility(View.GONE);
            }

            @Override
            void onNoResponse(String error) {

                if (!isVisible()) return;

                getActivity().findViewById(R.id.textViewPessoas).setVisibility(View.VISIBLE);
                ((TextView)getActivity().findViewById(R.id.textViewPessoas)).setText("Erro de conexão com o servidor.");
                getActivity().findViewById(R.id.progressPessoas).setVisibility(View.GONE);
            }

            @Override
            void onPostResponse() {

            }
        });
    }
}
