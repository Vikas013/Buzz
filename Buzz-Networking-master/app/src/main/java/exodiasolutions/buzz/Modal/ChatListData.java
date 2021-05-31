package exodiasolutions.buzz.Modal;

/**
 * Created by Sunny on 23-11-2017.
 */

public class ChatListData {
    String name,username,photo;
    public ChatListData(String name,String username,String photo){
        this.name= name;
        this.username= username;
        this.photo = photo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPhoto() {
        return photo;
    }
}
