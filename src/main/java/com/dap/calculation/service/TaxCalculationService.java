package com.dap.calculation.service;

import com.dap.calculation.entity.City;
import com.dap.calculation.entity.Rto;
import com.dap.calculation.entity.State;
import com.dap.calculation.entity.TaxRule;
import com.dap.calculation.entity.TaxRuleApplicability;
import com.dap.calculation.entity.TaxRuleCondition;
import com.dap.calculation.entity.TaxType;
import com.dap.calculation.entity.Vehicle;
import com.dap.calculation.repository.CityRepository;
import com.dap.calculation.repository.TaxRuleApplicabilityRepository;
import com.dap.calculation.repository.TaxRuleConditionRepository;
import com.dap.calculation.repository.TaxRuleRepository;
import com.dap.calculation.repository.TaxTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
public class TaxCalculationService {

    private final TaxTypeRepository taxTypeRepository;
    private final TaxRuleRepository taxRuleRepository;
    private final TaxRuleConditionRepository taxRuleConditionRepository;
    private final TaxRuleApplicabilityRepository taxRuleApplicabilityRepository;
    private final CityRepository cityRepository;

    public TaxCalculationService(
            TaxTypeRepository taxTypeRepository,
            TaxRuleRepository taxRuleRepository,
            TaxRuleConditionRepository taxRuleConditionRepository,
            TaxRuleApplicabilityRepository taxRuleApplicabilityRepository,
            CityRepository cityRepository
    ) {
        this.taxTypeRepository = taxTypeRepository;
        this.taxRuleRepository = taxRuleRepository;
        this.taxRuleConditionRepository = taxRuleConditionRepository;
        this.taxRuleApplicabilityRepository = taxRuleApplicabilityRepository;
        this.cityRepository = cityRepository;
    }

    @Transactional(readOnly = true)
    public TaxCalculationResult calculateTaxes(
            Vehicle vehicle,
            Rto rto,
            State state
    ) {
        if (vehicle == null || rto == null || state == null) {
            throw new IllegalArgumentException(
                    "Vehicle, RTO and state are required for tax calculation."
            );
        }

        City city = cityRepository.findById(rto.getCityId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "City not found for RTO: " + rto.getRtoCode()
                ));

        Map<String, Object> attributes = buildAttributes(
                vehicle, rto, city, state
        );

        List<TaxRule> activeRules = taxRuleRepository.findByIsActiveTrue();

        Map<Long, TaxType> taxTypes = new HashMap<>();
        Map<Long, List<TaxRuleCondition>> conditionsByRule = new HashMap<>();
        Map<Long, List<TaxRuleApplicability>> applicabilityByRule =
                new HashMap<>();

        for (TaxRule rule : activeRules) {
            Long taxTypeId = rule.getTaxTypeId();

            if (!taxTypes.containsKey(taxTypeId)) {
                TaxType taxType = taxTypeRepository.findById(taxTypeId)
                        .orElseThrow(() -> new IllegalStateException(
                                "Tax type not found: " + taxTypeId
                        ));

                taxTypes.put(taxTypeId, taxType);
            }

            conditionsByRule.put(
                    rule.getTaxRuleId(),
                    taxRuleConditionRepository.findByTaxRuleId(
                            rule.getTaxRuleId()
                    )
            );

            applicabilityByRule.put(
                    rule.getTaxRuleId(),
                    taxRuleApplicabilityRepository.findByTaxRuleId(
                            rule.getTaxRuleId()
                    )
            );
        }

        Map<Long, TaxRule> selectedRules = new HashMap<>();
        Map<Long, Integer> selectedSpecificity = new HashMap<>();

