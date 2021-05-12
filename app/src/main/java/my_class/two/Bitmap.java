package my_class.two;

public class Bitmap {
    private char[] map;
    private int maxSize;//最大内存大小，单位为字节
    private int size;//当前剩余内存大小，单位为字节
    public static final double BLOCK_SIZE = 1024; //单位为字节

    /**
     * 构造函数，size的单位为字节
     * */
    public Bitmap(int maxSize){
        if(maxSize <=0){
            return;
        }
        this.maxSize=maxSize;
        this.size=maxSize;
        int n = (int) Math.ceil(size / BLOCK_SIZE / 8);//向上取整
        this.map= new char[n];
    }

    /**
     * 使用随机数初始化内存
     * */
    public void randomInitialization(){
        for(int i=0;i<this.map.length;i++){
            this.map[i] = (char) (Math.random() * 256);
        }
        //计算剩余大小
        int i,j,number;
        number=0;
        for(i=0;i<this.map.length;i++){
            for(j=7;j>=0;j--){
                number+=getBit(i,j);
            }
        }
        this.size -= number * BLOCK_SIZE;
    }

    /**
     * 获得第index个字节的，第bit_no个位的占用情况，1表示占用，0表示未被占用
     * */
    public int getBit(int index,int bit_no){
        bit_no %= 8;
        char mask = (char) (1<<bit_no);
        if((char)(this.map[index]&mask)>0){
            return 1;
        }else {
            return 0;
        }
    }

    /**
     * 分配一个页，分配成功返回该块号，否则返回-1
     * */
    public int getOnePage(){
        int i,j;
        for(i=0;i<this.map.length;i++){
            for(j=0;j<8;j++){
                if(getBit(i,j)==0){
                    this.setBit(i,j,1);
                    return i*8+j;
                }
            }
        }
        return -1;
    }

    /**
     * 设置第index个字节的，第bit_no个位的占用情况，1表示占用，0表示未被占用
     * */
    public void setBit(int index,int bit_no,int flag){
        bit_no %= 8;
        char mask = (char) (1<<bit_no);
        if(flag == 1){
            this.map[index] |= mask;
            this.size -= BLOCK_SIZE;
        }else {
            mask= (char) ~mask;
            this.map[index] &= mask;
            this.size += BLOCK_SIZE;
        }
    }

    /**
     * 以位示图的形式显示内存情况
     * */
    public void showStatus(){
        System.out.println("bitmap:");
        int i,j;
        for(i=0;i<this.map.length;i++){
            for(j=7;j>=0;j--){
                System.out.print(getBit(i,j) + " ");
            }
            System.out.println();
        }
    }

    public int getSize() {
        return size;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public int getMapLength() {
        return this.map.length;
    }
}
