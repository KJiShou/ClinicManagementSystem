package entity.pharmacyManagement;

import java.util.UUID;

public class LabTest extends SalesItem {
    private String code;
    private boolean fastingRequired;
    private String patientPrecautions;
    private double requiredVolumeMl;

    LabTest(UUID id, String name, double price, String code, boolean fastingRequired, String patientPrecautions, double requiredVolumeMl) {
        super(id, name, price);
        this.code = code;
        this.fastingRequired = fastingRequired;
        this.patientPrecautions = patientPrecautions;
        this.requiredVolumeMl = requiredVolumeMl;
    }

}
