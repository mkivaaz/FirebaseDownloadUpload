package kivaaz.com.firebasedownloadupload;

/**
 * Created by Muguntan on 11/26/2017.
 */

public class ImageUpload {
    private String name;
    private String url;
    private String type;

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ImageUpload(String Name, String Url, String Type) {
        this.name = Name;
        this.url = Url;
        this.type = Type;
    }

    public ImageUpload() {
    }
}
