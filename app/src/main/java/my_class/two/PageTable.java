package my_class.two;

public class PageTable {
    private int size;               //页数
    private int[] memory;           //内存页表
    private int[] virtual_memory;   //虚拟内存页表（外存）
    private boolean[] flag;         //标志位，true表示在内存，false表示在外存
    private boolean[] modified;     //修改位，true表示被修改，false表示未被修改

    /**
     * 根据页数size初始化PageTable
     * */
    public PageTable(int size){
        this.size=size;
        this.memory=new int[size];
        this.virtual_memory=new int[size];
        this.flag=new boolean[size];
        this.modified=new boolean[size];
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getMemory(int page) {
        return memory[page];
    }

    public void setMemory(int page,int block) {
        this.memory[page] = block;
    }

    public int getVirtual_memory(int page) {
        return virtual_memory[page];
    }

    public void setVirtual_memory(int page,int block) {
        this.virtual_memory[page] = block;
    }

    public boolean getFlag(int page) {
        return flag[page];
    }

    public void setFlag(int page,boolean flag) {
        this.flag[page] = flag;
    }

    public boolean getModified(int page) {
        return modified[page];
    }

    public void setModified(int page,boolean modified) {
        this.modified[page] = modified;
    }
}
