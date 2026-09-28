package thanhdnh.ueh.edu.article_app;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class UserData {
  public static UserList data = new UserList();
  private Context context;
  private GridView gridview;
  private ProgressBar progressBar;

  public UserData(Context context, GridView gridview) {
    this.context = context;
    this.gridview = gridview;
  }

  public UserData(Context context, GridView gridview, ProgressBar progressBar) {
    this.context = context;
    this.gridview = gridview;
    this.progressBar = progressBar;
  }

  public static UserProfile getUserFromId(int id) {
    if (data != null && data.getUsers() != null) {
      for (UserProfile user : data.getUsers()) {
        if (user.getId() == id) {
          return user;
        }
      }
    }
    return null;
  }

  public void loadData(String url, Activity activity) {
    Handler mainHandler = new Handler(Looper.getMainLooper());
    Downloader.downloadWithProgress(url, context.getCacheDir(), new Downloader.ProgressCallback() {
      @Override
      public void onProgress(int percent) {
        mainHandler.post(() -> {
          if (progressBar != null) {
            progressBar.setProgress(percent);
          }
        });
      }

      @Override
      public void onSuccess(File downloadedFile) {
        mainHandler.post(() -> {
          try {
            String jsonStr = readText(downloadedFile);
            Gson gson = new Gson();
            data = gson.fromJson(jsonStr, UserList.class);
            if (data == null || data.getUsers() == null || data.getUsers().isEmpty()) {
              data = getSampleData();
            }
          } catch (Exception e) {
            data = getSampleData();
          }
          UserAdapter adapter = new UserAdapter(data.getUsers(), context);
          gridview.setAdapter(adapter);
        });
      }

      @Override
      public void onFailure(Exception e) {
        mainHandler.post(() -> {
          data = getSampleData();
          UserAdapter adapter = new UserAdapter(data.getUsers(), context);
          gridview.setAdapter(adapter);
        });
      }
    });
  }

  public static UserList getSampleData() {
    ArrayList<UserProfile> list = new ArrayList<>();
    list.add(new UserProfile(1, "nganthuy", "ngannguyen05122006@gmail.com", "Android Developer & Tech enthusiast", "https://images.unsplash.com/photo-1534528741775-53994a69daeb", "Photography, Coding, Music"));
    list.add(new UserProfile(2, "john_doe", "john.doe@example.com", "Mobile UI/UX Specialist", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d", "Travel, Reading"));
    list.add(new UserProfile(3, "alice_w", "alice.w@example.com", "Backend Engineer & Cloud architect", "https://images.unsplash.com/photo-1494790108377-be9c29b29330", "Hiking, Gaming"));
    list.add(new UserProfile(4, "bob_smith", "bob.smith@example.com", "Data Analyst & Machine Learning lover", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e", "Cooking, Swimming"));
    list.add(new UserProfile(5, "emma_watson", "emma.watson@example.com", "Frontend Developer & Designer", "https://images.unsplash.com/photo-1438761681033-6461ffad8d80", "Art, Coffee"));
    list.add(new UserProfile(6, "david_beck", "david.beck@example.com", "DevOps & System Administrator", "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e", "Football, Guitar"));
    return new UserList(list);
  }

  public String readText(File file) {
    try (InputStream stream = new FileInputStream(file);
         BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
      StringBuilder buffer = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        buffer.append(line).append("\n");
      }
      return buffer.toString();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "";
  }
}
