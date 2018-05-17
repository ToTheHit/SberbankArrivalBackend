public class SocketInfo {
    private String user;
    private String nickname;
    private String title;
    private String artist;
    private String albumArtist;
    private String albumTitle;
    private String genre;
    private String artwork; // 300x300
    private String trackPreviewURL;
    private String trackFullURL;
    private double latitude;
    private double longitude;

    public SocketInfo() {
    }

    public SocketInfo(String user, String nickname, String title, String artist, String albumArtist, String albumTitle, String genre, String artwork, String trackPreviewURL, String trackFullURL, double latitude, double longitude) {
        super();
        this.user = user;
        this.nickname = nickname;
        this.title = title;
        this.artist = artist;
        this.albumArtist = albumArtist;
        this.albumTitle = albumTitle;
        this.genre = genre;
        this.artwork = artwork;
        this.trackPreviewURL = trackPreviewURL;
        this.trackFullURL = trackFullURL;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbumArtist() {
        return albumArtist;
    }

    public void setAlbumArtist(String albumArtist) {
        this.albumArtist = albumArtist;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getArtwork() {
        return artwork;
    }

    public void setArtwork(String artwork) {
        this.artwork = artwork;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}