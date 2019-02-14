package br.gov.pb.pm.sasp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MeusCadastrosPessoasActivity extends SaspActivity {

    private RecyclerView recyclerView;
    private ArrayList<ListaPessoa> listaPessoas;
    private ListaPessoaAdapter listaPessoaAdapter;

    private DialogHelper dialogHelper;
    private SaspServer saspServer;

    private int index = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_cadastros_pessoas);

        dialogHelper = new DialogHelper(this);
        saspServer = new SaspServer(this);

        processData();
    }

    private void processData() {

        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());

        recyclerView = findViewById(R.id.recyclerViewPessoas);
        recyclerView.setLayoutManager(llm);

        listaPessoas = new ArrayList<>();
        listaPessoaAdapter = new ListaPessoaAdapter(this, dialogHelper, saspServer, recyclerView, listaPessoas);

        listaPessoaAdapter.setOnLoadMoreListener(new ListaPessoaAdapter.OnLoadMoreListener() {

            @Override
            public void onLoadMore() {

                listaPessoas.add(null);
                listaPessoaAdapter.notifyItemInserted(listaPessoas.size() - 1);

                saspServer.pessoasMeusCadastros(index, new SaspResponse(MeusCadastrosPessoasActivity.this) {

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

                        listaPessoas.remove(position - 1);
                        listaPessoaAdapter.notifyItemRemoved(position);

                        index++;
                    }

                    @Override
                    void onResponse(String error) {

                        listaPessoas.remove(listaPessoas.size() - 1);
                        listaPessoaAdapter.notifyItemRemoved(listaPessoas.size());
                    }

                    @Override
                    void onNoResponse(String error) {

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

        saspServer.pessoasMeusCadastros(index, new SaspResponse(this) {

            @Override
            void onSaspResponse(String error, String msg, JSONObject extra) {

                if (error.equals("1")) {

                    ((TextView)findViewById(R.id.textViewPessoas)).setText(msg);

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

                    findViewById(R.id.layoutCarregandoPessoas).setVisibility(View.GONE);

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

                ((TextView)findViewById(R.id.textViewPessoas)).setText("Erro de conexão com o servidor.");
            }

            @Override
            void onNoResponse(String error) {

                ((TextView)findViewById(R.id.textViewPessoas)).setText("Erro de conexão com o servidor.");
            }

            @Override
            void onPostResponse() {

                findViewById(R.id.progressPessoas).setVisibility(View.GONE);
            }
        });
    }

    @Override
    void onPermissionsChange(boolean confirmed) {

    }

    public void fecharJanela(View view) {

        finish();
    }
}
