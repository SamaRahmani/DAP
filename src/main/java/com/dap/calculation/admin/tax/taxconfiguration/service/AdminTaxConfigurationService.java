
package com.dap.calculation.admin.tax.taxconfiguration.service;

import com.dap.calculation.admin.tax.taxconfiguration.dto.TaxConfigurationResponseDTO;

import com.dap.calculation.entity.TaxConditionField;
import com.dap.calculation.entity.TaxConditionFieldOperator;
import com.dap.calculation.entity.TaxCalculationBase;
import com.dap.calculation.entity.TaxType;

import com.dap.calculation.repository.TaxConditionFieldRepository;
import com.dap.calculation.repository.TaxConditionFieldOperatorRepository;
import com.dap.calculation.repository.TaxCalculationBaseRepository;
import com.dap.calculation.repository.TaxTypeRepository;
import com.dap.calculation.repository.VehicleRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminTaxConfigurationService {

    private final TaxTypeRepository taxTypeRepository;
    private final TaxConditionFieldRepository fieldRepository;
    private final TaxConditionFieldOperatorRepository operatorRepository;
    private final TaxCalculationBaseRepository calculationBaseRepository;
    private final VehicleRepository vehicleRepository;

    public AdminTaxConfigurationService(
            TaxTypeRepository taxTypeRepository,
            TaxConditionFieldRepository fieldRepository,
            TaxConditionFieldOperatorRepository operatorRepository,
            TaxCalculationBaseRepository calculationBaseRepository,
            VehicleRepository vehicleRepository) {

        this.taxTypeRepository = taxTypeRepository;
        this.fieldRepository = fieldRepository;
        this.operatorRepository = operatorRepository;
        this.calculationBaseRepository = calculationBaseRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Transactional(readOnly = true)
    public TaxConfigurationResponseDTO getTaxConfiguration() {

        TaxConfigurationResponseDTO response =
                new TaxConfigurationResponseDTO();

        response.setTaxTypes(
                getTaxTypeOptions()
        );

        response.setConditionFields(
                getConditionFieldOptions()
        );

        response.setCalculationBases(
                getCalculationBaseOptions()
        );

        return response;
    }

    private List<TaxConfigurationResponseDTO.TaxTypeOption>
    getTaxTypeOptions() {

        return taxTypeRepository.findAll()
                .stream()
                .map(this::mapTaxType)
                .toList();
    }

    private TaxConfigurationResponseDTO.TaxTypeOption mapTaxType(
            TaxType taxType) {

        TaxConfigurationResponseDTO.TaxTypeOption option =
                new TaxConfigurationResponseDTO.TaxTypeOption();

        option.setTaxTypeId(taxType.getTaxTypeId());
        option.setTaxCode(taxType.getTaxCode());
        option.setTaxName(taxType.getTaxName());

        return option;
    }

    private List<TaxConfigurationResponseDTO.ConditionFieldOption>
    getConditionFieldOptions() {

        return fieldRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapConditionField)
                .toList();
    }

    private TaxConfigurationResponseDTO.ConditionFieldOption
    mapConditionField(TaxConditionField field) {

        TaxConfigurationResponseDTO.ConditionFieldOption option =
                new TaxConfigurationResponseDTO.ConditionFieldOption();

        option.setConditionFieldId(field.getConditionFieldId());
        option.setFieldCode(field.getFieldCode());
        option.setFieldLabel(field.getFieldLabel());
        option.setDataType(field.getDataType());
        option.setValueSource(field.getValueSource());

        List<String> operators = operatorRepository
                .findByConditionFieldId(field.getConditionFieldId())
                .stream()
                .map(TaxConditionFieldOperator::getOperator)
                .sorted()
                .toList();

        option.setOperators(operators);

        option.setAvailableValues(
                getAvailableValues(field)
        );

        return option;
    }

    private List<String> getAvailableValues(
            TaxConditionField field) {

        if (!"VEHICLE_DISTINCT_VALUES".equalsIgnoreCase(
                field.getValueSource())) {
            return List.of();
        }

        return switch (field.getFieldCode()) {

            case "fuelType" ->
                    vehicleRepository.findDistinctFuelTypes();

            case "transmissionType" ->
                    vehicleRepository.findDistinctTransmissionTypes();

            default -> List.of();
        };
    }

    private List<TaxConfigurationResponseDTO.CalculationBaseOption>
    getCalculationBaseOptions() {

        return calculationBaseRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapCalculationBase)
                .toList();
    }

    private TaxConfigurationResponseDTO.CalculationBaseOption
    mapCalculationBase(TaxCalculationBase base) {

        TaxConfigurationResponseDTO.CalculationBaseOption option =
                new TaxConfigurationResponseDTO.CalculationBaseOption();

        option.setCalculationBaseId(base.getCalculationBaseId());
        option.setBaseCode(base.getBaseCode());
        option.setBaseName(base.getBaseName());
        option.setDescription(base.getDescription());

        return option;
    }
}
