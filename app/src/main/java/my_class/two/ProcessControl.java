package my_class.two;

import java.util.List;

public class ProcessControl {
    private Bitmap memory;//内存
    private Bitmap virtual_memory;//虚拟内存（外存，置换空间）,虚拟内存为内存大小的2倍
    private PCB ready;//就绪队列
    private PCB blocked;//阻塞队列
    private PCB running;//正在运行的最多有一个
    private int maxMemory;//最大内存，单位为字节
    private int maxSize;//最大进程个数
    private int size;//当前进程个数
    public static final int NUMBER_OF_PAGE = 3;//每个进程固定装入页数，也是进程最大可用内存块数
    private int replacement_algorithm;
    public static final int FIFO = 0;
    public static final int LRU = 1;

    private List<Integer> stack_running;

    /**
     * 根据最大进程数maxSize，最大内存（字节）初始化进程控制
     * */
    public ProcessControl(int maxSize,int maxMemory,int replacement_algorithm){
        ready = new PCB();
        blocked = new PCB();
        running = null;
        this.maxMemory = maxMemory;
        this.maxSize=maxSize;
        this.size=0;
        this.memory = new Bitmap(maxMemory);
        this.virtual_memory = new Bitmap(maxMemory * 2);
        if(replacement_algorithm==0||replacement_algorithm==1){
            this.replacement_algorithm=replacement_algorithm;
        }
    }

    /**
     * 获得置换算法
     * */
    public int getReplacement_algorithm() {
        return replacement_algorithm;
    }

    /**
     * 内存和虚拟内存随机初始化
     * */
    public void randomInitialization(){
        this.memory.randomInitialization();
        this.virtual_memory.randomInitialization();
    }



    /**
     * 根据当前正在运行的进程的逻辑地址，转换成物理地址
     * 若没有正在运行的进程，则返回-1
     * */
    public int[] addressTranslation(int logical_address){
        if(running==null){
            return null;
        }
        int[] results = null;
        PageTable pageTable = running.getPage_table();
        stack_running = running.getStack();
        int page = (int) (logical_address / Bitmap.BLOCK_SIZE);
        int offset = (int) (logical_address % Bitmap.BLOCK_SIZE);

        int block;
        System.out.println("逻辑地址:" + logical_address + "对应页号为:" + page + ",页内偏移地址为:" + offset);
        if( pageTable.getFlag(page)){
            //访问的页在内存中
            System.out.println(page + "号页在内存中");
            block=pageTable.getMemory(page);
            if(replacement_algorithm==LRU){
                stack_running.remove(page);
                stack_running.add(0,page);
            }
            results = new int[4];
            results[0] = page;//页号
            results[1] = offset;//页内偏移
            results[2] = block;//物理内存块
            results[3] = (int) (block * Bitmap.BLOCK_SIZE) + offset;//物理地址
        }else {
            //访问的页不在内存中，则选择一个页进行置换
            block=pageTable.getVirtual_memory(page);
            System.out.println(page + "号页不在内存，外存块号为" + block + "需要置换……");
            int fifo_page;
            int fifo_block;
            if(replacement_algorithm == FIFO){
                fifo_page = stack_running.remove(0);
            }else {
                fifo_page = stack_running.remove(stack_running.size()-1);
            }
            fifo_block = pageTable.getMemory(fifo_page);
            if(replacement_algorithm == FIFO){
                System.out.println("利用FIFO算法选中内存" + fifo_page + "号页，该页内存块号为:" + fifo_block + ",修改位为:" + pageTable.getModified(fifo_page)); // + ",外存块号为:" + fifo_block
            }else {
                System.out.println("利用LRU算法选中内存" + fifo_page + "号页，该页内存块号为:" + fifo_block + ",修改位为:" + pageTable.getModified(fifo_page)); // + ",外存块号为:" + fifo_block
            }
            int temp = this.virtual_memory.getOnePage();
            if(temp != -1){
                System.out.println("将内存" + fifo_block + "写入 外存" + temp + ",成功");
            }else {
                //虚拟内存不足
                System.out.println("将内存" + fifo_block + "写入 外存" + temp + ",失败");
                return null;
            }
            pageTable.setFlag(fifo_page,false);
            pageTable.setVirtual_memory(fifo_page,temp);
            System.out.println("将外存" + block + "号块内容调入内存" + fifo_block +"号块中,置换完毕");
            this.virtual_memory.setBit(block/8,block%8,0);//回收虚拟内存
            pageTable.setFlag(page,true);
            pageTable.setMemory(page,fifo_block);
            block = fifo_block;
            stack_running.add(page);

            running.setPage_missing(running.getPage_missing()+1);


            results = new int[7];
            results[0] = page;//页号
            results[1] = offset;//页内偏移
            results[2] = pageTable.getVirtual_memory(page);//外存物理块
            results[3] = fifo_page;//置换页号
            results[4] = fifo_block;//置换内存块号
            results[5] = temp;//置换到外存块号
            results[6] = (int) (block * Bitmap.BLOCK_SIZE) + offset;//物理地址
        }
        running.setVisits(running.getVisits()+1);
        System.out.println("逻辑地址" + logical_address + "对应物理地址" + ((int) (block * Bitmap.BLOCK_SIZE) + offset));
        return results;
    }


