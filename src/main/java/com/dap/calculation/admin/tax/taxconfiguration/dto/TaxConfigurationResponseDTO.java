
package com.dap.calculation.admin.tax.taxconfiguration.dto;

import java.util.List;

public class TaxConfigurationResponseDTO {

    private List<TaxTypeOption> taxTypes;
    private List<ConditionFieldOption> conditionFields;
    private List<CalculationBaseOption> calculationBases;

    public TaxConfigurationResponseDTO() {
    }

    public List<TaxTypeOption> getTaxTypes() {
        return taxTypes;
    }

    public void setTaxTypes(List<TaxTypeOption> taxTypes) {
        this.taxTypes = taxTypes;
    }

    public List<ConditionFieldOption> getConditionFields() {
        return conditionFields;
    }

    public void setConditionFields(
            List<ConditionFieldOption> conditionFields) {
        this.conditionFields = conditionFields;
    }

    public List<CalculationBaseOption> getCalculationBases() {
        return calculationBases;
    }

    public void setCalculationBases(
            List<CalculationBaseOption> calculationBases) {
        this.calculationBases = calculationBases;
    }

    public static class TaxTypeOption {

        private Long taxTypeId;
        private String taxCode;
        private String taxName;

        public TaxTypeOption() {
        }

        public Long getTaxTypeId() {
            return taxTypeId;
        }

        public void setTaxTypeId(Long taxTypeId) {
            this.taxTypeId = taxTypeId;
        }

        public String getTaxCode() {
            return taxCode;
        }

        public void setTaxCode(String taxCode) {
            this.taxCode = taxCode;
        }

        public String getTaxName() {
            return taxName;
        }

        public void setTaxName(String taxName) {
            this.taxName = taxName;
        }
    }

    public static class ConditionFieldOption {

        private Long conditionFieldId;
        private String fieldCode;
        private String fieldLabel;
        private String dataType;
        private String valueSource;
        private List<String> operators;
        private List<String> availableValues;

        public ConditionFieldOption() {
        }

        public Long getConditionFieldId() {
            return conditionFieldId;
        }

        public void setConditionFieldId(Long id) {
            this.conditionFieldId = id;
        }

        public String getFieldCode() {
            return fieldCode;
        }

        public void setFieldCode(String fieldCode) {
            this.fieldCode = fieldCode;
        }

        public String getFieldLabel() {
            return fieldLabel;
        }

        public void setFieldLabel(String fieldLabel) {
            this.fieldLabel = fieldLabel;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public String getValueSource() {
            return valueSource;
        }

        public void setValueSource(String valueSource) {
            this.valueSource = valueSource;
        }

        public List<String> getOperators() {
            return operators;
        }

        public void setOperators(List<String> operators) {
            this.operators = operators;
        }

        public List<String> getAvailableValues() {
            return availableValues;
        }

        public void setAvailableValues(List<String> availableValues) {
            this.availableValues = availableValues;
        }
    }

    public static class CalculationBaseOption {

        private Long calculationBaseId;
        private String baseCode;
        private String baseName;
        private String description;

        public CalculationBaseOption() {
        }

        public Long getCalculationBaseId() {
            return calculationBaseId;
        }

        public void setCalculationBaseId(Long id) {
            this.calculationBaseId = id;
        }

        public String getBaseCode() {
            return baseCode;
        }

        public void setBaseCode(String baseCode) {
            this.baseCode = baseCode;
        }

        public String getBaseName() {
            return baseName;
        }

        public void setBaseName(String baseName) {
            this.baseName = baseName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
