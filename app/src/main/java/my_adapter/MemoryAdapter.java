package my_adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.operatingsystem.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import my_class.one.Memory;

public class MemoryAdapter extends RecyclerView.Adapter<MemoryAdapter.ViewHolder>{
    private List<Memory> memoryList;

    private int[] memory_colors = new int[]{
            R.drawable.shape_green,R.drawable.shape_red
    };

    static class ViewHolder extends RecyclerView.ViewHolder{
        View memoryView;
        TextView textView_start;
        TextView textView_length;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            memoryView=itemView;
            textView_start = itemView.findViewById(R.id.memory_start);
            textView_length=itemView.findViewById(R.id.memory_length);
        }
    }

    public MemoryAdapter(List<Memory> memoryList){
        this.memoryList= memoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.memory_item_layout,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Memory memory = memoryList.get(position);
        String string = "start:"+memory.getStart();
        holder.textView_start.setText(string);
        string="length:"+memory.getLength();
        holder.textView_length.setText(string);
        if(memory.getStatus()){
            holder.memoryView.setBackgroundResource(memory_colors[1]);
        }else {
            holder.memoryView.setBackgroundResource(memory_colors[0]);
        }
    }

    @Override
    public int getItemCount() {
        return memoryList.size();
    }

}

