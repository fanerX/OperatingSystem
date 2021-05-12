package com.example.operatingsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import my_adapter.PageTableAdapter;
import my_class.two.Bitmap;
import my_class.two.PageTable;
import my_class.two.ProcessControl;
import my_view.BitmapMemoryView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

public class BitmapMemoryActivity extends AppCompatActivity {

    private ProcessControl processControl;

    private Bitmap bitmap,virtual_bitmap;
    private BitmapMemoryView bitmapMemoryView,virtual_bitmap_view;
    private RecyclerView pagetable_recycer_view;
    private TextView algorithm_text_view,physical_address_text_view,page_number_and_offset,page_fault_rate;
    private TextView process_information_text;
    private EditText logic_address_edit;
    private Button button;

    private PageTable pageTable;
    private PageTableAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitmap_memery);
        //获得进程控制对象
        processControl = ThreeProcessControlActivity.getProcessControl();
        if(processControl==null){
            processControl = TwoProcessControlActivity.getProcessControl();
        }
        bitmap = processControl.getMemory();
        virtual_bitmap= processControl.getVirtual_memory();



        bitmap.showStatus();
        virtual_bitmap.showStatus();

        bitmapMemoryView = findViewById(R.id.bitmap_view);
        virtual_bitmap_view = findViewById(R.id.virtual_bitmap_view);
        bitmapMemoryView.setBitmap(bitmap);
        virtual_bitmap_view.setDoubleMode(true);
        virtual_bitmap_view.setBitmap(virtual_bitmap);

        algorithm_text_view=findViewById(R.id.algorithm_text_view);
        physical_address_text_view=findViewById(R.id.physical_address_text_view);
        page_number_and_offset=findViewById(R.id.page_number_and_offset);
        page_fault_rate=findViewById(R.id.page_fault_rate);

        logic_address_edit=findViewById(R.id.logic_address_edit);
        button=findViewById(R.id.button);

        process_information_text=findViewById(R.id.process_information_text);
        pagetable_recycer_view = findViewById(R.id.page_table_recycler_view);
        //不能没有正在运行的程序
        if(processControl.getRunning()!=null){
            pageTable = processControl.getRunning().getPage_table();
            adapter = new PageTableAdapter(pageTable);
            pagetable_recycer_view.setAdapter(adapter);

            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            pagetable_recycer_view.setLayoutManager(layoutManager);
            String s = "进程名：" + processControl.getRunning().getName() + "    进程大小：" + processControl.getRunning().getSize();
            process_information_text.setText(s);
        }else {
            process_information_text.setText("进程名：null");
        }
        init();
    }

    private void init(){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("-----------onClick--------------");
                String s = logic_address_edit.getText().toString();
                if(s.equals("")){
                    return;
                }
                if(processControl.getRunning()==null){
                    Toast.makeText(BitmapMemoryActivity.this,"没有正在运行的进程！",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(processControl.getReplacement_algorithm()==ProcessControl.FIFO){
                    algorithm_text_view.setText("FIFO算法");
                }else {
                    algorithm_text_view.setText("LRU算法");
                }
                int address = Integer.parseInt(s);
                if(address >= processControl.getRunning().getSize()){
                    Toast.makeText(BitmapMemoryActivity.this,"逻辑地址越界！",Toast.LENGTH_SHORT).show();
                    return;
                }
                int[] results = processControl.addressTranslation(address);
                if(results==null){
                    physical_address_text_view.setText("");
                    page_number_and_offset.setText("错误❌");
                }else if(results.length == 4){
                    s = "页号："+results[0]+"    页内偏移：" + results[1];
                    page_number_and_offset.setText(s);
                    s = "在内存中\n物理块号：" + results[2] + "\n物理地址为:" + results[3];
                    physical_address_text_view.setText(s);
                    s = "缺页率：" + (100.0 * processControl.getRunning().getPage_missing()/ processControl.getRunning().getVisits()) + "%";
                    page_fault_rate.setText(s);
                    bitmapMemoryView.update(new int[]{results[2]});
                }else if(results.length == 7){
                    s = "页号："+results[0]+"    页内偏移：" + results[1];
                    page_number_and_offset.setText(s);
                    s = "不在内存，置换：" + results[3] + "号页\n内存" + results[4] + "\t——>\t外存" + results[5] + "\n外存" + results[2]
                            + "\t——>\t内存" + results[4] + "\n物理地址为:" + results[6];
                    physical_address_text_view.setText(s);
                    s = "缺页率：" + (100.0 * processControl.getRunning().getPage_missing()/ processControl.getRunning().getVisits()) + "%";
                    page_fault_rate.setText(s);
                    adapter.notifyDataSetChanged();
                    bitmapMemoryView.update(new int[]{results[4]});
                    virtual_bitmap_view.update(new int[]{results[2],results[5]});
                }
            }
        });
    }
}
