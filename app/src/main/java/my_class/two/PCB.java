package my_class.two;

import java.util.ArrayList;
import java.util.List;

public class PCB {
    private String name;//进程名
    private int size;//进程的大小，单位为字节
    private PageTable page_table;//该进程的页表
    //有头结点的链表
    private PCB next;
    private PCB previous;
    private List<Integer> stack;
    private int visits;
    private int page_missing;

    public PCB(){
        this.next = null;
        this.previous = null;
    }

    public PCB(String name, int size){
        this.name = name;
        this.size = size;
        this.page_table = new PageTable((int) Math.ceil(size/Bitmap.BLOCK_SIZE));
        previous = null;
        next = null;
        stack=new ArrayList<>();
        this.visits=0;
        this.page_missing=0;
    }


    public void setPage_missing(int page_missing) {
        this.page_missing = page_missing;
    }

    public void setVisits(int visits) {
        this.visits = visits;
    }

    public int getPage_missing() {
        return page_missing;
    }

    public int getVisits() {
        return visits;
    }

    public int getSize() {
        return size;
    }

    public List<Integer> getStack() {
        return stack;
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
        return "PCB——name:" + this.name + "  memory:";
    }

    @Override
    public boolean equals(Object obj) {
        PCB pcb = null;
        if(obj instanceof PCB){
            pcb = (PCB) obj;
            if(pcb.page_table == this.page_table && pcb.name == this.name){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

    public PageTable getPage_table() {
        return page_table;
    }

    public void setPage_table(PageTable page_table) {
        this.page_table = page_table;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
