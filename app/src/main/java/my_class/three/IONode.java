package my_class.three;


import java.util.ArrayList;
import java.util.List;

import my_class.two.PCB;

public class IONode {
    private String name;
    private IONode next;
    private IONode parent;//上级设备（null<--通道<--控制器<--设备）
    private PCB process;
    private List<PCB> waiting_list;

    private String type;//通道、控制器、**设备（输入设备、输出设备……）


    public IONode(String name,String type){
        this.name = name;
        this.next = null;
        this.parent = null;
        this.process = null;
        this.waiting_list = new ArrayList<>();

        this.type = type;
    }


    /**
     * 添加各种设备
     * node为新节点，parent为上级设备
     * */
    public boolean addNode(IONode node,IONode parent){
        if(node==null){
            return false;
        }
        IONode p = this;//带头结点的链表
        while (p.next != null){
            p = p.next;
        }
        p.next = node;
        node.parent = parent;
        return true;
    }

    /**
     * 删除设备node
     * */
    public boolean deleteNode(IONode node){
        if(node==null){
            return false;
        }
        IONode p = this;
        //找到node的直接前驱
        while (p.next!=null&&p.next!=node) {
            p = p.next;
        }
        if(p.next == null){
            //没有找到，表明node不存在
            return false;
        }else {
            //找到设备node的直接前驱
            //删除
            p.next = node.next;
            //java会自动回收
            node.next = null;
        }
        return true;
    }

    /**
     * 设备独立性——在查找设备时，通过类型查找
     * 获得一个空闲设备
     * */
    public IONode findIdleNodeByType(String type){
        IONode p = this.next;
        //找到一个空闲的节点
        while (p != null && (!p.type.equals(type) || p.process != null)){
            p = p.next;
        }
        return p;
    }

    /**
     * 设备独立性——在查找设备时，通过类型查找
     * 获得一个被占用的设备
     * */
    public IONode findNodeByType(String type){
        IONode p = this.next;
        while (p != null && (!p.type.equals(type) || p.process == null)){
            p = p.next;
        }
        if(p != null){
            return p;
        }else {
            return null;
        }
    }

    /**
     * 通过名字查找节点
     * */
    public IONode findNodeByName(String name){
        IONode p = this.next;
        while (p != null && !p.name.equals(name)){
            p = p.next;
        }
        return p;
    }

    /**
     * 根据parent获得,该parent的管理设备
     * */
    public List<IONode> getIONodeByParent(IONode parent){
        List<IONode> ioNodeList = new ArrayList<>();
        IONode node = this.getNext();
        while (node!=null){
            if (node.getParent() == parent){
                ioNodeList.add(node);
            }
            node = node.getNext();
        }
        return ioNodeList;
    }



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public IONode getNext() {
        return next;
    }

    public void setNext(IONode next) {
        this.next = next;
    }

    public IONode getParent() {
        return parent;
    }

    public void setParent(IONode parent) {
        this.parent = parent;
    }

    public PCB getProcess() {
        return process;
    }

    public void setProcess(PCB process) {
        this.process = process;
    }

    public List<PCB> getWaiting_list() {
        return waiting_list;
    }

    public void setWaiting_list(List<PCB> waiting_list) {
        this.waiting_list = waiting_list;
    }
}
