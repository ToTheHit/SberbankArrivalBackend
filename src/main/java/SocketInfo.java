public class SocketInfo {
    private String user;
    private String nickname;
    private String trackTitle;
    private String trackAuthor;
    private String artwork150; // 150x150
    private String artwork500; // 500x500
    private String trackPreviewURL;
    private String trackFullURL;
    private double latitude;
    private double longitude;

    public SocketInfo() {
    }

    public SocketInfo(String user, String nickname, String trackTitle, String trackAuthor, String artwork150, String artwork500, String trackPreviewURL, String trackFullURL, double latitude, double longitude) {
        super();
        this.user = user;
        this.nickname = nickname;
        this.trackTitle = trackTitle;
        this.trackAuthor = trackAuthor;
        this.artwork150 = artwork150;
        this.artwork500 = artwork500;
        this.trackPreviewURL = trackPreviewURL;
        this.trackFullURL = trackFullURL;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getTrackPreviewURL() {
        return trackPreviewURL;
    }
    public void setTrackPreviewURL(String trackPreviewURL) {
        this.trackPreviewURL = trackPreviewURL;
    }

    public String getTrackFullURL() {
        return trackFullURL;
    }
    public void setTrackFullURL(String trackFullURL) {
        this.trackFullURL = trackFullURL;
    }

    public String getArtwork150() {
        return artwork150;
    }
    public void setArtwork150(String artwork150) {
        this.artwork150 = artwork150;
    }

    public String getArtwork500() {
        return artwork500;
    }
    public void setArtwork500(String artwork500) {
        this.artwork500 = artwork500;
    }

    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }

    public String getTrackTitle() {
        return trackTitle;
    }
    public void setTrackTitle(String trackTitle) {
        this.trackTitle = trackTitle;
    }

    public String getTrackAuthor() {
        return trackAuthor;
    }
    public void setTrackAuthor(String trackAuthor) {
        this.trackAuthor = trackAuthor;
    }

    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }
    public void setLongitube(double longitude) {
        this.longitude = longitude;
    }
}

