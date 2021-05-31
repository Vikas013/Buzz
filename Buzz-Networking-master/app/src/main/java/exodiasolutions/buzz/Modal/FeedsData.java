package exodiasolutions.buzz.Modal;

/**
 * Created by Sunny on 22-11-2017.
 */

public class FeedsData {
    String id,name,text,image,datetime,likes,comments,profile_img,location,liked;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public String getImage() {
        return image;
    }

    public String getProfile_img() {
        return profile_img;
    }

    public String getDatetime() {
        return datetime;
    }

    public String getLiked() {
        return liked;
    }

    public String getLikes() {
        return likes;
    }

    public String getLocation() {
        return location;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setLiked(String liked) {
        this.liked = liked;
    }

    public String getComments() {
        return comments;
    }

    public FeedsData(String id, String name, String text, String image, String datetime, String likes, String comments,String profile_img,String location,String liked){
        this.id = id;
        this.liked = liked;
        this.name = name;
        this.text = text;
        this.image = image;
        this.datetime = datetime;
        this.likes=likes;
        this.comments = comments;
        this.profile_img=profile_img;
        this.location = location;
    }


}
