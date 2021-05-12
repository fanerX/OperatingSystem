package com.example.operatingsystem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import my_adapter.MemoryAdapter;
import my_adapter.ProcessAdapter;
import my_class.one.Memory;
import my_class.one.PCB;
import my_class.one.ProcessControl;
import my_view.MemoryView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ProcessControlActivity extends AppCompatActivity implements View.OnClickListener {

    private Button create_process,up_to_time,blocking_process,wake_up_process,ending_process;
    private RecyclerView ready_recycler_view,blocked_recycler_view,memory_recycler_view;
    private TextView running_name,running_memory;
    private MemoryView memoryView;

    private PCB running_pcb;

    private ProcessControl processControl;
    private List<PCB> ready_list;
    private List<PCB> blocked_list;
    private ProcessAdapter ready_adapter,blocked_adapter;
    private MemoryAdapter memoryAdapter;
    private List<Memory> memoryList;
    private Button memory_status;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_control);
        Intent intent = getIntent();
        String s_memory_max = intent.getStringExtra("s_memory_max");
        String s_start_address = intent.getStringExtra("s_start_address");
        String s_max_process_number = intent.getStringExtra("s_max_process_number");
        init();
        processControl = new ProcessControl(Integer.parseInt(s_start_address),Integer.parseInt(s_max_process_number),Integer.parseInt(s_memory_max));

        ready_list = new ArrayList<>();
        blocked_list = new ArrayList<>();
        memoryList = new ArrayList<>();
        ready_adapter=new ProcessAdapter(ready_list,R.drawable.shape_yellow);
        blocked_adapter=new ProcessAdapter(blocked_list,R.drawable.shape_red);
        memoryAdapter=new MemoryAdapter(memoryList);
        LinearLayoutManager layoutManager_ready = new LinearLayoutManager(this);
        LinearLayoutManager layoutManager_blocked = new LinearLayoutManager(this);
        LinearLayoutManager layoutManager_memory = new LinearLayoutManager(this);
        layoutManager_memory.setOrientation(LinearLayoutManager.HORIZONTAL);
        ready_recycler_view.setAdapter(ready_adapter);
        ready_recycler_view.setLayoutManager(layoutManager_ready);

        blocked_recycler_view.setAdapter(blocked_adapter);
        blocked_recycler_view.setLayoutManager(layoutManager_blocked);
        memory_recycler_view.setLayoutManager(layoutManager_memory);
        memory_recycler_view.setAdapter(memoryAdapter);
        memoryChange();
    }
    private void init(){
        create_process=findViewById(R.id.create_process);
        up_to_time=findViewById(R.id.up_to_time);
        blocking_process=findViewById(R.id.blocking_process);
        wake_up_process=findViewById(R.id.wake_up_process);
        ending_process=findViewById(R.id.ending_process);
        memory_status=findViewById(R.id.memory_status);

        ready_recycler_view=findViewById(R.id.ready_recycler_view);
        blocked_recycler_view=findViewById(R.id.blocked_recycler_view);
        memory_recycler_view=findViewById(R.id.memory_recycler_view);

        running_name=findViewById(R.id.pcb_name);
        running_memory=findViewById(R.id.pcb_memory);

        memoryView=findViewById(R.id.memory_view);

        running_pcb=null;
        setRunningData();

        create_process.setOnClickListener(this);
        up_to_time.setOnClickListener(this);
        blocking_process.setOnClickListener(this);
        wake_up_process.setOnClickListener(this);
        ending_process.setOnClickListener(this);
        memory_status.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.create_process:
                createProcess();
                break;
            case R.id.up_to_time:
                if(processControl.upToTime()){
                    //保证能轮转
                    ready_list.add(running_pcb);
                    running_pcb = processControl.getRunning();
                    ready_list.remove(running_pcb);
                    ready_adapter.notifyItemRemoved(0);
                    //ready_adapter.notifyItemInserted(ready_list.size()-1);
                    setRunningData();

                }else {
                    //时间片轮转失败
                    Toast.makeText(ProcessControlActivity.this, "时间片轮转失败",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.blocking_process:
                if(processControl.blockingProcess()){
                    blocked_list.add(running_pcb);
                    blocked_adapter.notifyItemInserted(blocked_list.size()-1);
                    running_pcb=processControl.getRunning();
                    if(running_pcb != null){
                        ready_list.remove(running_pcb);
                        ready_adapter.notifyItemRemoved(0);
                    }
                    setRunningData();
                }else {
                    //阻塞失败
                    Toast.makeText(ProcessControlActivity.this, "阻塞失败",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.wake_up_process:
                if(processControl.wakeUpProcess()){
                    PCB pcb = blocked_list.remove(0);
                    blocked_adapter.notifyItemRemoved(0);
                    if(running_pcb==processControl.getRunning()){
                        ready_list.add(pcb);
                        ready_adapter.notifyItemInserted(ready_list.size()-1);
                    }else {
                        running_pcb=processControl.getRunning();
                        setRunningData();
                    }
                }else {
                    //唤醒失败
                    Toast.makeText(ProcessControlActivity.this, "唤醒失败",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ending_process:
                if(processControl.deleteProcess()){
                    running_pcb = processControl.getRunning();
                    setRunningData();
                    if(ready_list.size()>=1){
                        ready_list.remove(running_pcb);
                        ready_adapter.notifyItemRemoved(0);
                    }
                    memoryChange();
                }else {
                    //结束失败
                    Toast.makeText(ProcessControlActivity.this, "终止失败",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.memory_status:
                if(memory_recycler_view.getVisibility()==View.VISIBLE){
                    memory_recycler_view.setVisibility(View.GONE);
                    memoryView.setVisibility(View.VISIBLE);
                }else {
                    memoryView.setVisibility(View.GONE);
                    memory_recycler_view.setVisibility(View.VISIBLE);
                    memoryAdapter.notifyDataSetChanged();
                }
                memoryChange();
                break;
        }
    }
    private void setRunningData(){
        if(running_pcb!=null){
            running_name.setText(running_pcb.getName());
            running_memory.setText(running_pcb.getMemory().toDataString());
        }else {
            running_name.setText("null");
            running_memory.setText("null");
        }
    }
    private void memoryChange(){
        if(memory_recycler_view.getVisibility()==View.VISIBLE){
            memoryList.clear();
            Memory memory = processControl.getMemory().getNext();
            while(memory!=null){
                memoryList.add(memory);
                memory=memory.getNext();
            }
            memoryAdapter.notifyDataSetChanged();
        }else {
            memoryView.setMemory(processControl.getMemory());
        }
    }
    private void createProcess() {
        /* @setView 装入自定义View ==> R.layout.dialog_customize
         * 由于dialog_customize.xml只放置了一个EditView，因此和图8一样
         * dialog_customize.xml可自定义更复杂的View
         */
        AlertDialog.Builder customizeDialog = new AlertDialog.Builder(ProcessControlActivity.this);
        final View dialogView = LayoutInflater.from(ProcessControlActivity.this).inflate(R.layout.input_process_data_layout,null);
        //customizeDialog.setTitle("创建新进程");
        customizeDialog.setView(dialogView);
        customizeDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 获取EditView中的输入内容
                        EditText edit_name = dialogView.findViewById(R.id.edit_name);
                        EditText edit_size = dialogView.findViewById(R.id.edit_size);

                        String name = edit_name.getText().toString();
                        String string_size = edit_size.getText().toString();
                        if (name.equals("")||string_size.equals("")){
                            return;
                        }
                        if(Integer.parseInt(string_size)>0&&processControl.createProcess(name,Integer.parseInt(string_size))){
                            PCB pcb = processControl.getReady().getTail();
                            if(pcb==null){
                                running_pcb=processControl.getRunning();
                                setRunningData();
                            }else {
                                ready_list.add(pcb);
                                ready_adapter.notifyItemInserted(ready_list.size()-1);
                            }
                            memoryChange();
                        }else {
                            //创建进程失败
                            Toast.makeText(ProcessControlActivity.this, "创建进程失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        customizeDialog.show();
    }


}
