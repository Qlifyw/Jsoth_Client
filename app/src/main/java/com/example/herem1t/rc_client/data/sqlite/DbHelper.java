package com.example.herem1t.rc_client.data.sqlite;

import com.example.herem1t.rc_client.data.sqlite.model.Server;

import java.util.List;
import java.util.Map;

public interface DbHelper {

    void updateServerStatus(String extIp, int status);
    long addServer(String extIp, String pass, String description, double lat, double lon);
    String getServerDescription(String extIp);
    int updateServerDescription(String extIp, String description);
    int getServerStatus(String extIp);
    String getServerLogo(String extIp);
    int changeServerConnectionPassword(String extIp, String pass);
    List<Server> getLocationInfo();
    int getServersCountByStatus(int filter);
    List<Server> getRamUsages(String extIp);
    List<Server> getCPUUsages(String extIp);
    List<Server> getDiskUsages(String extIp);
    List<Server> getAllServers();
    List<Server> sortBy(int by);
    Server getServer(String extIp);
    void updateServerInfo(Server server);
    String getDisks(String extIp);
    int deleteRows(String extIp);
    int addToFavourite(String extIp);
    int removeFromFavourite(String extIp);
    void deleteAll();
    String getKey(String extIp);
    String getHashedPass(String extIp);
    boolean isServerExists(String extIp);
    boolean isHandshakeGotten(String extIp);
    void deleteMonthRows();
    void updateKey(byte[] key, String ip);
    List<String> getIPAddressList();

}
