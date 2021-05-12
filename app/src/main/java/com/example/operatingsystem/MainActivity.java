package com.example.operatingsystem;

import androidx.appcompat.app.AppCompatActivity;
import my_class.two.ProcessControl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText memory_max,max_process_number;
    private EditText start_address;
    private Button button;
    private RadioButton rb_fifo;
    private RadioGroup experiment,rg_1;
    private LinearLayout linear_start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        memory_max=findViewById(R.id.memory_max);
        start_address=findViewById(R.id.start_address);
        max_process_number=findViewById(R.id.max_process_number);
        rb_fifo=findViewById(R.id.rb_fifo);
        rg_1 = findViewById(R.id.rg_1);
        button=findViewById(R.id.button);
        experiment = findViewById(R.id.experiment);
        linear_start=findViewById(R.id.linear_start);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s_memory_max = memory_max.getText().toString();
                String s_start_address = start_address.getText().toString();
                String s_max_process_number = max_process_number.getText().toString();
                if(s_memory_max.equals("")||s_max_process_number.equals("")){
                    Toast.makeText(MainActivity.this,"不能为空",Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent;
                    switch (experiment.getCheckedRadioButtonId()){
                        case R.id.experiment_1:
                            intent = new Intent(MainActivity.this,ProcessControlActivity.class);
                            break;
                        case R.id.experiment_2:
                            intent = new Intent(MainActivity.this,TwoProcessControlActivity.class);
                            break;
                        default:
                            intent = new Intent(MainActivity.this,ThreeProcessControlActivity.class);
                    }
                    intent.putExtra("s_memory_max",s_memory_max);
                    intent.putExtra("s_start_address",s_start_address);
                    intent.putExtra("s_max_process_number",s_max_process_number);
                    if(rb_fifo.isChecked()){
                        intent.putExtra("algorithm", ProcessControl.FIFO);
                    }else {
                        intent.putExtra("algorithm",ProcessControl.LRU);
                    }
                    startActivity(intent);
                }
            }
        });
        experiment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.experiment_1){
                    linear_start.setVisibility(View.VISIBLE);
                    rg_1.setVisibility(View.GONE);
                }else {
                    linear_start.setVisibility(View.GONE);
                    rg_1.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
