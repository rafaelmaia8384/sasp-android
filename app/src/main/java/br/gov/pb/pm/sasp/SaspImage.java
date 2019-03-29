package br.gov.pb.pm.sasp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import com.snatik.storage.Storage;

import java.io.File;
import java.io.FileOutputStream;
import java.security.SecureRandom;

import id.zelory.compressor.Compressor;

public class SaspImage {

    public static final String UPLOAD_OBJECT_FOLDER = "sasp-server-upload";
    public static final String UPLOAD_OBJECT_MODULO_PESSOAS = "pessoas";
    public static final String UPLOAD_OBJECT_MODULO_ABORDAGENS = "abordagens";
    public static final String UPLOAD_OBJECT_MODULO_ALERTAS = "alertas";
    public static final String UPLOAD_OBJECT_MODULO_INFORMES = "informes";
    public static final String UPLOAD_OBJECT_MODULO_VEICULOS = "veiculos";

    private Context context;
    private Compressor compressor;
    private File imgPrincipal;
    private File imgBusca;
    private Storage storage;

    public SaspImage(Context ctx) {

        context = ctx;

        imgPrincipal = null;
        imgBusca = null;

        compressor = new Compressor(ctx)
                .setDestinationDirectoryPath(ctx.getExternalCacheDir().getPath())
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .setQuality(90);

        storage = new Storage(ctx);
    }

    public boolean salvarImagem(Uri imageUri) {

        try {

            Bitmap bmp = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);

            String buscalName = AppUtils.randomFileName(".jpg");
            String principalName = AppUtils.randomFileName(".jpg");

            imgBusca = new File(context.getExternalCacheDir(), buscalName);
            imgPrincipal = new File(context.getExternalCacheDir(), principalName);

            FileOutputStream osPrincipal = new FileOutputStream(imgPrincipal);
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, osPrincipal);

            if (imgPrincipal.length() > 1024*256) {

                imgPrincipal = compressor.compressToFile(imgPrincipal, imgPrincipal.getName());
            }

            FileOutputStream osBusca = new FileOutputStream(imgBusca);

            int dimension = Math.min(bmp.getWidth(), bmp.getHeight());

            Bitmap bitmapBusca = ThumbnailUtils.extractThumbnail(bmp, dimension, dimension);
            bitmapBusca = Bitmap.createScaledBitmap(bitmapBusca, 128, 128, true);
            bitmapBusca.compress(Bitmap.CompressFormat.JPEG, 80, osBusca);

            osPrincipal.close();
            osBusca.close();

            bmp.recycle();
            bitmapBusca.recycle();
        }
        catch (final Exception e) {

            if (AppUtils.DEBUG_MODE) Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();

            return false;
        }

        return true;
    }

    public File getImgPrincipal() {

        return imgPrincipal;
    }

    public File getImgBusca() {

        return imgBusca;
    }

    public void delete() {

        if (imgBusca.exists()) {

            imgBusca.delete();
        }

        if (imgPrincipal.exists()) {

            imgPrincipal.delete();
        }
    }

    public void saveUploadObject(String modulo) {

        String uploadFolder = storage.getInternalFilesDirectory() + File.separator + UPLOAD_OBJECT_FOLDER;

        if (!storage.isDirectoryExists(uploadFolder)) {

            storage.createDirectory(uploadFolder);
        }

        storage.move(getImgBusca().getPath(), uploadFolder + File.separator + getImgBusca().getName());
        storage.move(getImgPrincipal().getPath(), uploadFolder + File.separator + getImgPrincipal().getName());

        String jsonContent = "{ \"enviando\": false, \"tentativas\": 0, \"modulo\": \"" + modulo + "\", \"img_busca\": \"" + getImgBusca().getName() + "\", \"img_principal\": \"" + getImgPrincipal().getName() + "\" }";

        storage.createFile(uploadFolder + File.separator + AppUtils.randomFileName(".json"), jsonContent);
    }
}
