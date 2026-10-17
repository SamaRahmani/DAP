package com.dap.calculation.admin.city.dto;

public class AdminCityRequestDTO {

    private String cityName;
    private String stateCode;

    public AdminCityRequestDTO() {
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }
}