package br.gov.pb.pm.sasp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.security.SecureRandom;

import id.zelory.compressor.Compressor;

public class SaspImage {

    private Context context;
    private Compressor compressor;
    private File imgPrincipal;
    private File imgBusca;

    public SaspImage(Context ctx) {

        context = ctx;

        imgPrincipal = null;
        imgBusca = null;

        compressor = new Compressor(ctx)
                .setDestinationDirectoryPath(ctx.getExternalCacheDir().getPath())
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .setQuality(90);
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
            bitmapBusca.compress(Bitmap.CompressFormat.JPEG, 95, osBusca);

            osPrincipal.close();
            osBusca.close();

            bmp.recycle();
            bitmapBusca.recycle();
        }
        catch (final Exception e) {

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
}
