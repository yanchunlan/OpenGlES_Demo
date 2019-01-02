package com.opengles.book.es2_0_test2.main;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.opengles.book.es2_0_test2.R;

import java.util.ArrayList;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuHolder> {
    private ArrayList<MenuBean> data;
    private ItemClickListener clickListener;

    public MenuAdapter(ArrayList<MenuBean> data, ItemClickListener clickListener) {
        this.data = data;
        this.clickListener = clickListener;
    }

    @Override
    public MenuHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MenuHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_button, parent, false));
    }

    @Override
    public void onBindViewHolder(MenuHolder holder, int position) {
        holder.setPosition(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MenuHolder extends RecyclerView.ViewHolder {

        private Button mBtn;

        MenuHolder(View itemView) {
            super(itemView);
            mBtn = (Button) itemView.findViewById(R.id.mBtn);
            if (clickListener != null) {
                mBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (int) v.getTag();
                        MenuBean bean = data.get(position);
                        clickListener.click(bean);
                    }
                });
            }
        }

        public void setPosition(int position) {
            MenuBean bean = data.get(position);
            mBtn.setText(bean.getName());
            mBtn.setTag(position);
        }
    }

   public interface ItemClickListener {
        void click(MenuBean bean);
    }

}


