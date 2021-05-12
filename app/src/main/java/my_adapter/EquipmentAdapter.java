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
import my_interface.IChooseItem;

public class EquipmentAdapter extends RecyclerView.Adapter<EquipmentAdapter.ViewHolder>{
    //需要排序后的数据
    private List<EquipmentItem> equipmentItems;
    private TextView chooseItem;
    private IChooseItem iChooseItem;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        TextView channel_text;
        TextView controller_text;
        TextView equipment_text;
        View left_1;
        View left_2;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            channel_text = itemView.findViewById(R.id.channel_text);
            controller_text = itemView.findViewById(R.id.controller_text);
            equipment_text = itemView.findViewById(R.id.equipment_text);
            left_1 = itemView.findViewById(R.id.left_1);
            left_2 = itemView.findViewById(R.id.left_2);
        }
    }

    public EquipmentAdapter(List<EquipmentItem> items, IChooseItem iChooseItem){
        this.equipmentItems = items;
        this.chooseItem = null;
        this.iChooseItem = iChooseItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.equipment_item_layout,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        holder.channel_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = holder.channel_text.getText().toString();
                if(s.equals("")){
                    if(chooseItem!=null){
                        chooseItem.setBackgroundResource(R.drawable.side_empty);
                    }
                    chooseItem = null;
                }else {
                    if(chooseItem!=null){
                        chooseItem.setBackgroundResource(R.drawable.side_empty);
                    }
                    chooseItem = holder.channel_text;
                    chooseItem.setBackgroundResource(R.drawable.side_red);
                    iChooseItem.chooseItem(chooseItem.getText().toString());
                }
            }
        });
        holder.controller_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = holder.controller_text.getText().toString();
                if(s.equals("")){
                    if(chooseItem!=null){
                        chooseItem.setBackgroundResource(R.drawable.side_empty);
                    }
                    chooseItem = null;
                }else {
                    if(chooseItem!=null){
                        chooseItem.setBackgroundResource(R.drawable.side_empty);
                    }
                    chooseItem = holder.controller_text;
                    chooseItem.setBackgroundResource(R.drawable.side_red);
                    iChooseItem.chooseItem(chooseItem.getText().toString());
                }
            }
        });
        holder.equipment_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = holder.equipment_text.getText().toString();
                if(s.equals("")){
                    if(chooseItem!=null){
                        chooseItem.setBackgroundResource(R.drawable.side_empty);
                    }
                    chooseItem = null;
                }else {
                    if(chooseItem!=null){
                        chooseItem.setBackgroundResource(R.drawable.side_empty);
                    }
                    chooseItem = holder.equipment_text;
                    chooseItem.setBackgroundResource(R.drawable.side_red);
                    iChooseItem.chooseItem(chooseItem.getText().toString());
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EquipmentItem item = this.equipmentItems.get(position);
        holder.channel_text.setText(item.getChannel());
        holder.controller_text.setText(item.getController());
        holder.equipment_text.setText(item.getEquipment());
        if(item.getController().equals("")){
            holder.left_1.setVisibility(View.INVISIBLE);
        }else {
            holder.left_1.setVisibility(View.VISIBLE);
        }
        if(item.getEquipment().equals("")){
            holder.left_2.setVisibility(View.INVISIBLE);
        }else {
            holder.left_2.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return this.equipmentItems.size();
    }

    public String getChooseItemText(){
        if(chooseItem!=null){
            return chooseItem.getText().toString();
        }else {
            return "";
        }
    }

}
