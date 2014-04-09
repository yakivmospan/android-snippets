public class Adapter
        extends BaseAdapter {

    private List<Object> mData;
    private LayoutInflater mInflater;

    public Adapter(Context context, List<Object> data) {
        mInflater = LayoutInflater.from(context);
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int thePosition) {
        return this.mData.get(thePosition);
    }

    @Override
    public long getItemId(int thePosition) {
        return thePosition;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.layoutId, parent, false);

            holder.view = convertView.findViewById(R.id.viewId);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    public void setData(List<Object> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public void addData(List<Object> data) {
        mData.addAll(data);
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        View view;
    }
}
