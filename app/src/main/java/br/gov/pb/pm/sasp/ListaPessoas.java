package br.gov.pb.pm.sasp;

public class ListaPessoas {

    public String img_perfil_principal;
    public String img_perfil_busca;
    public String id_pessoa;
    public String nome_alcunha;
    public String areas_atuacao;
    public String data_cadastro;

    public ListaPessoas(String img_perfil_principal, String img_perfil_busca, String id_pessoa, String nome_alcunha, String areas_atuacao, String data_cadastro) {

        this.img_perfil_principal = img_perfil_principal;
        this.img_perfil_busca = img_perfil_busca;
        this.id_pessoa = id_pessoa;
        this.nome_alcunha = nome_alcunha;
        this.areas_atuacao = areas_atuacao;
        this.data_cadastro = data_cadastro;
    }
}
