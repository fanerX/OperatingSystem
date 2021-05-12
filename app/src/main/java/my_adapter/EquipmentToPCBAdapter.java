package my_adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.operatingsystem.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import my_class.two.PCB;

public class EquipmentToPCBAdapter extends RecyclerView.Adapter<EquipmentToPCBAdapter.ViewHolder>{
    private List<PCB> pcbList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View item;
        TextView pcb_name;
        TextView pcb_memory;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item=itemView;
            pcb_name=itemView.findViewById(R.id.pcb_name);
            pcb_memory=itemView.findViewById(R.id.pcb_memory);
        }
    }

    public EquipmentToPCBAdapter(List<PCB> list){
        this.pcbList=list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pcb_item_layout,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PCB pcb = pcbList.get(position);
        if(position==0){
            holder.item.setBackgroundResource(R.drawable.shape_green);
        }else {
            holder.item.setBackgroundResource(R.drawable.shape_yellow);
        }
        holder.pcb_name.setText(pcb.getName());
        String s = "内存大小："+pcb.getSize();
        holder.pcb_memory.setText(s);
    }

    @Override
    public int getItemCount() {
        return pcbList.size();
    }
}
