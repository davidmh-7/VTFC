package com.example.vtfc_master;

public class Camara {

    private Long id;
    private String cameraId;
    private String sourceId;
    private String cameraName;
    private String urlImage;
    private String latitude;
    private String longitude;
    private String kilometer;
    private String address;
    private String road;
    private boolean isFavoritoCam;


    public Camara(Long id, String cameraId, String sourceId, String cameraName, String urlImage, String latitude, String longitude, String kilometer, String address, String road, boolean isFavoritoCam) {
        this.id = id;
        this.cameraId = cameraId;
        this.sourceId = sourceId;
        this.cameraName = cameraName;
        this.urlImage = urlImage;
        this.latitude = latitude;
        this.longitude = longitude;
        this.kilometer = kilometer;
        this.address = address;
        this.road = road;
        this.isFavoritoCam = isFavoritoCam;
    }


    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCameraId() {
        return cameraId;
    }
    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
    }
    public String getSourceId() {
        return sourceId;
    }
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
    public String getCameraName() {
        return cameraName;
    }
    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }
    public String getUrlImage() {
        return urlImage;
    }
    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }
    public String getLatitude() {
        return latitude;
    }
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    public String getLongitude() {
        return longitude;
    }
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    public String getKilometer() {
        return kilometer;
    }
    public void setKilometer(String kilometer) {
        this.kilometer = kilometer;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getRoad() {
        return road;
    }
    public void setRoad(String road) {
        this.road = road;
    }
    public boolean isFavoritoCam() {
        return isFavoritoCam;
    }

    public void setFavoritoCam(boolean isFavoritoCam) {
        this.isFavoritoCam = isFavoritoCam;
    }

}
