package com.example.todoapp.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.AddNewTask;
import com.example.todoapp.MainActivity;
import com.example.todoapp.Model.ToDoModel;
import com.example.todoapp.R;
import com.example.todoapp.Utils.DataBaseHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.MyViewHolder> {

    private List<ToDoModel> mList;
    private MainActivity activity;
    private DataBaseHelper myDB;

    public ToDoAdapter(DataBaseHelper myDB , MainActivity activity,TextView deadlineTextView){
        this.activity = activity;
        this.myDB = myDB;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout , parent , false);
       return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ToDoModel item = mList.get(position);
        holder.mCheckBox.setText(item.getTask());
        holder.mCheckBox.setChecked(toBoolean(item.getStatus()));
        String deadline = item.getDeadline();
        if (isDeadlineExceeded(deadline)) {
            holder.mCheckBox.setTextColor(Color.RED);
        } else {
            holder.mCheckBox.setTextColor(Color.BLACK);
        }
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    myDB.updateStatus(item.getId(), 1);
                } else {
                    myDB.updateStatus(item.getId(), 0);
                }
            }
        });

        // Set the deadline text
        holder.mCheckBox.setText(item.getTask());
        holder.mDeadlineTextView.setText("DEADLINE: "+item.getDeadline());
    }
    private boolean isDeadlineExceeded(String deadline) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        try {
            Date deadlineDate = sdf.parse(deadline);
            Date currentDate = new Date();
            return currentDate.compareTo(deadlineDate) > 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean toBoolean(int num){
        return num!=0;
    }

    public Context getContext(){
        return activity;
    }

    public void setTasks(List<ToDoModel> mList){
        this.mList = mList;
        notifyDataSetChanged();
    }

    public void deletTask(int position){
        ToDoModel item = mList.get(position);
        myDB.deleteTask(item.getId());
        mList.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position){
        ToDoModel item = mList.get(position);

        Bundle bundle = new Bundle();
        bundle.putInt("id" , item.getId());
        bundle.putString("task" , item.getTask());
        bundle.putString("deadline", item.getDeadline());

        AddNewTask task = new AddNewTask();
        task.setArguments(bundle);
        task.show(activity.getSupportFragmentManager() , task.getTag());
        

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        CheckBox mCheckBox;
        TextView mDeadlineTextView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mCheckBox = itemView.findViewById(R.id.mcheckbox);
            mDeadlineTextView = itemView.findViewById(R.id.ddate);
        }
    }
}
