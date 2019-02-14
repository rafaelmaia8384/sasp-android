package br.gov.pb.pm.sasp;

import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by rafael on 4/11/18.
 */

public class SaspImageDownloader implements ImageDownloader {

    @Override
    public InputStream getStream(String imageUri, Object extra) throws IOException {

        String address = imageUri.replace("https://", "http://");

        URL url = new URL(address);

        URLConnection connection = null;

        try {

            connection = url.openConnection();
            connection.setRequestProperty("User-Agent", "SASP Image Downloader - PMPB");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
        }
        catch (final Exception e) {

            return null;
        }
        finally {

            return new BufferedInputStream(connection.getInputStream());
        }
    }
}
