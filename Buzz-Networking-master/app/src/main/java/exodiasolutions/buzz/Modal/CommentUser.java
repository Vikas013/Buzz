package exodiasolutions.buzz.Modal;

/**
 * Created by Sunny on 22-11-2017.
 */

public class CommentUser {
    String image,name,datetime,comment;
    public CommentUser(String name,String image,String datetime,String comment){
        this.name=name;
        this.comment=comment;
        this.image=image;
        this.datetime=datetime;

    }

    public String getImage() {
        return image;
    }

    public String getComment() {
        return comment;
    }

    public String getName() {
        return name;
    }

    public String getDatetime() {
        return datetime;
    }
}
