package ark.noah.wtwtviewer20;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.ViewHolder> {
    private ArrayList<EpisodesContainer> mData;

    private OnItemClickListener mOnItemClickListener;
    private IDDifferenceCallback callback;

    private LinkValidater linkValidater;

    private ToonsContainer currentToon;

    private Drawable foreground;

    public EpisodesAdapter(ArrayList<EpisodesContainer> mData, ToonsContainer currentToon, OnItemClickListener onItemClickListener, IDDifferenceCallback callback) {
        this.mData = mData;
        this.currentToon = currentToon;
        mOnItemClickListener = onItemClickListener;
        this.callback = callback;
    }

    @NonNull
    @Override
    public EpisodesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.recycler_episode_item, parent, false);

        if(LinkValidater.Instance == null) LinkValidater.Instance = new LinkValidater();
        linkValidater = LinkValidater.Instance;

        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.ButtonFocusedForegroundTransparent, value, true);
        foreground = new ColorDrawable(value.data);

        return new EpisodesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodesAdapter.ViewHolder holder, int position) {
        EpisodesContainer mContainer = mData.get(position);

        holder.Number.setText(String.valueOf(mContainer.number));
        holder.Title.setText(mContainer.title);
        holder.Date.setText(mContainer.date.format(DateTimeFormatter.ofPattern(holder.itemView.getContext().getString(R.string.date_format))));

        String urlInString = LinkGetter.Instance.getEntryPoint() + mContainer.link;
        LinkValidater.Info info = linkValidater.extractInfo(urlInString);
        int currentID = info.episodeID;

        if(currentToon.episodeID == currentID)
            holder.Card.setForeground(foreground);
        else
            holder.Card.setForeground(null);

        holder.itemView.setOnClickListener(v -> {
            if(currentID != -1)
                if(callback != null)
                    callback.onIDDifferent(currentID);
            mOnItemClickListener.onClick(v, position);
        });
    }

    public void addAtFront(ArrayList<EpisodesContainer> episodesContainers) {
        mData.addAll(0, episodesContainers);
        notifyItemRangeInserted(0, episodesContainers.size());
    }

    public void add(ArrayList<EpisodesContainer> episodesContainers) {
        int positionStart = mData.size();
        mData.addAll(episodesContainers);
        notifyItemRangeInserted(positionStart, episodesContainers.size());
    }

    public void updateCurrentToon(ToonsContainer toonsContainer) {
        int prev = getPositionOfEpisode(currentToon.episodeID);
        int curr = getPositionOfEpisode(toonsContainer.episodeID);
        currentToon = toonsContainer;
        if(prev!=-1) notifyItemChanged(prev);
        if(curr!=-1) notifyItemChanged(curr);
    }

    public int getPositionOfEpisode(int episodeID) {
        for(int i = 0; i < mData.size(); ++i)
            if(mData.get(i).number == episodeID)
                return i;
        return 0;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public ArrayList<EpisodesContainer> getEditableMData() {
        return mData;
    }

    public void replaceAllData(ArrayList<EpisodesContainer> newMData) {
        mData = newMData;
        notifyDataSetChanged();
    }

    interface OnItemClickListener {
        void onClick(View v, int position);
    }

    interface IDDifferenceCallback {
        void onIDDifferent(int id);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView Number, Title, Date;
        CardView Card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Number = itemView.findViewById(R.id.tv_episodeNumber);
            Title = itemView.findViewById(R.id.tv_episodeTitle);
            Date = itemView.findViewById(R.id.tv_episodeDate);
            Card = itemView.findViewById(R.id.card_rec_episode);
        }
    }
}