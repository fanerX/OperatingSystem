package com.example.operatingsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import my_adapter.EquipmentsOfPCBAdapter;
import my_class.three.EquipmentItem;
import my_class.three.EquipmentManage;
import my_class.three.EquipmentsOfPCBItem;
import my_class.three.IONode;
import my_class.two.PCB;
import my_class.two.ProcessControl;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ProcessOccupationActivity extends AppCompatActivity {
    private EquipmentManage equipmentManage;
    private ProcessControl processControl;

    private RecyclerView process_occupation_recycler;
    private Button forced_release_button;

    private List<EquipmentsOfPCBItem> equipmentsOfPCBItems;
    private EquipmentsOfPCBAdapter equipmentsOfPCBAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_occupation);
        equipmentManage = ThreeProcessControlActivity.getEquipmentManage();
        processControl = ThreeProcessControlActivity.getProcessControl();

        process_occupation_recycler = findViewById(R.id.process_occupation_recycler);
        forced_release_button = findViewById(R.id.forced_release_button);
        init();
    }
    private void init(){
        equipmentsOfPCBItems = new ArrayList<>();
        initdate();
        equipmentsOfPCBAdapter = new EquipmentsOfPCBAdapter(equipmentsOfPCBItems);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        process_occupation_recycler.setLayoutManager(manager);
        process_occupation_recycler.setAdapter(equipmentsOfPCBAdapter);
        forced_release_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = equipmentsOfPCBAdapter.getChooseIndex();
                if(index==-1){
                    Toast.makeText(ProcessOccupationActivity.this,"请选择删除目标",Toast.LENGTH_SHORT).show();
                    return;
                }
                EquipmentsOfPCBItem item = equipmentsOfPCBItems.remove(index);
                equipmentManage.releaseEquipment(item.getPcb());
                equipmentsOfPCBAdapter.notifyItemRemoved(index);
                //暂时不添加释放引起调度的机制--？
//                //链表删除操作
//                PCB node = processControl.getBlocked();
//                PCB pcb = item.getPcb();
//                while (node!=null&&node.getNext()!=pcb){
//                    node = node.getNext();
//                }
//                if(node!=null){
//                    node.setNext(pcb.getNext());
//                    pcb.setNext(null);
//                    processControl.getReady().addPCB(pcb);
//                }
            }
        });
    }

    private void initdate(){
        PCB node = processControl.getBlocked().getNext();
        EquipmentItem equipmentItem;
        EquipmentsOfPCBItem equipmentsOfPCBItem;
        IONode[] ioNodes;
        while (node != null){
            if(equipmentManage.isContainsPCB(node)){
                ioNodes = equipmentManage.getPCBIONodes(node);
                equipmentItem = new EquipmentItem();
                if(ioNodes[0] != null){
                    equipmentItem.setEquipment(ioNodes[0].getName());
                }
                if(ioNodes[1] != null){
                    equipmentItem.setController(ioNodes[1].getName());
                }
                if(ioNodes[2] != null){
                    equipmentItem.setChannel(ioNodes[2].getName());
                }
                equipmentsOfPCBItem = new EquipmentsOfPCBItem();
                equipmentsOfPCBItem.setEquipmentItem(equipmentItem);
                equipmentsOfPCBItem.setPcb(node);
                equipmentsOfPCBItems.add(equipmentsOfPCBItem);
                Log.d("MyTest", "initdate: ------add");
            }
            Log.d("MyTest", "initdate: ------next");
            node = node.getNext();
        }
        Log.d("MyTest", "initdate: ------" + equipmentsOfPCBItems.size());
    }
}
