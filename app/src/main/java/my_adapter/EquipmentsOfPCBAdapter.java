package my_adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.operatingsystem.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import my_class.three.EquipmentItem;
import my_class.three.EquipmentsOfPCBItem;
import my_class.two.PCB;

public class EquipmentsOfPCBAdapter extends RecyclerView.Adapter<EquipmentsOfPCBAdapter.ViewHolder> {
    //需要排序后的数据
    private List<EquipmentsOfPCBItem> equipmentsOfPCBItems;
    private int chooseIndex;
    private View choose_view;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        TextView channel_text;
        TextView controller_text;
        TextView equipment_text;
        View left_1;
        View left_2;
        TextView pcb_name;
        TextView pcb_memory;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            channel_text = itemView.findViewById(R.id.channel_text);
            controller_text = itemView.findViewById(R.id.controller_text);
            equipment_text = itemView.findViewById(R.id.equipment_text);
            left_1 = itemView.findViewById(R.id.left_1);
            left_2 = itemView.findViewById(R.id.left_2);
            pcb_name=itemView.findViewById(R.id.pcb_name);
            pcb_memory=itemView.findViewById(R.id.pcb_memory);
        }
    }

    public EquipmentsOfPCBAdapter(List<EquipmentsOfPCBItem> items){
        this.equipmentsOfPCBItems = items;
        this.chooseIndex = -1;
        this.choose_view = null;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.equipments_of_pcb_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(choose_view != null){
                    choose_view.setBackgroundResource(R.drawable.side_empty);
                }
                choose_view = holder.view;
                choose_view.setBackgroundResource(R.drawable.side_red);
                chooseIndex = holder.getAdapterPosition();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EquipmentsOfPCBItem equipmentsOfPCBItem = this.equipmentsOfPCBItems.get(position);
        EquipmentItem item = equipmentsOfPCBItem.getEquipmentItem();

        holder.channel_text.setText(item.getChannel());
        holder.controller_text.setText(item.getController());
        holder.equipment_text.setText(item.getEquipment());
        if(item.getChannel().equals("")){
            holder.left_1.setVisibility(View.INVISIBLE);
        }else {
            holder.left_1.setVisibility(View.VISIBLE);
        }
        if(item.getController().equals("")){
            holder.left_2.setVisibility(View.INVISIBLE);
        }else {
            holder.left_2.setVisibility(View.VISIBLE);
        }

        PCB pcb = equipmentsOfPCBItem.getPcb();
        holder.pcb_name.setText(pcb.getName());
        String s = "内存大小："+pcb.getSize();
        holder.pcb_memory.setText(s);
    }

    @Override
    public int getItemCount() {
        return this.equipmentsOfPCBItems.size();
    }

    public int getChooseIndex() {
        return chooseIndex;
    }
}
