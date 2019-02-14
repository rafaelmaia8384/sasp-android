package br.gov.pb.pm.sasp;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.security.SecureRandom;

import id.zelory.compressor.Compressor;

public class DataHolder extends Application {

    private static final DataHolder holder = new DataHolder();

    private boolean isLoginActivityVisible = false;

    private JSONObject loginData;
    private JSONObject pessoaData;
    private JSONObject veiculoData;
    private JSONObject lugarData;
    private JSONObject informeData;

    private String userIP;
    private String userIMEI;
    private String userMAC;

    private String cadastroSuspeitoNomeAlcunha;
    private String cadastroSuspeitoNomeCompleto;
    private String cadastroSuspeitoNomeDaMae;
    private String cadastroSuspeitoRelato;
    private String cadastroSuspeitoCrtCorPele;
    private String cadastroSuspeitoCrtCorOlhos;
    private String cadastroSuspeitoCrtCorCabelos;
    private String cadastroSuspeitoCrtTipoCabelos;
    private String cadastroSuspeitoCrtPorteFisico;
    private String cadastroSuspeitoCrtEstatura;
    private String cadastroSuspeitoCrtDeficiente;
    private String cadastroSuspeitoCrtPossuiTatuagem;
    private String cadastroSuspeitoHistoricoCriminal;
    private String cadastroSuspeitoAreasDeAtuacao;
    private String cadastroSuspeitoCPF;
    private String cadastroSuspeitoRG;
    private String cadastroSuspeitoDataNascimento;

    public static DataHolder getInstance() {

        return holder;
    }

    public void setUserIP(String ip) {

        userIP = ip;
    }

    public String getUserIP() {

        return userIP;
    }

    public void setUserMAC(String mac) {

        userMAC = mac;
    }

    public String getUserMAC() {

        return userMAC;
    }

    public void setUserIMEI(String imei) {

        userIMEI = imei;
    }

    public String getUserIMEI() {

        return userIMEI;
    }

    public void setLoginData(JSONObject json) {

        loginData = json;
    }

    public JSONObject getLoginData() {

        return loginData;
    }

    public String getLoginDataItem(String item) {

        try {

            return loginData.getString(item);
        }
        catch (Exception e) {

            return "";
        }
    }

    public void setPessoaData(JSONObject json) {

        pessoaData = json;
    }

    public JSONObject getPessoaData() {

        return loginData;
    }

    public String getPessoaDataItem(String item) {

        try {

            return pessoaData.getString(item);
        }
        catch (Exception e) {

            return "";
        }
    }

    public void setCadastrarSuspeitoPasso1(String nomeAlcunha, String nomeCompleto, int crtCorPele, int crtCorOlhos, int crtCorCabelos, int crtTipoCabelos, int crtPorteFisico, int crtEstatura, int crtDeficiente, int crtTatuagem, String relato) {

        cadastroSuspeitoNomeAlcunha = nomeAlcunha;
        cadastroSuspeitoNomeCompleto = nomeCompleto;
        cadastroSuspeitoCrtCorPele = Integer.toString(crtCorPele);
        cadastroSuspeitoCrtCorOlhos = Integer.toString(crtCorOlhos);
        cadastroSuspeitoCrtCorCabelos = Integer.toString(crtCorCabelos);
        cadastroSuspeitoCrtTipoCabelos = Integer.toString(crtTipoCabelos);
        cadastroSuspeitoCrtPorteFisico = Integer.toString(crtPorteFisico);
        cadastroSuspeitoCrtEstatura = Integer.toString(crtEstatura);
        cadastroSuspeitoCrtDeficiente = Integer.toString(crtDeficiente);
        cadastroSuspeitoCrtPossuiTatuagem = Integer.toString(crtTatuagem);
        cadastroSuspeitoRelato = relato;
    }

    public void setCadastrarSuspeitoPasso2(int historico, int areas_de_atuacao) {

        cadastroSuspeitoHistoricoCriminal = Integer.toString(historico);
        cadastroSuspeitoAreasDeAtuacao = Integer.toString(areas_de_atuacao);
    }

    public void setCadastrarSuspeitoPasso3(String nome_da_mae, String cpf, String rg, String data_nascimento) {

        cadastroSuspeitoNomeDaMae = nome_da_mae;
        cadastroSuspeitoCPF = cpf;
        cadastroSuspeitoRG = rg;
        cadastroSuspeitoDataNascimento = data_nascimento;
    }

    public String[] getInfoCadastroSuspeito() {

        String[] result = {cadastroSuspeitoNomeAlcunha, cadastroSuspeitoNomeCompleto, cadastroSuspeitoCrtCorPele, cadastroSuspeitoCrtCorOlhos, cadastroSuspeitoCrtCorCabelos, cadastroSuspeitoCrtTipoCabelos, cadastroSuspeitoCrtPorteFisico, cadastroSuspeitoCrtEstatura, cadastroSuspeitoCrtDeficiente, cadastroSuspeitoCrtPossuiTatuagem, cadastroSuspeitoRelato, cadastroSuspeitoHistoricoCriminal, cadastroSuspeitoAreasDeAtuacao, cadastroSuspeitoNomeDaMae, cadastroSuspeitoCPF, cadastroSuspeitoRG, cadastroSuspeitoDataNascimento};

        return result;
    }

    public void setPerfilSuspeitoData(JSONObject data) {

        pessoaData = data;
    }

    public void setLoginActivityVisible() {

        isLoginActivityVisible = true;
    }

    public void setLoginActivityInvisible() {

        isLoginActivityVisible = false;
    }

    public boolean isLoginActivityVisible() {

        return isLoginActivityVisible;
    }
}
