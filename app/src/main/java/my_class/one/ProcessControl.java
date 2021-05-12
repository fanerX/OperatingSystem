package my_class.one;

import android.util.Log;

import java.util.HashSet;
import java.util.Set;


public class ProcessControl {
    private PCB ready;//就绪队列
    private PCB blocked;//阻塞队列
    private PCB running;//正在运行的最多有一个
    private Memory memory;
    private int maxSize;
    private int size;
    private Set<String> nameSet = new HashSet<>();

    public ProcessControl(int startAddress,int maxSize,int maxMemory){
        ready = new PCB();
        blocked = new PCB();
        running = null;
        memory = new Memory();
        this.maxSize = maxSize;
        memory.setLength(maxMemory);
        memory.setStart(startAddress);
        memory.createMemory();
        this.size = 0;
    }

    public boolean createProcess(String name,int size){
        //System.out.println("---------创建新进程---------");
        if(nameSet.contains(name)){
            return false;
        }else {
            nameSet.add(name);
        }
        if(this.size > maxSize){
            Log.d("MyTest", "createProcess: if(this.size > maxSize){");
            return false;
        }
        Memory memory_new = memory.getMemory(size);
        if(memory_new==null){
            Log.d("MyTest", "createProcess: if(memory_new==null){");
            return false;
        }
        PCB pcb = new PCB();
        pcb.setName(name);
        pcb.setMemory(memory_new);
        //若没有正在运行的进程，则直接加入执行队列，否则加入就绪队列
        if(running == null){
            running = pcb;
        }else {
            ready.addPCB(pcb);
        }
        this.size += 1;
        Log.d("MyTest", "createProcess: true");
        return true;
    }

    public boolean deleteProcess(){
        //System.out.println("---------撤销进程---------");
        //没有正在运行的进程，撤销失败
        if(running==null){
            return false;
        }
        //有正在运行的进程
        //回收内存
        memory.reclaimMemory(running.getMemory());
        //删除PCB(java会自动回收)
        nameSet.remove(running.getName());
        running=ready.removePCB();
        size -= 1;
        return true;
    }

    public boolean upToTime(){
        //System.out.println("---------时间片轮转---------");
        //没有正在运行的进程，则撤销失败
        if(running==null){
            return false;
        }
        ready.addPCB(running);
        running = ready.removePCB();
        //因为不会出现就绪的队列为空的情况所以不用判断
        return true;
    }

    public boolean blockingProcess(){
        //System.out.println("---------阻塞进程---------");
        //没有正在运行的进程，则阻塞失败
        if(running==null){
            return false;
        }
        blocked.addPCB(running);
        //running可能会出现null的情况
        running = ready.removePCB();
        return true;
    }

    public boolean wakeUpProcess(){
        //System.out.println("---------唤醒进程---------");
        //没有阻塞的进程，则唤醒失败
        PCB pcb=blocked.removePCB();
        if(pcb==null){
            return false;
        }
        if(running==null){
            running=pcb;
        }else {
            ready.addPCB(pcb);
        }
        return true;
    }

    public void show(){
        System.out.println("就绪队列:");
        PCB pcb=ready.getNext();
        while (pcb!=null){
            System.out.println(pcb);
            pcb=pcb.getNext();
        }
        System.out.println("阻塞队列：");
        pcb=blocked.getNext();
        while (pcb!=null){
            System.out.println(pcb);
            pcb=pcb.getNext();
        }
        System.out.println("\n正在运行:"+running);
        System.out.println("内存情况:");
        memory.showMemory();
    }

    public Memory getMemory() {
        return memory;
    }

    public PCB getReady() {
        return ready;
    }

    public PCB getRunning() {
        return running;
    }

    public PCB getBlocked() {
        return blocked;
    }

    public int getSize() {
        return size;
    }

    public int getMaxSize() {
        return maxSize;
    }
}
