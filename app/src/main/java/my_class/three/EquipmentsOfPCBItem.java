package my_class.three;

import my_class.two.PCB;

public class EquipmentsOfPCBItem {
    private PCB pcb;
    private EquipmentItem equipmentItem;

    public PCB getPcb() {
        return pcb;
    }

    public void setPcb(PCB pcb) {
        this.pcb = pcb;
    }

    public EquipmentItem getEquipmentItem() {
        return equipmentItem;
    }

    public void setEquipmentItem(EquipmentItem equipmentItem) {
        this.equipmentItem = equipmentItem;
    }
}
