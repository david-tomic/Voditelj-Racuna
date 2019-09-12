package davidtomic.projekti.diplomskiprojekt;

        import android.support.annotation.NonNull;
        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Filter;
        import android.widget.Filterable;
        import android.widget.ImageView;
        import android.widget.TextView;
        import com.bumptech.glide.Glide;
        import java.util.ArrayList;
        import java.util.List;

public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.ReceiptHolder> implements Filterable {

    private List<Receipt> receipts = new ArrayList<>();
    private List<Receipt> copyOfReceipts;
    private OnItemClickListener mListener;
    @NonNull
    @Override
    public ReceiptHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.receipt_item, viewGroup, false);
        return new ReceiptHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ReceiptHolder receiptHolder, int i) {
        final Receipt currentReceipt = receipts.get(i);
        receiptHolder.category.setText(currentReceipt.getCategory());
        receiptHolder.date.setText(currentReceipt.getDate());
        receiptHolder.total.setText(String.format("%.02f",currentReceipt.getTotal()) + " KM");
        Glide.with(receiptHolder.imageView.getContext()).load(currentReceipt.getIcon()).placeholder(R.drawable.ic_launcher_background).into(receiptHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return receipts.size();
    }

    public void setReceipts(List<Receipt> receipts){
        this.receipts = receipts;
        copyOfReceipts = new ArrayList<>(receipts);
        notifyDataSetChanged();
    }

    public Receipt getReceiptAt(int position){
        return receipts.get(position);
    }

    class ReceiptHolder extends RecyclerView.ViewHolder{
        private TextView category;
        private TextView date;
        private TextView total;
        private ImageView imageView;

        public ReceiptHolder(@NonNull View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.category_tv);
            date = itemView.findViewById(R.id.date_tv);
            total = itemView.findViewById(R.id.total_tv);
            imageView = itemView.findViewById(R.id.image_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(mListener!=null && position !=RecyclerView.NO_POSITION) {
                        mListener.onItemClick(receipts.get(position));
                    }
                }
            });
        }
    }
    public interface OnItemClickListener{
        void onItemClick(Receipt receipt);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }
    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter= new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<Receipt> filteredList = new ArrayList<>();

            if(constraint==null||constraint.length()==0){
                filteredList.addAll(copyOfReceipts);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(Receipt receipt: copyOfReceipts){
                    if(receipt.getCategory().toLowerCase().contains(filterPattern)||receipt.getDate().contains(filterPattern)){
                        filteredList.add(receipt);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values =filteredList;
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            receipts.clear();
            receipts.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };
}
