package com.example.herem1t.rc_client.data.sqlite.model;

import com.example.herem1t.rc_client.data.network.api.model.Coordinates;

public class Server {
    public final static int INIT_RAM = 1;
    public final static int INIT_CPU = 2;
    public final static int INIT_HDD = 3;

    public static final int SERVER_DOWN = -1;
    public static final int SERVER_NOT_RESPONDING = 0;
    public static final int SERVER_AVAILABLE = 1;

    private int status;
    private int favourite;
    private double swap;
    private String extIp;
    private String description;
    private String countryCode;
    private String localIp;
    private String machineName;
    private String osName;
    private String cpuInfo;
    private String uptime;
    private String disks;
    private String disksUsages;
    private String timestamp;
    private String ram;
    private String ramUsages;
    private String cpuUsages;
    private String logoPath;
    private Coordinates coordinates;

    public Server() {

    }

    public Server(String extIp,
                  String description,
                  String countryCode,
                  String localIp,
                  String machineName,
                  String osName,
                  String cpuInfo,
                  String uptime,
                  String disks,
                  String ram,
                  String logoPath,
                  double lat,
                  double lon,
                  int status,
                  int favourite) {
        this.extIp = extIp;
        this.description = description;
        this.countryCode = countryCode;
        this.localIp = localIp;
        this.machineName = machineName;
        this.osName = osName;
        this.cpuInfo = cpuInfo;
        this.uptime = uptime;
        this.disks = disks;
        this.ram = ram;
        this.logoPath = logoPath;
        this.coordinates = new Coordinates(lat, lon);
        this.status = status;
        this.favourite = favourite;
    }


    public Server(String extIp, String description) {
        this.extIp = extIp;
        this.description = description;
    }

    public Server(String extIp, String description, String logo, int favourite, int status) {
        this.extIp = extIp;
        this.description = description;
        this.logoPath = logo;
        this.favourite = favourite;
        this.status = status;
    }


    public Server(String extIp, double lat, double lon) {
        this.extIp = extIp;
        this.coordinates = new Coordinates(lat,lon);
    }

    // int just for avoid overload conflict
    public Server(String data, String timestamp, int intent) {
        this.timestamp = timestamp;
        switch (intent) {
            case INIT_RAM:
                this.ramUsages = data;
                break;
            case INIT_CPU:
                this.cpuUsages = data;
                break;
            case INIT_HDD:
                this.disksUsages = data;
            default:
                break;
        }

    }

    public boolean isFavourite() {
        return favourite == 1;
    }

    public String getDisksUsages(){
        return disksUsages;
    }

    public void setDisksUsages(String disksUsages){
        this.disksUsages = disksUsages;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName){
        this.machineName = machineName;
    }

    public double getSWAP() {
        return swap;
    }

    public void setSWAP(double swap){
        this.swap = swap;
    }

    public String getDisks() {
        return disks;
    }

    public void setDisks(String disks){
        this.disks = disks;
    }

    public String getLocalIP(){
        return localIp;
    }

    public void setLocalIP(String localIp){
        this.localIp = localIp;
    }

    public String getOSname(){
        return osName;
    }

    public void setOSname(String osName){
        this.osName = osName;
    }

    public String getLogoPath(){
        return logoPath;
    }

    public void setLogoPath(String logoPath){
        this.logoPath = logoPath;
    }

    public String getCPUInfo(){
        return cpuInfo;
    }

    public void setCPUInfo(String cpuInfo){
        this.cpuInfo = cpuInfo;
    }

    public String getUptime(){
        return uptime;
    }

    public void setUptime(String uptime){
        this.uptime = uptime;
    }

    public String getRam(){
        return ram;
    }

    public void setRam(String ram){
        this.ram = ram;
    }

    public String getCpuUsages(){
        return cpuUsages;
    }

    public void setCpuUsages(String cpuUsages){
        this.cpuUsages = cpuUsages;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getRamUsages() {
        return ramUsages;
    }

    public void setRamUsages(String ramUsages){
        this.ramUsages = ramUsages;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public String getExternalIP() {
        return extIp;
    }

    public void setExternalIP(String extIp) {
        this.extIp = extIp;
    }

    public String getCountyCode() {
        return countryCode;
    }

    public void setCountyCode(String countryCode){
        this.countryCode = countryCode;
    }

    public String getDescription() {
        return description;
    }

    public int getStatus() {return status;}

    public void setFavourite(boolean favourite) {
        this.favourite = favourite? 1 : 0;
    }


}
