package my_adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.operatingsystem.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import my_class.one.PCB;

public class ProcessAdapter extends RecyclerView.Adapter<ProcessAdapter.ViewHolder>{
    private List<PCB> pcbList;
    private int drawables;

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

    public ProcessAdapter(List<PCB> list,int drawables){
        this.pcbList=list;
        this.drawables=drawables;
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
        holder.item.setBackgroundResource(drawables);
        holder.pcb_name.setText(pcb.getName());
        holder.pcb_memory.setText(pcb.getMemory().toDataString());
    }

    @Override
    public int getItemCount() {
        return pcbList.size();
    }


}
