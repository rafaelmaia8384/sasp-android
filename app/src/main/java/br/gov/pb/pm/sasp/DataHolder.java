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

    private String buscaPessoaNomeAlcunha;
    private String buscaPessoaCrtCorPele;
    private String buscaPessoaCrtCorOlhos;
    private String buscaPessoaCrtCorCabelos;
    private String buscaPessoaCrtTipoCabelos;
    private String buscaPessoaCrtPorteFisico;
    private String buscaPessoaCrtEstatura;
    private String buscaPessoaCrtDeficiente;
    private String buscaPessoaCrtPossuiTatuagem;
    private String buscaPessoaHistoricoCriminal;
    private String buscaPessoaAreasDeAtuacao;

    private String cadastroPessoaNomeAlcunha;
    private String cadastroPessoaNomeCompleto;
    private String cadastroPessoaNomeDaMae;
    private String cadastroPessoaRelato;
    private String cadastroPessoaCrtCorPele;
    private String cadastroPessoaCrtCorOlhos;
    private String cadastroPessoaCrtCorCabelos;
    private String cadastroPessoaCrtTipoCabelos;
    private String cadastroPessoaCrtPorteFisico;
    private String cadastroPessoaCrtEstatura;
    private String cadastroPessoaCrtDeficiente;
    private String cadastroPessoaCrtPossuiTatuagem;
    private String cadastroPessoaHistoricoCriminal;
    private String cadastroPessoaAreasDeAtuacao;
    private String cadastroPessoaCPF;
    private String cadastroPessoaRG;
    private String cadastroPessoaDataNascimento;

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

    public void setBuscarPessoaData(String nome_alcunha, int area_de_atuacao, int historico, int crtCorPele, int crtCorOlhos, int crtCorCabelos, int crtTipoCabelos, int crtPorteFisico, int crtEstatura, int crtDeficiente, int crtTatuagem) {

        buscaPessoaNomeAlcunha = nome_alcunha;
        buscaPessoaAreasDeAtuacao = Integer.toString(area_de_atuacao);
        buscaPessoaHistoricoCriminal = Integer.toString(historico);
        buscaPessoaCrtCorPele = Integer.toString(crtCorPele);
        buscaPessoaCrtCorOlhos = Integer.toString(crtCorOlhos);
        buscaPessoaCrtCorCabelos = Integer.toString(crtCorCabelos);
        buscaPessoaCrtTipoCabelos = Integer.toString(crtTipoCabelos);
        buscaPessoaCrtPorteFisico = Integer.toString(crtPorteFisico);
        buscaPessoaCrtEstatura = Integer.toString(crtEstatura);
        buscaPessoaCrtDeficiente = Integer.toString(crtDeficiente);
        buscaPessoaCrtPossuiTatuagem = Integer.toString(crtTatuagem);
    }

    public String[] getBuscarPessoaData() {

        String[] result = {buscaPessoaNomeAlcunha, buscaPessoaAreasDeAtuacao, buscaPessoaHistoricoCriminal, buscaPessoaCrtCorPele, buscaPessoaCrtCorOlhos, buscaPessoaCrtCorCabelos, buscaPessoaCrtTipoCabelos, buscaPessoaCrtPorteFisico, buscaPessoaCrtEstatura, buscaPessoaCrtDeficiente, buscaPessoaCrtPossuiTatuagem};

        return result;
    }

    public void setCadastrarPessoaData(String nomeAlcunha, String nomeCompleto, int crtCorPele, int crtCorOlhos, int crtCorCabelos, int crtTipoCabelos, int crtPorteFisico, int crtEstatura, int crtDeficiente, int crtTatuagem, String relato, int historico, int areas_de_atuacao, String nome_da_mae, String cpf, String rg, String data_nascimento) {

        cadastroPessoaNomeAlcunha = nomeAlcunha;
        cadastroPessoaNomeCompleto = nomeCompleto;
        cadastroPessoaCrtCorPele = Integer.toString(crtCorPele);
        cadastroPessoaCrtCorOlhos = Integer.toString(crtCorOlhos);
        cadastroPessoaCrtCorCabelos = Integer.toString(crtCorCabelos);
        cadastroPessoaCrtTipoCabelos = Integer.toString(crtTipoCabelos);
        cadastroPessoaCrtPorteFisico = Integer.toString(crtPorteFisico);
        cadastroPessoaCrtEstatura = Integer.toString(crtEstatura);
        cadastroPessoaCrtDeficiente = Integer.toString(crtDeficiente);
        cadastroPessoaCrtPossuiTatuagem = Integer.toString(crtTatuagem);
        cadastroPessoaRelato = relato;

        cadastroPessoaHistoricoCriminal = Integer.toString(historico);
        cadastroPessoaAreasDeAtuacao = Integer.toString(areas_de_atuacao);

        cadastroPessoaNomeDaMae = nome_da_mae;
        cadastroPessoaCPF = cpf;
        cadastroPessoaRG = rg;
        cadastroPessoaDataNascimento = data_nascimento;
    }

    public String[] getCadastrarPessoaData() {

        String[] result = {cadastroPessoaNomeAlcunha, cadastroPessoaNomeCompleto, cadastroPessoaCrtCorPele, cadastroPessoaCrtCorOlhos, cadastroPessoaCrtCorCabelos, cadastroPessoaCrtTipoCabelos, cadastroPessoaCrtPorteFisico, cadastroPessoaCrtEstatura, cadastroPessoaCrtDeficiente, cadastroPessoaCrtPossuiTatuagem, cadastroPessoaRelato, cadastroPessoaHistoricoCriminal, cadastroPessoaAreasDeAtuacao, cadastroPessoaNomeDaMae, cadastroPessoaCPF, cadastroPessoaRG, cadastroPessoaDataNascimento};

        return result;
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
