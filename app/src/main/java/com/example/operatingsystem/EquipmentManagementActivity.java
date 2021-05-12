package com.example.operatingsystem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import my_adapter.EquipmentAdapter;
import my_adapter.EquipmentToPCBAdapter;
import my_class.three.EquipmentItem;
import my_class.three.EquipmentManage;
import my_class.three.IONode;
import my_class.two.PCB;
import my_interface.IChooseItem;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class EquipmentManagementActivity extends AppCompatActivity implements View.OnClickListener{
    private RecyclerView equipments_recycler_view,pcb_recycler_view;
    private Button create_channel,create_controller,create_equipment,delete_equipment_choose;

    private static EquipmentManage equipmentManage;

    private EquipmentAdapter equipmentAdapter;
    private List<EquipmentItem> equipmentItems;

    private EquipmentToPCBAdapter toPCBAdapter;
    private List<PCB> pcbList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_management);
        //list
        equipments_recycler_view = findViewById(R.id.equipments_recycler_view);
        pcb_recycler_view = findViewById(R.id.pcb_recycler_view);
        //button
        create_channel = findViewById(R.id.create_channel);
        create_controller = findViewById(R.id.create_controller);
        create_equipment = findViewById(R.id.create_equipment);
        delete_equipment_choose = findViewById(R.id.delete_equipment_choose);
        //EquipmentManage
        equipmentManage = ThreeProcessControlActivity.getEquipmentManage();
        init();
    }
    private void init(){
        pcbList = new ArrayList<>();
        toPCBAdapter = new EquipmentToPCBAdapter(pcbList);
        pcb_recycler_view.setAdapter(toPCBAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        pcb_recycler_view.setLayoutManager(layoutManager);


        equipmentItems = new ArrayList<>();
        updateEquipmentItems();
        equipmentAdapter = new EquipmentAdapter(equipmentItems, new IChooseItem() {
            @Override
            public void chooseItem(String chooseItem) {
                IONode item = equipmentManage.getCHCTs().findNodeByName(chooseItem);
                if(item == null){
                    item = equipmentManage.getCOCTs().findNodeByName(chooseItem);
                }
                if(item == null){
                    item = equipmentManage.getDCTs().findNodeByName(chooseItem);
                }
                if(item == null){
                    return;
                }
                //更新该设备被占用情况
                pcbList.clear();
                if(item.getProcess()!= null){
                    pcbList.add(item.getProcess());
                    //删除强制释放的进程
                    List<PCB> waiting_list = item.getWaiting_list();
                    for(int i=0;i<waiting_list.size();i++){
                        if(!equipmentManage.isContainsPCB(waiting_list.get(i))){
                            waiting_list.remove(i);
                            i--;
                        }
                    }
                    if(waiting_list.size()>0){
                        pcbList.addAll(item.getWaiting_list());
                    }
                }
                toPCBAdapter.notifyDataSetChanged();
            }
        });
        equipments_recycler_view.setAdapter(equipmentAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        equipments_recycler_view.setLayoutManager(manager);

        create_channel.setOnClickListener(this);
        create_controller.setOnClickListener(this);
        create_equipment.setOnClickListener(this);
        delete_equipment_choose.setOnClickListener(this);
    }

    private void updateEquipmentItems(){
        equipmentItems.clear();
        boolean flag_controller = true;
        boolean flag_channel = true;
        EquipmentItem item;
        List<IONode> conrollerList;
        List<IONode> eqiupmentList;
        for (IONode channel:equipmentManage.getCHCTs().getIONodeByParent(null)){
            conrollerList = equipmentManage.getCOCTs().getIONodeByParent(channel);
            if(conrollerList.size()>0){
                for(IONode controller:conrollerList){
                    eqiupmentList = equipmentManage.getDCTs().getIONodeByParent(controller);
                    if(eqiupmentList.size()>0){
                        for(IONode equip:eqiupmentList){
                            item = new EquipmentItem();
                            item.setEquipment(equip.getName());
                            if(flag_controller){
                                item.setController(controller.getName());
                                flag_controller = false;
                            }
                            if(flag_channel){
                                item.setChannel(channel.getName());
                                flag_channel = false;
                            }
                            equipmentItems.add(item);
                        }
                    }else {
                        //单个控制器
                        item = new EquipmentItem();
                        item.setController(controller.getName());
                        if(flag_channel){
                            item.setChannel(channel.getName());
                            flag_channel = false;
                        }
                        equipmentItems.add(item);
                    }
                    flag_controller = true;
                }
            }else {
                //单个通道
                item = new EquipmentItem();
                item.setChannel(channel.getName());
                equipmentItems.add(item);
            }
            flag_channel = true;
        }
        if(equipmentAdapter != null){
            equipmentAdapter.notifyDataSetChanged();
        }
        Log.d("MyTest", "updateEquipmentItems: -----------" + equipmentItems.size());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.create_channel:
                //创建新通道
                createXXX("channel");
                break;
            case R.id.create_controller:
                createXXX("controller");
                break;
            case R.id.create_equipment:
                createXXX("equipment");
                break;
            case R.id.delete_equipment_choose:
                String chooseItem = equipmentAdapter.getChooseItemText();
                if(chooseItem.equals("")){
                    Toast.makeText(EquipmentManagementActivity.this,"请选择要删除的设备",Toast.LENGTH_SHORT).show();
                }else {
                    if(equipmentManage.deleteChannel(chooseItem)){
                        Toast.makeText(EquipmentManagementActivity.this,"删除通道成功",Toast.LENGTH_SHORT).show();
                        updateEquipmentItems();
                    }else if(equipmentManage.deleteController(chooseItem)){
                        Toast.makeText(EquipmentManagementActivity.this,"删除控制器成功",Toast.LENGTH_SHORT).show();
                        updateEquipmentItems();
                    }else if(equipmentManage.deleteEquipment(chooseItem)){
                        Toast.makeText(EquipmentManagementActivity.this,"删除设备成功",Toast.LENGTH_SHORT).show();
                        updateEquipmentItems();
                    }else {
                        Toast.makeText(EquipmentManagementActivity.this,"删除失败",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
        equipmentManage.showAll();
    }

    private void createXXX(final String type) {
        /* @setView 装入自定义View ==> R.layout.dialog_customize
         * 由于dialog_customize.xml只放置了一个EditView，因此和图8一样
         * dialog_customize.xml可自定义更复杂的View
         */
        AlertDialog.Builder customizeDialog = new AlertDialog.Builder(EquipmentManagementActivity.this);
        View dialogView = LayoutInflater.from(EquipmentManagementActivity.this).inflate(R.layout.input_equipment_layout,null);
        final TextView title_equipment = dialogView.findViewById(R.id.title_equipment);
        final EditText edit_equipment = dialogView.findViewById(R.id.edit_equipment);
        final EditText edit_equipment_type = dialogView.findViewById(R.id.edit_equipment_type);
        if(type.equals("channel")){
            title_equipment.setText("添加通道");
        }else if(type.equals("controller")){
            title_equipment.setText("添加控制器");
        }else {
            title_equipment.setText("添加设备");
            edit_equipment_type.setVisibility(View.VISIBLE);
        }
        customizeDialog.setView(dialogView);
        customizeDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = edit_equipment.getText().toString();
                        if(name.equals("")){
                            return;
                        }
                        if(type.equals("channel")){
                            if(equipmentManage.createChannel(name)){
                                Toast.makeText(EquipmentManagementActivity.this,"添加通道成功",Toast.LENGTH_SHORT).show();
                                updateEquipmentItems();
                            }else {
                                Toast.makeText(EquipmentManagementActivity.this,"添加通道失败",Toast.LENGTH_SHORT).show();
                            }
                        }else if(type.equals("controller")){
                            String channel =equipmentAdapter.getChooseItemText();
                            if(equipmentManage.getCHCTs().findNodeByName(channel) == null){
                                Toast.makeText(EquipmentManagementActivity.this,"请选中一个通道",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if(equipmentManage.createController(name,equipmentAdapter.getChooseItemText())){
                                Toast.makeText(EquipmentManagementActivity.this,"添加控制器成功",Toast.LENGTH_SHORT).show();
                                updateEquipmentItems();
                            }else {
                                Toast.makeText(EquipmentManagementActivity.this,"添加控制器失败",Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            String controller = equipmentAdapter.getChooseItemText();
                            if(equipmentManage.getCOCTs().findNodeByName(controller) == null){
                                Toast.makeText(EquipmentManagementActivity.this,"请选中一个控制器",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String type = edit_equipment_type.getText().toString();
                            if(type.equals("")){
                                Toast.makeText(EquipmentManagementActivity.this,"请输入设备类型",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if(equipmentManage.createEquipment(name,type,controller)){
                                Toast.makeText(EquipmentManagementActivity.this,"添加设备成功",Toast.LENGTH_SHORT).show();
                                updateEquipmentItems();
                            }else {
                                Toast.makeText(EquipmentManagementActivity.this,"添加设备失败",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
        customizeDialog.show();
    }

}
