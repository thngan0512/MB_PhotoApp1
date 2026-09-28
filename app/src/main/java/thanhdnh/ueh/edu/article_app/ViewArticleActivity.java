package thanhdnh.ueh.edu.article_app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class ViewArticleActivity extends AppCompatActivity {
  private ImageView iv_detail;
  private TextView tv_detail_username, tv_detail_email, tv_detail_hobby, tv_detail_description;
  private ProgressBar pb_detail_progress;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view_article);
    if (getSupportActionBar() != null) {
      getSupportActionBar().hide();
    }

    iv_detail = findViewById(R.id.iv_detail);
    tv_detail_username = findViewById(R.id.tv_detail_username);
    tv_detail_email = findViewById(R.id.tv_detail_email);
    tv_detail_hobby = findViewById(R.id.tv_detail_hobby);
    tv_detail_description = findViewById(R.id.tv_detail_description);
    pb_detail_progress = findViewById(R.id.pb_detail_progress);

    int id = (int) getIntent().getLongExtra("id", 0);
    UserProfile user = UserData.getUserFromId(id);

    if (user != null) {
      tv_detail_username.setText(user.getUsername());
      tv_detail_email.setText("Email: " + (user.getEmail() != null ? user.getEmail() : "N/A"));
      tv_detail_hobby.setText("Sở thích: " + (user.getHobby() != null ? user.getHobby() : "N/A"));
      tv_detail_description.setText(user.getDescription());

      if (user.getAvatar_url() != null && !user.getAvatar_url().isEmpty()) {
        pb_detail_progress.setVisibility(View.VISIBLE);
        Handler mainHandler = new Handler(Looper.getMainLooper());
        // Load image using Downloader with progress bar as requested by instructor
        Downloader.downloadWithProgress(user.getAvatar_url(), mainHandler, this, getCacheDir(), pb_detail_progress, iv_detail);
      }
    }
  }
}
