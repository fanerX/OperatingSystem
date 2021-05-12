package my_class.three;



import java.util.HashSet;
import java.util.List;
import java.util.Set;

import my_class.two.PCB;

public class EquipmentManage {
    private IONode CHCTs;   //通道队列
    private IONode COCTs;   //控制器队列
    private IONode DCTs;    //设备队列
    private Set<String> name_set;   //设备名字管理、防止名字冲突（重复）
    private Set<PCB> pcbSet;    //防止进程重复


    public EquipmentManage(){
        this.CHCTs = new IONode("CHCTs","CHCTs");
        this.COCTs = new IONode("COCTs","COCTs");
        this.DCTs = new IONode("DCTs","DCTs");
        this.name_set = new HashSet<>();
        this.pcbSet = new HashSet<>();
        //初始化
        init();
    }

    /**
     * PCB设备占用情况
     * [设备、控制器、通道]
     */
    public IONode[] getPCBIONodes(PCB pcb){
        IONode[] ioNodes = new IONode[3];
        IONode node = this.DCTs.getNext();
        //找到PCB占用设备
        while (node != null && node.getProcess() != pcb){
            node = node.getNext();
        }
        int i = 0;
        while (node != null&&node.getProcess()==pcb){
            ioNodes[i++] = node;
            node = node.getParent();
        }
        return ioNodes;
    }

    public boolean isContainsPCB(PCB pcb){
        return this.pcbSet.contains(pcb);
    }

    /**
     * PCB根据类型申请分配设备,保证PCB != null
     * true表示操作正常（不一定占有设备），false表示没有该类型的任何设备
     * */
    public boolean applyForEquipment(PCB pcb,String type){
        if(pcb == null){
            return false;
        }
        if(this.pcbSet.contains(pcb)){
            //防止多次申请
            return false;
        }else {
            this.pcbSet.add(pcb);
        }
        //设备
        IONode equip = this.DCTs.findIdleNodeByType(type);
        if(equip != null){
            //如果有空闲的该类型设备,则把该设备分配给该进程
            equip.setProcess(pcb);
        }else {
            equip = this.DCTs.findNodeByType(type);
            if(equip != null){
                //如果有该类型的设备，则把该进程放入等待队列
                equip.getWaiting_list().add(pcb);
                return true;
            }else {
                //没有该类型的设备
                return false;
            }
        }

        //控制器
        IONode controller = equip.getParent();
        if(controller.getProcess() == null){
            //控制器空闲，则把该控制器分配给该进程
            controller.setProcess(pcb);
        }else {
            //控制器被占用，则把该进程放入等待队列
            controller.getWaiting_list().add(pcb);
            return true;
        }

        //通道
        IONode channel = controller.getParent();
        if(channel.getProcess() == null){
            //通道空闲，则把该通道分配给该进程
            channel.setProcess(pcb);
        }else {
            //通道被占用，则把该进程放入等待队列
            channel.getWaiting_list().add(pcb);
            return true;
        }
        return true;
    }


    /**
     * PCB请求释放设备
     */
    public boolean releaseEquipment(PCB pcb){
        IONode[] ioNodes = getPCBIONodes(pcb);
        if(ioNodes[0] == null){
            return false;
        }
        this.pcbSet.remove(pcb);
        PCB temp_pcb;
        for(int i=0;i<3;i++){
            if(ioNodes[i]!=null){
                //防止已经强制释放进程再分配
                List<PCB> pcbList =ioNodes[i].getWaiting_list();
                for(int j = 0;j<pcbList.size();j++){
                    temp_pcb = pcbList.get(j);
                    if(!this.pcbSet.contains(temp_pcb)){
                        pcbList.remove(temp_pcb);
                        j--;
                    }
                }
                if(ioNodes[i].getWaiting_list().size()>0){
                    //如果队列不为空，则队列中的第一个PCB占用该设备
                    temp_pcb = ioNodes[i].getWaiting_list().remove(0);
                    ioNodes[i].setProcess(temp_pcb);
                    //PCB申请占用上级设备(因为上级设备还未释放，则必定被占用)
                    if(ioNodes[i].getParent() != null){
                        ioNodes[i].getParent().getWaiting_list().add(temp_pcb);
                    }
                }else {
                    //队列空，则设备空闲
                    ioNodes[i].setProcess(null);

                    //----------------------------同类型设备分配-------------------------------
                    if(i==0){
                        //实现设备独立的同类型共享
                        IONode node = this.DCTs.findNodeByType(ioNodes[0].getType());
                        if(node != null && node.getWaiting_list().size()>0){
                            //如果有同类型的被占用设备，且等待队列不为空，则分担等待队列第一个
                            temp_pcb = node.getWaiting_list().remove(0);
                            ioNodes[0].setProcess(temp_pcb);
                            //PCB申请占用控制器(因为上级设备还未释放，则必定被占用)
                            ioNodes[0].getParent().getWaiting_list().add(temp_pcb);
                        }
                    }
                }
            }else {
                break;
            }
        }
        return true;
    }


    /**
     * 添加设备
     * 输入：设备名、设备类型、控制器名（保证控制器一定存在）
     * */
    public boolean createEquipment(String name,String type,String controller_name){
        //判断名字是否存在，存在则创建失败
        if(this.name_set.contains(name)){
            return false;
        }
        IONode controller = this.COCTs.findNodeByName(controller_name);
        if(controller == null){
            return false;
        }
        IONode node = new IONode(name,type);
        this.DCTs.addNode(node,controller);
        this.name_set.add(name);
        return true;
    }

