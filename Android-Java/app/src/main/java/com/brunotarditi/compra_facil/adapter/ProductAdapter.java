package com.brunotarditi.compra_facil.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.brunotarditi.compra_facil.R;
import com.brunotarditi.compra_facil.models.Product;

import java.text.DecimalFormat;
import java.util.List;

public class ProductAdapter extends BaseAdapter {

    private Context context;
    private List<Product> list;
    private int layout;

    public ProductAdapter(Context context, List<Product> products, int layout) {
        this.context = context;
        this.list = products;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ViewHolder viewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(this.layout, null);
            viewHolder = new ViewHolder();
            viewHolder.description = convertView.findViewById(R.id.txtViewProductDescription);
            viewHolder.total = convertView.findViewById(R.id.txtViewProductTotal);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Product product = list.get(position);
        viewHolder.description.setText(product.getName());

        //Formato del total
        DecimalFormat df = new DecimalFormat("#.00");
        String total = product.getQuantity()  + " x " + product.getPrice()  + " = " + df.format(product.total());
        viewHolder.total.setText(total);

        return convertView;
    }

    public class ViewHolder{
        TextView description;
        TextView total;
    }
}
