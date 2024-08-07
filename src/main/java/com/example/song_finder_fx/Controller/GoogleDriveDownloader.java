package com.example.song_finder_fx.Controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GoogleDriveDownloader {

    private final String fileUrl;
    private final String localFilePath;

    public GoogleDriveDownloader(String url, String filePath) {
        this.fileUrl = url;
        this.localFilePath = filePath;
    }

    public void downloadFile() throws IOException {
        // 01
        /*try {
            URL url = new URL(fileUrl);
            try (InputStream inputStream = url.openStream();
                 ReadableByteChannel readableByteChannel = Channels.newChannel(inputStream);
                 FileOutputStream fileOutputStream = new FileOutputStream(localFilePath)) {

                fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                System.out.println("File downloaded successfully.");
            }
        } catch (IOException e) {
            System.err.println("An error occurred while downloading the file: " + e.getMessage());
        }*/

        // With Progress bar and downloads lightweight files
        /*try {
            URL url = new URL(fileUrl);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            int contentLength = httpConnection.getContentLength();

            try (InputStream inputStream = httpConnection.getInputStream();
                 FileOutputStream outputStream = new FileOutputStream(localFilePath)) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                long totalBytesRead = 0;
                int percentCompleted = 0;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                    int newPercentCompleted = (int) (totalBytesRead * 100 / contentLength);

                    if (newPercentCompleted > percentCompleted) {
                        percentCompleted = newPercentCompleted;
                        System.out.print("\rDownload progress: " + percentCompleted + "%");
                    }
                }
                System.out.println("\nFile downloaded successfully.");
            }
        } catch (IOException e) {
            System.err.println("An error occurred while downloading the file: " + e.getMessage());
        }*/

        String downloadUrl = "https://drive.google.com/uc?id=" + fileUrl + "&export=download";

        URL url = new URL(downloadUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        // Handle redirects and get the final URL
        String cookies = "";
        boolean redirect = false;
        int status = conn.getResponseCode();
        if (status != HttpURLConnection.HTTP_OK) {
            if (status == HttpURLConnection.HTTP_MOVED_TEMP
                    || status == HttpURLConnection.HTTP_MOVED_PERM
                    || status == HttpURLConnection.HTTP_SEE_OTHER) {
                redirect = true;
            }
        }

        if (redirect) {
            String newUrl = conn.getHeaderField("Location");
            cookies = conn.getHeaderField("Set-Cookie");
            conn = (HttpURLConnection) new URL(newUrl).openConnection();
            conn.setRequestProperty("Cookie", cookies);
        }

        try (InputStream in = conn.getInputStream();
             FileOutputStream out = new FileOutputStream(localFilePath)) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            long totalBytesRead = 0;
            long fileSize = conn.getContentLengthLong();

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                int percentCompleted = (int) (totalBytesRead * 100 / fileSize);
                System.out.print("\rDownload progress: " + percentCompleted + "%");
            }
            System.out.println("\nFile downloaded successfully.");
        }
    }
}
