package br.gov.pb.pm.sasp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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


public class FragmentMainActivityResultadoBuscaPessoa extends Fragment {

    public static final String id = "FRAGMENT_PESSOAS_RESULTADO_BUSCA";

    private RecyclerView recyclerView;
    private ArrayList<ListaPessoa> listaPessoas;
    private ListaPessoaAdapter listaPessoaAdapter;
    private SwipeRefreshLayout refreshLayout;

    private int index = 1;

    private boolean loaded_data = false;
    private View view = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (view == null) {

            view = inflater.inflate(R.layout.fragment_mainactivity_resultado_busca_pessoas, container, false);
        }

        return view;
    }

    @Override
    public void onDestroyView() {

        if (view != null) {

            ((ViewGroup)view.getParent()).removeAllViews();
        }

        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        getActivity().findViewById(R.id.buttonVoltar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                getActivity().onBackPressed();
            }
        });

        refreshLayout = getActivity().findViewById(R.id.refreshLayout);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                index = 1;

                recyclerView.setVisibility(View.GONE);
                getActivity().findViewById(R.id.textError).setVisibility(View.GONE);
                getActivity().findViewById(R.id.progress).setVisibility(View.VISIBLE);

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

        recyclerView = getActivity().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(llm);

        listaPessoas = new ArrayList<>();
        listaPessoaAdapter = new ListaPessoaAdapter(getActivity(), MainActivity.dialogHelper, MainActivity.saspServer, recyclerView, listaPessoas);

        listaPessoaAdapter.setOnLoadMoreListener(new ListaPessoaAdapter.OnLoadMoreListener() {

            @Override
            public void onLoadMore() {

                listaPessoas.add(null);
                listaPessoaAdapter.notifyItemInserted(listaPessoas.size() - 1);

                MainActivity.saspServer.pessoasBuscarPessoa(index, new SaspResponse(getActivity()) {

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

        MainActivity.saspServer.pessoasBuscarPessoa(index, new SaspResponse(getActivity()) {

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

                        listaPessoas.add(new ListaPessoa(json.getString("img_principal").toString(), json.getString("img_busca").toString(), json.getString("id_pessoa").toString(), json.getString("nome_alcunha").toString(), json.getString("areas_de_atuacao").toString(), json.getString("data_registro").toString()));
                    }

                    listaPessoaAdapter.notifyDataSetChanged();
                    listaPessoaAdapter.setLoaded();

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

                getActivity().findViewById(R.id.progress).setVisibility(View.GONE);
            }
        });
    }
}
