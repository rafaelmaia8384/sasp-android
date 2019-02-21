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


public class FragmentMeusCadastrosActivityMeusCadastros extends Fragment {

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

            view = inflater.inflate(R.layout.fragment_meuscadastrosactivity_meuscadastros, container, false);
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

        refreshLayout = getActivity().findViewById(R.id.refreshLayout);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new FragmentMeusCadastrosActivityMeusCadastros()).commitAllowingStateLoss();
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
        listaPessoaAdapter = new ListaPessoaAdapter(getActivity(), MeusCadastrosPessoasActivity.dialogHelper, MeusCadastrosPessoasActivity.saspServer, recyclerView, listaPessoas);

        listaPessoaAdapter.setOnLoadMoreListener(new ListaPessoaAdapter.OnLoadMoreListener() {

            @Override
            public void onLoadMore() {

                listaPessoas.add(null);
                listaPessoaAdapter.notifyItemInserted(listaPessoas.size() - 1);

                MeusCadastrosPessoasActivity.saspServer.pessoasMeusCadastros(index, new SaspResponse(getActivity()) {

                    @Override
                    void onSaspResponse(String error, String msg, JSONObject extra) {

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

                        if (listaPessoas.size() == 0) return;

                        listaPessoas.remove(position - 1);
                        listaPessoaAdapter.notifyItemRemoved(position);

                        index++;
                    }

                    @Override
                    void onResponse(String error) {

                        if (listaPessoas.size() == 0) return;

                        listaPessoas.remove(listaPessoas.size() - 1);
                        listaPessoaAdapter.notifyItemRemoved(listaPessoas.size());
                    }

                    @Override
                    void onNoResponse(String error) {

                        if (listaPessoas.size() == 0) return;

                        listaPessoas.remove(listaPessoas.size() - 1);
                        listaPessoaAdapter.notifyItemRemoved(listaPessoas.size());
                    }

                    @Override
                    void onPostResponse() {

                        listaPessoaAdapter.setLoaded();
                    }
                });
            }
        });

        recyclerView.setAdapter(listaPessoaAdapter);

        MeusCadastrosPessoasActivity.saspServer.pessoasMeusCadastros(index, new SaspResponse(getActivity()) {

            @Override
            void onSaspResponse(String error, String msg, JSONObject extra) {

                if (error.equals("1")) {

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

                    recyclerView.setAlpha(0.0f);
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.animate().alpha(1.0f);
                }
                catch (JSONException e) {

                    return;
                }

                index++;
            }

            @Override
            void onResponse(String error) {

                getActivity().findViewById(R.id.textError).setVisibility(View.VISIBLE);
                ((TextView)getActivity().findViewById(R.id.textError)).setText("Erro de conexão com o servidor.");
            }

            @Override
            void onNoResponse(String error) {

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