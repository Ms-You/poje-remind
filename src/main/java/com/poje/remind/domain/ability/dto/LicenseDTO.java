package com.poje.remind.domain.ability.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class LicenseDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateReq {
        private String name;
        private String issueInstitution;
        private LocalDate issueDate;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateReq {
        private String name;
        private String issueInstitution;
        private LocalDate issueDate;
    }
    
    @Getter
    @AllArgsConstructor
    public static class LicenseResp {
        private String name;
        private String issueInstitution;
        private LocalDate issueDate;
    }
    
    @Getter
    public static class LicenseListResp {
        private List<LicenseResp> licenseRespList;
        
        public LicenseListResp(List<LicenseResp> licenseRespList) {
            this.licenseRespList = licenseRespList;
        }
    }
}
