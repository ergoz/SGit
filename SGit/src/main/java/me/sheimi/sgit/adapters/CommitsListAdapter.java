package me.sheimi.sgit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import me.sheimi.sgit.R;
import me.sheimi.sgit.database.models.Repo;
import me.sheimi.sgit.utils.CommonUtils;
import me.sheimi.sgit.utils.ImageCache;
import me.sheimi.sgit.utils.ViewUtils;

/**
 * Created by sheimi on 8/18/13.
 */
public class CommitsListAdapter extends ArrayAdapter<RevCommit> {

    private Repo mRepo;
    private static final SimpleDateFormat COMMITTIME_FORMATTER = new
            SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
    private Set<Integer> mChosenItems;
    private ViewUtils mViewUtils;

    public CommitsListAdapter(Context context, Set<Integer> chosenItems, Repo repo) {
        super(context, 0);
        mChosenItems = chosenItems;
        mViewUtils = ViewUtils.getInstance(context);
        mRepo = repo;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        CommitsListItemHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem_commits,
                    parent, false);
            holder = new CommitsListItemHolder();
            holder.commitsTitle = (TextView) convertView.findViewById(R.id
                    .commitTitle);
            holder.commitsIcon = (ImageView) convertView.findViewById(R.id
                    .commitIcon);
            holder.commitAuthor = (TextView) convertView.findViewById(R.id
                    .commitAuthor);
            holder.commitsMsg = (TextView) convertView.findViewById(R.id
                    .commitMsg);
            holder.commitTime = (TextView) convertView.findViewById(R.id
                    .commitTime);
            convertView.setTag(holder);
        } else {
            holder = (CommitsListItemHolder) convertView.getTag();
        }
        RevCommit commit = getItem(position);
        PersonIdent person = commit.getCommitterIdent();
        Date date = person.getWhen();
        String email = person.getEmailAddress();

        holder.commitsTitle.setText(Repo.getCommitDisplayName(commit.getName()));
        holder.commitAuthor.setText(person.getName());
        holder.commitsMsg.setText(commit.getShortMessage());
        holder.commitTime.setText(COMMITTIME_FORMATTER.format(date));
        holder.commitsIcon.setImageResource(R.drawable.ic_default_author);

        ImageLoader im = ImageCache.getInstance(getContext()).getImageLoader();
        im.displayImage(CommonUtils.buildGravatarURL(email), holder.commitsIcon);

        if (mChosenItems.contains(position)) {
            convertView.setBackgroundColor(mViewUtils.getColor(R.color.pressed_sgit));
        } else {
            convertView.setBackgroundColor(mViewUtils.getColor(android.R.color.transparent));
        }

        return convertView;
    }

    public void resetCommit() {
        clear();
        List<RevCommit> commitsList = mRepo.getCommitsList();
        if (commitsList != null) {
            // TODO why == null
            mViewUtils.adapterAddAll(this, commitsList);
        }
        notifyDataSetChanged();
    }

    private static class CommitsListItemHolder {
        public ImageView commitsIcon;
        public TextView commitsTitle;
        public TextView commitsMsg;
        public TextView commitAuthor;
        public TextView commitTime;
    }
}
