package thanhdnh.ueh.edu.article_app;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

public class Downloader {
  public static String cached_file_path = "";

  public interface ProgressCallback {
    void onProgress(int percent);
    void onSuccess(File downloadedFile);
    void onFailure(Exception e);
  }

  public static void downloadWithProgress(String url, File storageDir, ProgressCallback callback) {
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder().url(url).build();

    client.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        if (callback != null) callback.onFailure(e);
      }

      @Override
      public void onResponse(Call call, Response response) {
        if (!response.isSuccessful() || response.body() == null) {
          if (callback != null) callback.onFailure(new IOException("Request failed with status code: " + response.code()));
          return;
        }

        try {
          String contentType = response.header("Content-Type", "");
          String extension = getExtensionFromMimeType(contentType);
          File file = File.createTempFile("downloaded_", extension, storageDir);

          long totalBytes = response.body().contentLength();
          InputStream inputStream = response.body().byteStream();
          try (OutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[2048];
            long downloadedBytes = 0;
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
              outputStream.write(buffer, 0, bytesRead);
              downloadedBytes += bytesRead;
              if (totalBytes > 0 && callback != null) {
                int progress = (int) ((downloadedBytes * 100) / totalBytes);
                callback.onProgress(progress);
              }
            }
            outputStream.flush();
          }

          if (callback != null) {
            callback.onSuccess(file);
          }
        } catch (Exception e) {
          if (callback != null) callback.onFailure(e);
        }
      }
    });
  }

  public static void downloadWithProgress(String inputurl, Handler mainHandler, Context context, File where2store, ProgressBar progressBar, ImageView imageView) {
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder().url(inputurl).build();

    client.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        if (mainHandler != null) {
          mainHandler.post(() -> {
            if (progressBar != null) progressBar.setVisibility(ProgressBar.INVISIBLE);
          });
        }
      }

      @Override
      public void onResponse(Call call, Response response) {
        if (!response.isSuccessful() || response.body() == null) {
          if (mainHandler != null) {
            mainHandler.post(() -> {
              if (progressBar != null) progressBar.setVisibility(ProgressBar.INVISIBLE);
            });
          }
          return;
        }

        long totalBytes = response.body().contentLength();
        InputStream inputStream = response.body().byteStream();
        String contentType = response.header("Content-Type", "");
        String extension = getExtensionFromMimeType(contentType);

        try {
          File targetFile = new File(where2store, "downloaded_file" + extension);
          try (OutputStream outputStream = new FileOutputStream(targetFile)) {
            byte[] buffer = new byte[1024];
            long downloadedBytes = 0;
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
              outputStream.write(buffer, 0, bytesRead);
              downloadedBytes += bytesRead;
              if (totalBytes > 0 && mainHandler != null && progressBar != null) {
                int progress = (int) ((downloadedBytes * 100) / totalBytes);
                mainHandler.post(() -> progressBar.setProgress(progress));
              }
            }
            outputStream.flush();
          }

          if (mainHandler != null) {
            mainHandler.post(() -> {
              cached_file_path = targetFile.getAbsolutePath();
              if (imageView != null) {
                imageView.setImageURI(Uri.fromFile(targetFile));
              }
              if (progressBar != null) {
                progressBar.setVisibility(ProgressBar.INVISIBLE);
              }
            });
          }
        } catch (Exception e) {
          if (mainHandler != null) {
            mainHandler.post(() -> {
              if (progressBar != null) progressBar.setVisibility(ProgressBar.INVISIBLE);
            });
          }
        }
      }
    });
  }

  public static File downloadFile(String url, File cached) {
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder().url(url).build();

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) return null;
      String contentType = response.header("Content-Type", "");
      String extension = getExtensionFromMimeType(contentType);
      File file = File.createTempFile("downloaded_file", extension, cached);
      if (response.body() != null) {
        BufferedSink sink = Okio.buffer(Okio.sink(file));
        sink.writeAll(response.body().source());
        sink.close();
        return file;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  private static String getExtensionFromMimeType(String mimeType) {
    Map<String, String> mimeMap = new HashMap<>();
    mimeMap.put("image/jpeg", ".jpg");
    mimeMap.put("image/png", ".png");
    mimeMap.put("application/json", ".json");
    if (mimeType == null) return "";
    for (Map.Entry<String, String> entry : mimeMap.entrySet()) {
      if (mimeType.contains(entry.getKey())) return entry.getValue();
    }
    return ".json";
  }
}
