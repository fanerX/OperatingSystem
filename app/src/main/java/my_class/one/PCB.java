package my_class.one;

public class PCB {
    private String name;
    private Memory memory;
    //有头结点的链表
    private PCB next;
    private PCB previous;

    public PCB(){
        name = null;
        memory = null;
        next = null;
        previous = null;
    }

    public void addPCB(PCB newPCB){
        PCB temp = this;
        while (temp.next != null){
            temp = temp.next;
        }
        temp.next = newPCB;
        newPCB.previous = temp;
    }

    //移除第一个PCB
    public PCB removePCB(){
        if(this.next==null){
            return null;
        }
        PCB temp = this.next;
        this.next = temp.next;
        if(temp.next!=null){
            temp.next.previous=this;
        }
        temp.next=null;
        temp.previous=null;
        return temp;
    }

    public void showPCB(){
        PCB temp = this.next;
        while (temp!=null){
            System.out.println(temp);
            temp = temp.next;
        }
    }

    public PCB getHead(){
        return this.next;
    }

    public PCB getTail(){
        PCB pcb = this.next;
        if(pcb==null){
            return null;
        }else {
            while (pcb.next!=null){
                pcb=pcb.next;
            }
            return pcb;
        }
    }

    @Override
    public String toString() {
        return "PCB——name:" + this.name + "  memory:" + this.memory;
    }

    @Override
    public boolean equals(Object obj) {
        PCB pcb = null;
        if(obj instanceof PCB){
            pcb = (PCB) obj;
            if(pcb.memory == this.memory && pcb.name == this.name){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Memory getMemory() {
        return memory;
    }

    public void setMemory(Memory memory) {
        this.memory = memory;
    }

    public PCB getNext() {
        return next;
    }

    public void setNext(PCB next) {
        this.next = next;
    }

    public PCB getPrevious() {
        return previous;
    }

    public void setPrevious(PCB previous) {
        this.previous = previous;
    }
}
