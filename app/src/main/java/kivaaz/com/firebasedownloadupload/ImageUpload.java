package kivaaz.com.firebasedownloadupload;

/**
 * Created by Muguntan on 11/26/2017.
 */

public class ImageUpload {
    private String name;
    private String url;
    private String type;
    private String key;

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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ImageUpload(String name, String url, String type, String key) {
        this.name = name;
        this.url = url;
        this.type = type;
        this.key = key;
    }

    public ImageUpload() {
    }
}
