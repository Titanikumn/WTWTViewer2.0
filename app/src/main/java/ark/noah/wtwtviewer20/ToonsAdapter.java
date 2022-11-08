package ark.noah.wtwtviewer20;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;

public class ToonsAdapter extends RecyclerView.Adapter<ToonsAdapter.ViewHolder> {
    private ArrayList<ToonsContainer> mData;
    private SortManager sortManager;

    private Drawable foreground;

    private boolean bResorted = true;

    Drawable ic_loading, ic_error, ic_empty;


    @SuppressLint("UseCompatLoadingForDrawables")
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.recycler_item_toons, parent, false);

        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.ButtonFocusedForegroundTransparent, value, true);
        foreground = new ColorDrawable(value.data);

        ic_loading = context.getDrawable(R.drawable.ic_baseline_downloading_24);
        ic_empty   = context.getDrawable(R.drawable.ic_baseline_device_unknown_24);
        ic_error   = context.getDrawable(R.drawable.ic_baseline_error_outline_24);

        TypedValue value2 = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.textColorSecondary, value2, true);
        BlendModeColorFilter greyColorFilter = new BlendModeColorFilter(value2.data, BlendMode.SRC_ATOP);

        ic_loading.setColorFilter(greyColorFilter);
        ic_empty.setColorFilter(greyColorFilter);
        ic_error.setColorFilter(greyColorFilter);

        return new ToonsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ToonsContainer item = mData.get(position);

        holder.ToonName.setText(item.toonName);
        holder.ReleaseWeekday.setText(item.getAllReleaseDaysInString());

        Glide.with(holder.Thumbnail)
                .load(item.thumbnailURL)
                .placeholder(ic_loading)
                .error(ic_error)
                .fallback(ic_empty)
                .into(holder.Thumbnail);

        Calendar calendar = Calendar.getInstance();
        Integer[] releaseWeekdays = item.getAllReleaseDaysInArray();
        boolean anymatch = false;
        for (Integer day : releaseWeekdays) {
            if (day == calendar.get(Calendar.DAY_OF_WEEK)) {
                holder.cardView.setForeground(foreground);
                anymatch = true;
                break;
            }
        }
        if(!anymatch) holder.cardView.setForeground(null);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setItemVisivility(int position, boolean beVisible) {
        mData.get(position).hide = beVisible;
        notifyItemChanged(position);
    }

    public int deleteAndGetDBIDof(ToonsContainer item) {
        int id = item.dbID;
        int posToUpdate = mData.indexOf(item);
        mData.remove(item);
        notifyItemRemoved(posToUpdate);
        return id;
    }

    public ArrayList<ToonsContainer> getmData() {
        return mData;
    }

    public SortManager getSortManager() { return sortManager; }
    public void sortData(Comparator<ToonsContainer> toonsContainerComparator) {
        mData.sort(toonsContainerComparator);
        bResorted = true;
        notifyDataSetChanged();     //list after sort might be completely different from list before sort, so no choice.
    }

    public boolean isResorted() {
        if(bResorted) {
            bResorted = false;
            return true;
        }
        return false;
    }

    public ToonsAdapter(ArrayList<ToonsContainer> list) {
        mData = list;
        sortManager = new SortManager();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView Thumbnail;
        TextView ToonName;
        TextView ReleaseWeekday;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card_rec_main);
            Thumbnail = itemView.findViewById(R.id.img_toons_thumbnail);
            ToonName = itemView.findViewById(R.id.tv_toons_title);
            ReleaseWeekday = itemView.findViewById(R.id.tv_toons_release);
        }
    }

    enum SortType {
        FIFO,
        ALPHABET,
        RELEASEDAY,
    }
    enum SortDirection {
        ASCENDING,
        DESCENDING,
    }

    class SortManager {
        private SortType sortType = SortType.FIFO;
        private SortDirection sortDirection = SortDirection.ASCENDING;

        public boolean isSortedByAlphabet() { return sortType == SortType.ALPHABET; }
        public boolean isSortedByFIFO() { return sortType == SortType.FIFO; }
        public boolean isSortedByReleaseDay() { return sortType == SortType.RELEASEDAY; }
        public void setSortType(SortType sortType) { this.sortType = sortType; }

        public boolean isAscending() { return sortDirection == SortDirection.ASCENDING; }
        public void setSortDirection(SortDirection dir) { sortDirection = dir; }

        @NonNull
        public String toString() {
            return sortType.toString() + " " + sortDirection.toString();
        }
    }
}