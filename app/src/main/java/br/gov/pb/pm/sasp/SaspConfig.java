package br.gov.pb.pm.sasp;

import android.content.Context;

import com.snatik.storage.Storage;

import java.io.File;

public class SaspConfig {

    private Storage storage;

    private static final String CONFIG_LOGIN_CPF = "CONFIG_LOGIN_CPF.data";
    private static final String CONFIG_LOGIN_SENHA = "CONFIG_LOGIN_SENHA.data";

    public SaspConfig(Context context) {

        storage = new Storage(context);
    }

    public void setConfigLoginCPF(String cpf) {

        storage.createFile(getConfigFile(CONFIG_LOGIN_CPF), cpf);
    }

    public String getConfigLoginCPF() {

        if (storage.isFileExist(getConfigFile(CONFIG_LOGIN_CPF))) {

            return storage.readTextFile(getConfigFile(CONFIG_LOGIN_CPF));
        }

        return null;
    }

    public void setConfigLoginSenha(String senha) {

        storage.createFile(getConfigFile(CONFIG_LOGIN_SENHA), senha);
    }

    public String getConfigLoginSenha() {

        if (storage.isFileExist(getConfigFile(CONFIG_LOGIN_SENHA))) {

            return storage.readTextFile(getConfigFile(CONFIG_LOGIN_SENHA));
        }

        return null;
    }

    public void removeConfigLoginSenha() {

        if (storage.isFileExist(getConfigFile(CONFIG_LOGIN_SENHA))) {

            storage.deleteFile(getConfigFile(CONFIG_LOGIN_SENHA));
        }
    }

    private String getConfigFile(String fileName) {

        return storage.getInternalFilesDirectory() + File.separator + fileName;
    }
}
