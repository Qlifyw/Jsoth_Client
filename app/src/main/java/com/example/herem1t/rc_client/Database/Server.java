package com.example.herem1t.rc_client.Database;

import com.example.herem1t.rc_client.Net.Coordinates;

/**
 * Created by Herem1t on 16.04.2018.
 */

public class Server {
    final static int INIT_RAM = 1;
    final static int INIT_CPU = 2;
    final static int INIT_HDD = 3;

    private int status;
    private int favourite;
    private double swap;
    private String ext_ip;
    private String description;
    private String country_code;
    private String local_ip;
    private String machine_name;
    private String os_name;
    private String cpu_info;
    private String uptime;
    private String disks;
    private String disks_usages;
    private String timestamp;
    private String ram;
    private String ram_usages;
    private String cpu_usages;
    private String logo_path;
    private Coordinates coordinates;

    public Server() {

    }

    public Server(String ext_ip, String description, String country_code, String local_ip, String machine_name, String os_name,
                  String cpu_info, String uptime, String disks, String ram, String logo_path, double lat, double lon, int status,
                  int favourite) {
        this.ext_ip = ext_ip;
        this.description = description;
        this.country_code = country_code;
        this.local_ip = local_ip;
        this.machine_name = machine_name;
        this.os_name = os_name;
        this.cpu_info = cpu_info;
        this.uptime = uptime;
        this.disks = disks;
        this.ram = ram;
        this.logo_path = logo_path;
        this.coordinates = new Coordinates(lat, lon);
        this.status = status;
        this.favourite = favourite;
    }


    public Server(String ext_ip, String description) {
        this.ext_ip = ext_ip;
        this.country_code = "DE";
        this.description = description;
    }

    public Server(String ext_ip, String description, String logo, int favourite, int status) {
        this.ext_ip = ext_ip;
        this.description = description;
        this.logo_path = logo;
        this.favourite = favourite;
        this.status = status;
    }


    public Server(String ext_ip, double lat, double lon) {
        this.ext_ip = ext_ip;
        this.coordinates = new Coordinates(lat,lon);
    }

    // int just for avoid overload conflict
    public Server(String data, String timestamp, int intent) {
        this.timestamp = timestamp;
        switch (intent) {
            case INIT_RAM:
                this.ram_usages = data;
                break;
            case INIT_CPU:
                this.cpu_usages = data;
                break;
            case INIT_HDD:
                this.disks_usages = data;
            default:
                break;
        }

    }

    public boolean isFavourite() {
        return favourite == 1;
    }

    public String getDisksUsages(){
        return disks_usages;
    }

    public void setDisksUsages(String disks_usages){
        this.disks_usages = disks_usages;
    }

    public String getMachineName() {
        return machine_name;
    }

    public void setMachineName(String machine_name){
        this.machine_name = machine_name;
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
        return local_ip;
    }

    public void setLocalIP(String local_ip){
        this.local_ip = local_ip;
    }

    public String getOSname(){
        return os_name;
    }

    public void setOSname(String os_name){
        this.os_name = os_name;
    }

    public String getLogoPath(){
        return logo_path;
    }

    public void setLogoPath(String logo_path){
        this.logo_path = logo_path;
    }

    public String getCPUInfo(){
        return cpu_info;
    }

    public void setCPUInfo(String cpu_info){
        this.cpu_info = cpu_info;
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
        return cpu_usages;
    }

    public void setCpuUsages(String cpu_usages){
        this.cpu_usages = cpu_usages;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getRamUsages() {
        return ram_usages;
    }

    public void setRamUsages(String ram_usages){
        this.ram_usages = ram_usages;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public String getExternalIP() {
        return ext_ip;
    }

    public void setExternalIP(String ext_ip) {
        this.ext_ip = ext_ip;
    }

    public String getCountyCode() {
        return country_code;
    }

    public void setCountyCode(String country_code){
        this.country_code = country_code;
    }

    public String getDescription() {
        return description;
    }

    public int getStatus() {return status;}

    public void setFavourite(boolean favourite) {
        this.favourite = favourite? 1 : 0;
    }


}
