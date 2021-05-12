package my_adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.operatingsystem.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import my_class.two.PageTable;

public class PageTableAdapter extends RecyclerView.Adapter<PageTableAdapter.ViewHolder>{
    private PageTable pageTable;
    private int[] drawables = new int[]{
            R.drawable.side_page_table,R.drawable.side_page_table_green
    };

    static class ViewHolder extends RecyclerView.ViewHolder{
        View item;
        TextView page_number;
        TextView memory_block_number;
        TextView status_bit;
        TextView out_block_number;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item=itemView;
            page_number = itemView.findViewById(R.id.page_number);
            memory_block_number=itemView.findViewById(R.id.memory_block_number);
            status_bit=itemView.findViewById(R.id.status_bit);
            out_block_number=itemView.findViewById(R.id.out_block_number);
        }
    }

    public PageTableAdapter(PageTable pageTable){
        this.pageTable=pageTable;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.page_table_item_layout,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String s = position+"";
        holder.page_number.setText(s);
        s = pageTable.getMemory(position)+"";
        holder.memory_block_number.setText(s);
        s = pageTable.getVirtual_memory(position)+"";
        holder.out_block_number.setText(s);
        if(pageTable.getFlag(position)){
            holder.status_bit.setText("1");
            holder.item.setBackgroundResource(drawables[1]);
            holder.out_block_number.setText("-");
        }else {
            holder.status_bit.setText("0");
            holder.item.setBackgroundResource(drawables[0]);
            holder.memory_block_number.setText("-");
        }
    }

    @Override
    public int getItemCount() {
        return pageTable.getSize();
    }

}