    /**
     * 通过名字删除设备
     * 不能删除有进程占用的设备
     * */
    public boolean deleteEquipment(String name){
        //先通过名字找到，然后删除
        IONode node = this.DCTs.findNodeByName(name);
        if(node ==  null){
            //未找到设备
            return false;
        }
        if(node.getProcess()!=null){
            //不能删除有进程占用的设备
            return false;
        }
        this.DCTs.deleteNode(node);
        this.name_set.remove(name);
        return true;
    }

    /**
     * 通过名字创建通道
     * */
    public boolean createChannel(String name){
        if(this.name_set.contains(name)){
            return false;
        }
        IONode channel = new IONode(name,"channel");
        this.CHCTs.addNode(channel,null);
        this.name_set.add(name);
        return true;
    }

    /**
     * 通过名字删除通道
     * 如果还有其它通道，则把要删除通道的控制器转移到其它通道上
     * 否则（即没有任何通道），清空所有设备和控制器
     * 不能删除有进程占用的通道
     * */
    public boolean deleteChannel(String name){
        //先通过名字找到，然后删除
        IONode channel = this.CHCTs.findNodeByName(name);
        if(channel ==  null){
            //未找到设备
            return false;
        }
        if(channel.getProcess()!=null){
            //不能删除有进程占用的通道
            return false;
        }
        this.CHCTs.deleteNode(channel);
        if(this.CHCTs.getNext()!=null){
            //如果还有其它通道，则把要删除通道的控制器转移到其它通道上
            for(IONode node = this.COCTs.getNext();node!=null;node = node.getNext()){
                if(node.getParent()==channel){
                    node.setParent(this.CHCTs.findNodeByType("channel"));
                }
            }
            this.name_set.remove(name);
        }else {
            //否则（即没有任何通道），清空所有设备和控制器
            this.COCTs.setNext(null);//java自动回收
            this.DCTs.setNext(null);//java自动回收
            this.name_set.clear();
        }
        return true;
    }


    /**
     * 通过名字创建控制器
     * */
    public boolean createController(String name, String channel_name){
        if(this.name_set.contains(name)){
            return false;
        }
        IONode channel = this.CHCTs.findNodeByName(channel_name);
        if(channel == null){
            return false;
        }
        IONode controller = new IONode(name,"controller");
        this.COCTs.addNode(controller,channel);
        this.name_set.add(name);
        return true;
    }

    /**
     * 通过名字删除控制器
     * 如果还有其它控制器，则把要删除控制器的设备转移到其它控制器上
     * 否则（即没有任何控制器），清空所有设备
     * 不能删除有进程占用的控制器
     * */
    public boolean deleteController(String name){
        //先通过名字找到，然后删除
        IONode controller = this.COCTs.findNodeByName(name);
        if(controller ==  null){
            //未找到设备
            return false;
        }
        if(controller.getProcess()!=null){
            //不能删除有进程占用的控制器
            return false;
        }
        this.COCTs.deleteNode(controller);
        if(this.COCTs.getNext()!=null){
            //如果还有其它控制器，则把要删除控制器的设备转移到其它控制器上
            for(IONode node = this.DCTs.getNext();node!=null;node = node.getNext()){
                if(node.getParent()==controller){
                    node.setParent(this.COCTs.findNodeByType("controller"));
                }
            }
        }else {
            //否则（即没有任何控制器），清空所有设备
            for(IONode node = this.DCTs.getNext();node!=null;node = node.getNext()){
                this.name_set.remove(node.getName());//删除设备名字
            }
            this.DCTs.setNext(null);//java自动回收
        }
        this.name_set.remove(name);
        return true;
    }


    /**
     * 初始化设备
     * */
    private void init(){
        IONode channel1 = new IONode("channel1","channel");
        IONode channel2 = new IONode("channel2","channel");

        this.CHCTs.addNode(channel1,null);
        this.CHCTs.addNode(channel2,null);

        IONode controller1 = new IONode("controller1","controller");
        IONode controller2 = new IONode("controller2","controller");
        IONode controller3 = new IONode("controller3","controller");

        this.COCTs.addNode(controller1,channel1);
        this.COCTs.addNode(controller2,channel1);
        this.COCTs.addNode(controller3,channel2);

        IONode equip1 = new IONode("键盘1","键盘");
        IONode equip2 = new IONode("鼠标1","鼠标");
        IONode equip3 = new IONode("打印机1","打印机");
        IONode equip4 = new IONode("显示器1","显示器");

        this.DCTs.addNode(equip1,controller1);
        this.DCTs.addNode(equip2,controller1);
        this.DCTs.addNode(equip3,controller2);
        this.DCTs.addNode(equip4,controller3);

        this.name_set.add(channel1.getName());
        this.name_set.add(channel2.getName());
        this.name_set.add(controller1.getName());
        this.name_set.add(controller2.getName());
        this.name_set.add(controller3.getName());
        this.name_set.add(equip1.getName());
        this.name_set.add(equip2.getName());
        this.name_set.add(equip3.getName());
        this.name_set.add(equip4.getName());
    }

    public IONode getCHCTs() {
        return CHCTs;
    }

    public IONode getCOCTs() {
        return COCTs;
    }

    public IONode getDCTs() {
        return DCTs;
    }

    public void showAll(){
        System.out.println("----------------------begin---------------------------");
        System.out.println("通道：");
        IONode node = this.CHCTs.getNext();
        while (node!=null){
            System.out.println(node.getName());
            node = node.getNext();
        }
        System.out.println("控制器：");
        node = this.COCTs.getNext();
        while (node!=null){
            System.out.println(node.getName());
            node = node.getNext();
        }
        System.out.println("设备：");
        node = this.DCTs.getNext();
        while (node!=null){
            System.out.println(node.getName());
            node = node.getNext();
        }
        System.out.println("-----------------------end--------------------------");
    }
}
