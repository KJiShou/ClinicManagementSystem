package entity.pharmacyManagement;

import adt.DictionaryInterface;
import adt.HashedDictionary;

import java.util.UUID;

public class TubeNeeded{
    private UUID id;
    private String name;
    private int quantity;
    private BloodTube bloodTube;
    private LabTest labTest;
    private DictionaryInterface<Integer, BloodTube> bloodTubes = new HashedDictionary<>();

    public TubeNeeded(UUID id, String name, int quantity, BloodTube bloodTube, LabTest labTest, DictionaryInterface<Integer, BloodTube> bloodTubes) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.bloodTube = bloodTube;
        this.labTest = labTest;
        this.bloodTubes = bloodTubes;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BloodTube getBloodTube() {
        return bloodTube;
    }

    public void setBloodTube(BloodTube bloodTube) {
        this.bloodTube = bloodTube;
    }

    public LabTest getLabTest() {
        return labTest;
    }

    public void setLabTest(LabTest labTest) {
        this.labTest = labTest;
    }

    public DictionaryInterface<Integer, BloodTube> getBloodTubes() {
        return bloodTubes;
    }

    public void setBloodTubes(DictionaryInterface<Integer, BloodTube> bloodTubes) {
        this.bloodTubes = bloodTubes;
    }
}
