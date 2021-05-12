package my_class.one;

public class Memory {
    //status:内存的状态 true-占用、false-空闲
    private boolean status;
    //start:内存起始位置
    private int start;
    //内存段长度
    private int length;

    //用于生成链表
    private Memory next;
    private Memory previous;

    public Memory(){
        status=false;
        start=-1;
        length=0;
    }
    /**
     * 必须先开辟内存空间
     * */
    public void createMemory(){
        Memory memory = new Memory();
        this.next=memory;
        //让头结点不能合并
        this.status=true;
        memory.previous=this;
        memory.setLength(this.length-this.start);
        memory.setStart(this.start);
        memory.setStatus(false);
    }

    /**
     * 获得大小为length的内存块
     * 若内存不足则返回null
     * */
    public Memory getMemory(int length){
        Memory memory = this.next;
        Memory new_memory = null;
        while (memory != null){
            if(!memory.status&&memory.length >= length){
                if(memory.length == length){
                    //内存大小正好，则返回该内存块
                    memory.setStatus(true);
                    new_memory = memory;
                }else {
                    ///内存大，则新分出内存块
                    new_memory = new Memory();
                    new_memory.setLength(length);
                    new_memory.setStart(memory.start);
                    new_memory.setStatus(true);

                    memory.setLength(memory.length - length);
                    memory.setStart(new_memory.start + length);

                    if(memory.previous!=null){
                        memory.previous.next = new_memory;
                    }
                    new_memory.previous = memory.previous;
                    memory.previous = new_memory;
                    new_memory.next = memory;
                }
                break;
            }
            memory = memory.next;
        }
        return new_memory;
    }

    public void reclaimMemory(Memory recycle){
        Memory temp = null;
        //设置成未被占用
        recycle.setStatus(false);
        //判断是否能合并previous
        if(recycle.previous!=null && !recycle.previous.status){
            temp = recycle.previous;
            temp.setLength(temp.length + recycle.length);
            if(recycle.next!=null){
                recycle.next.previous = temp;
            }
            temp.next = recycle.next;
            recycle.previous=null;
            recycle.next = null;
            recycle = temp;
        }
        //判断是否能合并next
        if(recycle.next!=null && !recycle.next.status){
            temp = recycle.next;
            recycle.setLength(recycle.length + temp.length);
            if(temp.next != null){
                temp.next.previous = recycle;
            }
            recycle.next = temp.next;
            temp.next =null;
            temp.previous = null;
        }
    }


    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }


    public Memory getNext() {
        return next;
    }

    public void setNext(Memory next) {
        this.next = next;
    }

    public Memory getPrevious() {
        return previous;
    }

    public void setPrevious(Memory previous) {
        this.previous = previous;
    }


    public void showMemory(){
        Memory memory=this.next;
        while (memory!=null){
            System.out.println(memory);
            memory=memory.next;
        }
    }

    @Override
    public boolean equals(Object obj) {
        Memory m=null;
        if(obj instanceof Memory){
            m = (Memory)obj;
            if(m.getStatus()==this.status&&m.getStart()==this.start&&m.getLength()==this.length){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "memory: start-" + this.start + "  size:" + length + "  status:" + this.status;
    }

    public String toDataString(){
        return "start=" + this.start + "    size:" + length;
    }

}
