package com.daily.gccollector.Utility;

public class AppConstraint {

    public  static String PRF_LOGINAUTH= "LoginAuth";
    public  static String PRF_TOKEN= "token";
    public  static String PRF_USER= "user";
    public  static String PRF_DEVICENO= "deviceno";
    public  static String PRF_VEHICLENO= "vehicleno";
    public  static String PRF_BINNO= "binno";

    public  static String BASE_URL="http://103.20.213.26:8099/api";

    //APIs
    public  static String POST_LOGIN= BASE_URL+"/Auth/login";
    public  static String GET_DEVICE_DTL= BASE_URL+"/Master/GetDeviceNo";
    public  static String GET_VEHICLE_DTL= BASE_URL+"/Master/GetVehicleNo";
    public  static String GET_BIN_DTL= BASE_URL+"/Master/GetBinDetail";
    public  static String POST_BIN= BASE_URL+"/Master/UpsertBinScanningData";

    public  static String GET_ALL_BIN= BASE_URL+"/Master/GetBinLocation";
}