        for (TaxRule rule : activeRules) {
            List<TaxRuleCondition> conditions =
                    conditionsByRule.get(rule.getTaxRuleId());

            // A rule without conditions is not applied implicitly.
            if (conditions == null || conditions.isEmpty()) {
                continue;
            }

            boolean conditionsMatch = conditions.stream()
                    .allMatch(condition ->
                            evaluateCondition(condition, attributes));

            if (!conditionsMatch) {
                continue;
            }

            List<TaxRuleApplicability> applicabilityRecords =
                    applicabilityByRule.get(rule.getTaxRuleId());

            int specificity = getSpecificity(
                    applicabilityRecords, rto, city, state
            );

            if (specificity < 0) {
                continue;
            }

            Long taxTypeId = rule.getTaxTypeId();

            Integer currentSpecificity =
                    selectedSpecificity.get(taxTypeId);

            if (currentSpecificity == null
                    || specificity > currentSpecificity) {

                selectedRules.put(taxTypeId, rule);
                selectedSpecificity.put(taxTypeId, specificity);

            } else if (specificity == currentSpecificity) {

                TaxRule previousRule = selectedRules.get(taxTypeId);

                throw new IllegalStateException(
                        "Ambiguous tax rules for tax type "
                                + taxTypes.get(taxTypeId).getTaxCode()
                                + ": "
                                + previousRule.getRuleName()
                                + " and "
                                + rule.getRuleName()
                );
            }
        }

        BigDecimal roadTax = BigDecimal.ZERO;
        BigDecimal tcs = BigDecimal.ZERO;
        BigDecimal cess = BigDecimal.ZERO;

        for (Map.Entry<Long, TaxRule> entry : selectedRules.entrySet()) {
            TaxType taxType = taxTypes.get(entry.getKey());
            TaxRule rule = entry.getValue();

            BigDecimal amount = vehicle.getExShowroomPrice()
                    .multiply(rule.getTaxPercentage())
                    .divide(
                            BigDecimal.valueOf(100),
                            2,
                            RoundingMode.HALF_UP
                    );

            String taxCode = taxType.getTaxCode()
                    .trim()
                    .toUpperCase(Locale.ROOT);

            switch (taxCode) {
                case "ROAD_TAX":
                    roadTax = amount;
                    break;

                case "TCS":
                    tcs = amount;
                    break;

                case "CESS":
                    cess = amount;
                    break;

                default:
                    // Other tax types can be calculated, but the current
                    // response DTO has no field for them yet.
                    break;
            }
        }

