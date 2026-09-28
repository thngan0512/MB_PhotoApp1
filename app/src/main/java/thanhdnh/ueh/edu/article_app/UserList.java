package thanhdnh.ueh.edu.article_app;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UserList {

  @SerializedName(value = "users", alternate = {"user_list", "profiles", "articles"})
  @Expose
  private ArrayList<UserProfile> users;

  public UserList() {
    this.users = new ArrayList<>();
  }

  public UserList(ArrayList<UserProfile> users) {
    this.users = users;
  }

  public ArrayList<UserProfile> getUsers() {
    return users;
  }

  public void setUsers(ArrayList<UserProfile> users) {
    this.users = users;
  }
}
