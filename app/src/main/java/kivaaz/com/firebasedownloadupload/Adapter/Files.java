package kivaaz.com.firebasedownloadupload.Adapter;

/**
 * Created by Muguntan on 11/26/2017.
 */

public class Files {
    String id;
    String name;
    String url;
    String local_url;
    String type;
    String downloaded;
    String userEmail;

    public Files() {
    }

    public Files(String id, String name, String url,String local_url, String type, String downloaded, String userEmail) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.local_url = local_url;
        this.type = type;
        this.downloaded = downloaded;
        this.userEmail = userEmail;
    }

    public Files(String name, String url,String local_url, String type, String downloaded, String userEmail) {
        this.name = name;
        this.url = url;
        this.local_url = local_url;
        this.type = type;
        this.downloaded = downloaded;
        this.userEmail = userEmail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocal_url() {
        return local_url;
    }

    public void setLocal_url(String local_url) {
        this.local_url = local_url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(String downloaded) {
        this.downloaded = downloaded;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}