        return new TaxCalculationResult(roadTax, tcs, cess);
    }

    private Map<String, Object> buildAttributes(
            Vehicle vehicle,
            Rto rto,
            City city,
            State state
    ) {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put("vehicleId", vehicle.getVehicleId());
        attributes.put("modelName", vehicle.getModelName());
        attributes.put("variantName", vehicle.getVariantName());
        attributes.put("fuelType", vehicle.getFuelType());
        attributes.put("transmissionType", vehicle.getTransmissionType());
        attributes.put("exShowroomPrice", vehicle.getExShowroomPrice());
        attributes.put("lengthMm", vehicle.getLengthMm());
        attributes.put("groundClearanceMm",
                vehicle.getGroundClearanceMm());

        attributes.put("rtoCode", rto.getRtoCode());
        attributes.put("rtoName", rto.getRtoName());
        attributes.put("cityId", city.getCityId());
        attributes.put("cityName", city.getCityName());
        attributes.put("stateCode", state.getStateCode());
        attributes.put("stateName", state.getStateName());

        return attributes;
    }

    private int getSpecificity(
            List<TaxRuleApplicability> records,
            Rto rto,
            City city,
            State state
    ) {
        // No applicability records means the rule applies nowhere.
        if (records == null || records.isEmpty()) {
            return -1;
        }

        int bestSpecificity = -1;

        for (TaxRuleApplicability record : records) {
            String type = record.getApplicabilityType()
                    .trim()
                    .toUpperCase(Locale.ROOT);

            String code = record.getApplicabilityCode()
                    .trim();

            boolean matches = false;
            int specificity = -1;

            switch (type) {
                case "NATIONAL":
                    matches = code.equalsIgnoreCase("ALL")
                            || code.equalsIgnoreCase("INDIA");
                    specificity = 1;
                    break;

                case "STATE":
                    matches = code.equalsIgnoreCase(state.getStateCode());
                    specificity = 2;
                    break;

                case "CITY":
                    matches = code.equalsIgnoreCase(
                            String.valueOf(city.getCityId())
                    );
                    specificity = 3;
                    break;

                case "RTO":
                    matches = code.equalsIgnoreCase(rto.getRtoCode());
                    specificity = 4;
                    break;

                default:
                    throw new IllegalStateException(
                            "Unsupported applicability type: " + type
                    );
            }

            if (matches) {
                bestSpecificity = Math.max(
                        bestSpecificity, specificity
                );
            }
        }

        return bestSpecificity;
    }

    private boolean evaluateCondition(
            TaxRuleCondition condition,
            Map<String, Object> attributes
    ) {
        String fieldName = condition.getFieldName();
        Object actualValue = attributes.get(fieldName);

        if (actualValue == null) {
            throw new IllegalArgumentException(
                    "Unknown or unavailable tax condition field: "
                            + fieldName
            );
        }

        String operator = condition.getOperator()
                .trim()
                .toUpperCase(Locale.ROOT);

        String expectedFrom = condition.getValueFrom();
        String expectedTo = condition.getValueTo();

        switch (operator) {
            case "EQUALS":
                return equalsValue(actualValue, expectedFrom);

            case "NOT_EQUALS":
                return !equalsValue(actualValue, expectedFrom);

            case "GREATER_THAN":
                return compare(actualValue, expectedFrom) > 0;

            case "GREATER_THAN_OR_EQUAL":
                return compare(actualValue, expectedFrom) >= 0;

            case "LESS_THAN":
                return compare(actualValue, expectedFrom) < 0;

            case "LESS_THAN_OR_EQUAL":
                return compare(actualValue, expectedFrom) <= 0;

            case "BETWEEN":
                if (expectedTo == null) {
                    throw new IllegalArgumentException(
                            "BETWEEN requires value_to for field "
                                    + fieldName
                    );
                }

                return compare(actualValue, expectedFrom) >= 0
                        && compare(actualValue, expectedTo) <= 0;

            case "IN":
                return parseList(expectedFrom).stream()
                        .anyMatch(value ->
                                equalsValue(actualValue, value));

            default:
                throw new IllegalArgumentException(
                        "Unsupported tax condition operator: " + operator
                );
        }
    }

    private boolean equalsValue(Object actualValue, String expectedValue) {
        if (actualValue instanceof BigDecimal) {
            try {
                return ((BigDecimal) actualValue).compareTo(
                        new BigDecimal(expectedValue)
                ) == 0;
            } catch (NumberFormatException exception) {
                return false;
            }
        }

        return String.valueOf(actualValue)
                .equalsIgnoreCase(expectedValue.trim());
    }

    private int compare(Object actualValue, String expectedValue) {
        if (actualValue instanceof Number
                || actualValue instanceof BigDecimal) {
            return new BigDecimal(String.valueOf(actualValue))
                    .compareTo(new BigDecimal(expectedValue));
        }

        return String.valueOf(actualValue)
                .compareToIgnoreCase(expectedValue);
    }

    private List<String> parseList(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "IN operator requires a comma-separated value list."
            );
        }

        List<String> values = new ArrayList<>();

        for (String item : value.split(",")) {
            String trimmed = item.trim();

            if (!trimmed.isEmpty()) {
                values.add(trimmed);
            }
        }

        if (values.isEmpty()) {
            throw new IllegalArgumentException(
                    "IN operator requires at least one value."
            );
        }

        return values;
    }

    public static class TaxCalculationResult {

        private final BigDecimal roadTax;
        private final BigDecimal tcs;
        private final BigDecimal cess;

        public TaxCalculationResult(
                BigDecimal roadTax,
                BigDecimal tcs,
                BigDecimal cess
        ) {
            this.roadTax = roadTax;
            this.tcs = tcs;
            this.cess = cess;
        }

        public BigDecimal getRoadTax() {
            return roadTax;
        }

        public BigDecimal getTcs() {
            return tcs;
        }

        public BigDecimal getCess() {
            return cess;
        }
    }
}