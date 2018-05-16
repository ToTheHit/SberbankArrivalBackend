public class SocketInfo {
    private String user;
    private String nickname;
    private String trackTitle;
    private String trackAuthor;
    private String trackArtURL;
    private String trackPreviewURL;
    private String trackFullURL;
    private double latitude;
    private double longitube;

    public SocketInfo() {
    }

    public SocketInfo(String user, String nickname, String trackTitle, String trackAuthor, String trackArtURL, String trackPreviewURL, String trackFullURL, double latitude, double longitube) {
        super();
        this.user = user;
        this.nickname = nickname;
        this.trackTitle = trackTitle;
        this.trackTitle = trackAuthor;
        this.trackArtURL = trackArtURL;
        this.trackPreviewURL = trackPreviewURL;
        this.trackFullURL = trackFullURL;
        this.latitude = latitude;
        this.longitube = longitube;
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

    public double getLongitube() {
        return longitube;
    }
    public void setLongitube(double longitube) {
        this.longitube = longitube;
    }

}