    /**
     * 根据进程名，大小（字节）来创建新进程
     *
     * */
    public boolean createProcess(String name,int size){
        System.out.println("---------创建新进程---------");
        if(this.size > maxSize){
            return false;
        }

        PCB pcb = new PCB(name, size);
        PageTable pageTable = pcb.getPage_table();
        stack_running = pcb.getStack();
        //加载到内存
        int block,i;
        for(i=0;i<NUMBER_OF_PAGE&&i<pageTable.getSize();i++){
            block=this.memory.getOnePage();
            if(block!=-1){
                pageTable.setMemory(i,block);
                pageTable.setFlag(i,true);
                stack_running.add(i);
            }else {
                //内存不足
                //因为采取的固定分配，局部置换所以，没有办法运行给进程
                return false;
            }
        }
        for(;i<pageTable.getSize();i++){
            block=this.virtual_memory.getOnePage();
            if(block!=-1){
                pageTable.setVirtual_memory(i,block);
                pageTable.setFlag(i,false);
            }else {
                //内存不足
                //因为采取的固定分配，局部置换所以，没有办法运行给进程
                return false;
            }
        }

        //若没有正在运行的进程，则直接加入执行队列，否则加入就绪队列
        if(running == null){
            running = pcb;
        }else {
            ready.addPCB(pcb);
        }
        this.size += 1;
        return true;
    }

    /**
     * 撤销当前正在运行的进程
     * */
    public boolean deleteProcess(){
        System.out.println("---------撤销进程---------");
        //没有正在运行的进程，撤销失败
        if(running==null){
            return false;
        }
        //有正在运行的进程
        //回收内存、虚拟内存
        PageTable pageTable = running.getPage_table();
        int block;
        for(int i=0;i<pageTable.getSize();i++){
            if(pageTable.getFlag(i)){
                block=pageTable.getMemory(i);
                this.memory.setBit(block/8,block%8,0);
            }else {
                block=pageTable.getVirtual_memory(i);
                this.virtual_memory.setBit(block/8,block%8,0);
            }
        }
        //计算缺页率
        if(replacement_algorithm == FIFO){
            System.out.println("使用FIFO算法,缺页率为:" + (1.0 * running.getPage_missing()/ running.getVisits()));
        }else {
            System.out.println("使用LRU算法,缺页率为:" + (1.0 * running.getPage_missing()/ running.getVisits()));
        }
        //删除PCB(java会自动回收)
        running=ready.removePCB();
        this.size -= 1;
        //如果
        return true;
    }


    /**
     * 时间片轮转
     * */
    public boolean upToTime(){
        System.out.println("---------时间片轮转---------");
        //没有正在运行的进程，则撤销失败
        if(running==null){
            return false;
        }
        ready.addPCB(running);
        running = ready.removePCB();
        //因为不会出现就绪的队列为空的情况所以不用判断
        return true;
    }

    /**
     * 阻塞正在运行的进程，到阻塞队列队尾
     * */
    public boolean blockingProcess(){
        System.out.println("---------阻塞进程---------");
        //没有正在运行的进程，则阻塞失败
        if(running==null){
            return false;
        }
        blocked.addPCB(running);
        //running可能会出现null的情况
        running = ready.removePCB();
        return true;
    }


    /**
     * 唤醒阻塞队列的对头，到就绪队列的队尾
     * */
    public boolean wakeUpProcess(){
        System.out.println("---------唤醒进程---------");
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

//    /**
//     * 获得唤醒阻塞队列的对头，到就绪队列的队尾
//     * */
//    public PCB getWakeUpProcess(){
//        System.out.println("---------唤醒进程---------");
//        //没有阻塞的进程，则唤醒失败
//        PCB pcb=blocked.removePCB();
//        if(pcb==null){
//            return null;
//        }
//        if(running==null){
//            running=pcb;
//        }else {
//            ready.addPCB(pcb);
//        }
//        return pcb;
//    }

    /**
     * 输出当前所有进程的状态和内存情况
     * */
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
        this.memory.showStatus();
        System.out.println("虚拟内存情况:");
        this.virtual_memory.showStatus();
    }

    public int getMaxSize() {
        return maxSize;
    }

    public int getSize() {
        return size;
    }

    public int getMaxMemory() {
        return maxMemory;
    }

    public Bitmap getMemory() {
        return memory;
    }

    public Bitmap getVirtual_memory() {
        return virtual_memory;
    }

    public PCB getBlocked() {
        return blocked;
    }

    public PCB getReady() {
        return ready;
    }

    public PCB getRunning() {
        return running;
    }
}
