package com.daily.gccollector.Models;

import java.util.Date;

public class BinMaster {

        public int BinLocID ;
    public String BinLocName ;
    public String BinLocCode ;
    public int ZoneID ;
    public int AreaID ;
    public String RFID ;
    public String Latitude ;
    public String Longitude ;
    public String Description ;
    public String CreatedBy ;
    public String UpdatedBy ;
    public Date CreatedOn ;
    public Date UpdatedOn ;
    public String LocImage ;

    public BinMaster()
    {

    }
    public int getBinLocID() {
        return BinLocID;
    }

    public void setBinLocID(int binLocID) {
        BinLocID = binLocID;
    }

    public String getBinLocName() {
        return BinLocName;
    }

    public void setBinLocName(String binLocName) {
        this.BinLocName = binLocName;
    }

    public String getBinLocCode() {
        return BinLocCode;
    }

    public void setBinLocCode(String binLocCode) {
        this.BinLocCode = binLocCode;
    }

    public int getZoneID() {
        return ZoneID;
    }

    public void setZoneID(int zoneID) {
        ZoneID = zoneID;
    }

    public int getAreaID() {
        return AreaID;
    }

    public void setAreaID(int areaID) {
        AreaID = areaID;
    }

    public String getRFID() {
        return RFID;
    }

    public void setRFID(String RFID) {
        this.RFID = RFID;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(String createdBy) {
        CreatedBy = createdBy;
    }

    public String getUpdatedBy() {
        return UpdatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        UpdatedBy = updatedBy;
    }

    public Date getCreatedOn() {
        return CreatedOn;
    }

    public void setCreatedOn(Date createdOn) {
        CreatedOn = createdOn;
    }

    public Date getUpdatedOn() {
        return UpdatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        UpdatedOn = updatedOn;
    }

    public String getLocImage() {
        return LocImage;
    }

    public void setLocImage(String locImage) {
        LocImage = locImage;
    }
}
