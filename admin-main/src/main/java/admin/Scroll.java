package admin;

public class Scroll {
    private String id;
    private String name;
    private String path;
    private String author;
    private int viewCount;
    private int downloadCount;

    public Scroll(String id, String name, String path, String author) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.author = author;
        this.viewCount = 0;
        this.downloadCount = 0;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getAuthor() {
        return author;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }


    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

}
