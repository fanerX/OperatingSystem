package my_class.three;

public class EquipmentItem {
    private String channel;
    private String controller;
    private String equipment;

    public EquipmentItem(){
        channel = "";
        controller = "";
        equipment = "";
    }

    public EquipmentItem(String channel,String controller,String equipment){
        this.channel = channel;
        this.controller = controller;
        this.equipment = equipment;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getController() {
        return controller;
    }

    public void setController(String controller) {
        this.controller = controller;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }
}
