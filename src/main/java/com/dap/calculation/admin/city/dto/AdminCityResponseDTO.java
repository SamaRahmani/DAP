package com.dap.calculation.admin.city.dto;

public class AdminCityResponseDTO {

    private Long cityId;
    private String cityName;
    private String stateCode;

    public AdminCityResponseDTO() {
    }

    public AdminCityResponseDTO(Long cityId, String cityName, String stateCode) {
        this.cityId = cityId;
        this.cityName = cityName;
        this.stateCode = stateCode;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
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