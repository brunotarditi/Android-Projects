package com.brunotarditi.compra_facil.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.brunotarditi.compra_facil.R;
import com.brunotarditi.compra_facil.models.Buy;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class BuyAdapter extends BaseAdapter {

    private Context context;
    private List<Buy> list;
    private int layout;

    public BuyAdapter() {
    }

    public BuyAdapter(Context context, List<Buy> list, int layout) {
        this.context = context;
        this.list = list;
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
            viewHolder.title = convertView.findViewById(R.id.txtViewBuyDescription);
            viewHolder.product = convertView.findViewById(R.id.txtViewBuyProduct);
            viewHolder.total = convertView.findViewById(R.id.txtViewBuyTotal);
            viewHolder.createdAt = convertView.findViewById(R.id.txtBuyDate);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Buy buy = list.get(position);
        viewHolder.title.setText(buy.getTitle());
        int numberOfNotes = buy.getProducts().size();
        String textForNotes = (numberOfNotes == 1) ? numberOfNotes + " Producto " : numberOfNotes + " Productos";
        viewHolder.product.setText(textForNotes);
        //Total
        DecimalFormat df = new DecimalFormat("#.00");
        String total = "$ " + df.format(buy.totalProducts());
        viewHolder.total.setText(total);

        //Formateamos la fecha
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String createdAt = dateFormat.format(buy.getCreatedAt());
        viewHolder.createdAt.setText(createdAt);

        return convertView;
    }

    public class ViewHolder{
        TextView title;
        TextView product;
        TextView total;
        TextView createdAt;
    }

}
