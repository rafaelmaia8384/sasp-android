package br.gov.pb.pm.sasp;

import android.app.Activity;
import android.graphics.Color;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Timer;
import java.util.TimerTask;

public class DialogHelper {

    private Activity activity;

    private MaterialDialog progressDialog;
    private MaterialDialog errorDialog;
    private MaterialDialog successDialog;
    private MaterialDialog blockDialog;

    private TextWatcher mascaraMatricula;

    public DialogHelper(Activity activity) {

        this.activity = activity;

        progressDialog = new MaterialDialog.Builder(activity)
                .content("Aguarde...")
                .progress(true, 0)
                .cancelable(false)
                .build();

        successDialog = new MaterialDialog.Builder(activity)
                .title("Sucesso")
                .positiveText("OK")
                .build();

        errorDialog = new MaterialDialog.Builder(activity)
                .title("Desculpe")
                .positiveText("OK")
                .build();

        blockDialog = new MaterialDialog.Builder(activity)
                .title("SASP")
                .canceledOnTouchOutside(false)
                .cancelable(false)
                .build();

        progressDialog.getContentView().setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        successDialog.getContentView().setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        errorDialog.getContentView().setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        blockDialog.getContentView().setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

        blockDialog.getActionButton(DialogAction.POSITIVE).setVisibility(View.GONE);
    }

    public void showBlock(String text) {

        blockDialog.setContent(text + "\n");
        blockDialog.show();
    }

    public void dismissBlock() {

        blockDialog.dismiss();
    }

    public void showProgress() {

        progressDialog.show();
    }

    public void dismissProgress() {

        progressDialog.dismiss();
    }

    public void showProgressDelayed(final int millisec, final Runnable runnable) {

        showProgress();

        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {

                dismissProgress();

                if (runnable == null) return;

                try {

                    runnable.run();
                }
                catch (Exception e) { }
            }
        }, millisec);
    }

    public void showSuccess(String text) {

        successDialog.setContent(text);
        successDialog.show();
    }

    public void showError(String text) {

        errorDialog.setContent(text);
        errorDialog.show();
    }

    public void showList(String title, String[] list, MaterialDialog.ListCallback listCallBack) {

        new MaterialDialog.Builder(activity)
                .title(title)
                .items(list)
                .itemsCallback(listCallBack)
                .show();
    }

    public void inputDialog(String title, String text, int inputType, MaterialDialog.InputCallback inputCallback) {

        MaterialDialog input = new MaterialDialog.Builder(activity)
                .title(title)
                .content(text)
                .inputType(inputType)
                .input("", "", inputCallback)
                .build();

        input.getContentView().setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        input.show();
    }

    public void matriculaDialog(MaterialDialog.InputCallback inputCallback) {

        MaterialDialog matriculaDialog = new MaterialDialog.Builder(activity)
                .title("Adicionar Matrícula")
                .content("Digite a matrícula:")
                .inputType(InputType.TYPE_NUMBER_VARIATION_NORMAL)
                .input("000.000-0", "", inputCallback)
                .positiveText("OK")
                .build();

        mascaraMatricula = MascaraCPF.insert("###.###-#", matriculaDialog.getInputEditText());
        matriculaDialog.getInputEditText().addTextChangedListener(mascaraMatricula);

        matriculaDialog.getContentView().setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        matriculaDialog.show();
    }

    public void confirmDialog(boolean cancelable, String title, String text, String negativeText, MaterialDialog.SingleButtonCallback inputCallback) {

        MaterialDialog confirm = new MaterialDialog.Builder(activity)
                .title(title)
                .content(text)
                .positiveText("Sim")
                .negativeText(negativeText)
                .onPositive(inputCallback)
                .cancelable(cancelable)
                .build();

        confirm.getContentView().setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        confirm.show();
    }
}
