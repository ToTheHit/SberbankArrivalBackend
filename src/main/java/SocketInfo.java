public class SocketInfo {
    private String user;
    private String nickname;
    private String trackTitle;
    private String trackAuthor;
    private String trackMiniArtURL; // 150x150
    private String trackArtURL; // 500x500
    private String trackPreviewURL;
    private String trackFullURL;
    private double latitude;
    private double longitude;

    public SocketInfo() {
    }

    public SocketInfo(String user, String nickname, String trackTitle, String trackAuthor, String trackMiniArtURL, String trackArtURL, String trackPreviewURL, String trackFullURL, double latitude, double longitude) {
        super();
        this.user = user;
        this.nickname = nickname;
        this.trackTitle = trackTitle;
        this.trackTitle = trackAuthor;
        this.trackMiniArtURL = trackMiniArtURL;
        this.trackArtURL = trackArtURL;
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

    public String getTrackMiniArtURL() {
        return trackMiniArtURL;
    }
    public void setTrackMiniArtURL(String trackMiniArtURL) {
        this.trackMiniArtURL = trackMiniArtURL;
    }

    public String getTrackArtURL() {
        return trackArtURL;
    }
    public void setTrackArtURL(String trackArtURL) {
        this.trackArtURL = trackArtURL;
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

