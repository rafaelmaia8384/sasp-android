package br.gov.pb.pm.sasp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONObject;

import java.util.ArrayList;

public class ListaPessoaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private DialogHelper dialogHelper;
    private SaspServer saspServer;
    private ArrayList<ListaPessoa> listaPessoas;

    private RecyclerView rv;

    private final int VIEW_ITEM = 0;
    private final int VIEW_LOADING = 1;

    private boolean isLoading;
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;

    private OnLoadMoreListener onLoadMoreListener;

    public ListaPessoaAdapter(Context context, DialogHelper dialogHelper, SaspServer saspServer, RecyclerView rv, ArrayList<ListaPessoa> listaPessoas) {

        this.context = context;
        this.dialogHelper = dialogHelper;
        this.saspServer = saspServer;
        this.rv = rv;
        this.listaPessoas = listaPessoas;

        final LinearLayoutManager llm = (LinearLayoutManager) rv.getLayoutManager();

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = llm.getItemCount();
                lastVisibleItem = llm.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {

                    if (onLoadMoreListener != null) {

                        onLoadMoreListener.onLoadMore();
                    }

                    isLoading = true;
                }
            }
        });
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {

        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    public void setLoaded() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isLoading = false;
            }
        }, 300);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        if (viewType == VIEW_ITEM) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_lista_pessoa, parent, false);

            final RecyclerView rv = parent.findViewById(R.id.recyclerView);

            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    int pos = rv.getChildAdapterPosition(view);

                    dialogHelper.showProgress();

                    saspServer.pessoasPerfil(listaPessoas.get(pos).id_pessoa, new SaspResponse(context) {

                        @Override
                        void onSaspResponse(String error, String msg, JSONObject extra) {

                            DataHolder.getInstance().setPessoaData(extra);

                            Intent i = new Intent(context, PerfilPessoaActivity.class);

                            ((SaspActivity)context).startActivityForResult(i, 400);
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
                }
            });

            view.findViewById(R.id.imagemPerfil).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    int pos = rv.getChildAdapterPosition((View)view.getParent().getParent());

                    String img_principal = listaPessoas.get(pos).img_perfil_principal;

                    if (!img_principal.equals("null")) {

                        Intent i = new Intent(context, ImageViewActivity.class);
                        i.putExtra("img_principal", img_principal);
                        context.startActivity(i);
                    }
                }
            });

            return new mViewHolder(view);
        }
        else { //VIEW_LOADING

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading, parent, false);

            return new mLoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof mViewHolder) {

            ListaPessoa listaPessoa = listaPessoas.get(position);

            String img_busca = listaPessoa.img_perfil_busca;

            if (!img_busca.equals("null")) {

                ImageLoader.getInstance().loadImage(SaspServer.getImageAddress(img_busca, "pessoas", true), new SimpleImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                        ((mViewHolder)holder).imagemPerfil.setImageResource(R.drawable.img_perfil);

                        super.onLoadingStarted(imageUri, view);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                        ((mViewHolder)holder).imagemPerfil.setImageBitmap(loadedImage);

                        super.onLoadingComplete(imageUri, view, loadedImage);
                    }
                });
            }
            else{

                ((mViewHolder)holder).imagemPerfil.setImageResource(R.drawable.img_perfil);
            }

            ((mViewHolder)holder).nomeAlcunha.setText(listaPessoa.nome_alcunha);
            ((mViewHolder)holder).areasAtuacao.setText(listaPessoa.areas_atuacao);
            ((mViewHolder)holder).dataCadastro.setText(AppUtils.formatarData(listaPessoa.data_cadastro));
        }
    }

    @Override
    public int getItemCount() {

        return listaPessoas.size();
    }

    @Override
    public int getItemViewType(int position) {

        return listaPessoas.get(position) == null ? VIEW_LOADING : VIEW_ITEM;
    }

    public class mViewHolder extends RecyclerView.ViewHolder {

        ImageView imagemPerfil;
        TextView nomeAlcunha;
        TextView areasAtuacao;
        TextView dataCadastro;

        public mViewHolder(View view) {

            super(view);

            imagemPerfil = view.findViewById(R.id.imagemPerfil);
            nomeAlcunha = view.findViewById(R.id.nomeAlcunha);
            areasAtuacao = view.findViewById(R.id.areasAtuacao);
            dataCadastro = view.findViewById(R.id.dataCadastro);
        }
    }

    public class mLoadingViewHolder extends RecyclerView.ViewHolder {

        public mLoadingViewHolder(View view) {

            super(view);
        }
    }

    public interface OnLoadMoreListener {

        void onLoadMore();
    }
}
