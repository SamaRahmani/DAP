package com.dap.calculation.admin.rto.dto;

public class AdminRtoResponseDTO {

    private String rtoCode;
    private String rtoName;
    private String stateCode;
    private Long cityId;

    public AdminRtoResponseDTO() {
    }

    public AdminRtoResponseDTO(
            String rtoCode,
            String rtoName,
            String stateCode,
            Long cityId) {

        this.rtoCode = rtoCode;
        this.rtoName = rtoName;
        this.stateCode = stateCode;
        this.cityId = cityId;
    }

    public String getRtoCode() {
        return rtoCode;
    }

    public void setRtoCode(String rtoCode) {
        this.rtoCode = rtoCode;
    }

    public String getRtoName() {
        return rtoName;
    }

    public void setRtoName(String rtoName) {
        this.rtoName = rtoName;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }
}