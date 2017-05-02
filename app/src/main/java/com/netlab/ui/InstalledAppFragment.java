package com.netlab.ui;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.netlab.actfreezer.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZQ on 2017/5/2.
 */

public class InstalledAppFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    private List<AppStatus> installedAppStatus = null;

    public InstalledAppFragment() {

    }

    public static Fragment newInstance() {
        Fragment fragment = new InstalledAppFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.installedapp_fragment_layout, container, false);



        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.installedapps_recycler_view);

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(getActivity());


        /**
         * set layout manager
         */
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);

        /**
         * set adaptor
         */
        mAdapter = new CustomAdapter();

        LoadInstalledPkgTask task = new LoadInstalledPkgTask();
        task.execute();

        // END_INCLUDE(initializeRecyclerView)


        return rootView;
    }


    private List<AppStatus> getAppStatus()
    {
        List<AppStatus> installedAppStatus = new ArrayList<>();

        PackageManager pm = getActivity().getPackageManager();
        List<ApplicationInfo> pkgList = pm.getInstalledApplications(0);

        for(ApplicationInfo info:pkgList)
        {
            installedAppStatus.add(new AppStatus(info.loadIcon(pm),info.packageName));
        }

        return installedAppStatus;
    }


    class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
        private static final String TAG = "CustomAdapter";

        private String[] mDataSet;

        private static final int DATASET_COUNT = 60;

        /**
         * Generates Strings for RecyclerView's adapter. This data would usually come
         * from a local content provider or remote server.
         */
        private void initDataset() {
            mDataSet = new String[DATASET_COUNT];
            for (int i = 0; i < DATASET_COUNT; i++) {
                mDataSet[i] = "This is element #" + i;
            }
        }

        // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
        /**
         * Provide a reference to the type of views that you are using (custom ViewHolder)
         */
        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView pkg_name_textview;
            private final ImageView icon_view;
            public ViewHolder(View v) {
                super(v);
                // Define click listener for the ViewHolder's View.
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                    }
                });
                pkg_name_textview = (TextView) v.findViewById(R.id.textview_package_name);
                icon_view = (ImageView) v.findViewById(R.id.app_icon);
            }

            public TextView getPkg_name_textview() {
                return pkg_name_textview;
            }
            public ImageView getIcon_view() {return icon_view; }
        }
        // END_INCLUDE(recyclerViewSampleViewHolder)

        /**
         * Initialize the dataset of the Adapter.
         */
        public CustomAdapter() {
            initDataset();
        }

        // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            // Create a new view.
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.usage_row, viewGroup, false);

            return new ViewHolder(v);
        }
        // END_INCLUDE(recyclerViewOnCreateViewHolder)

        // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {
            Log.d(TAG, "Element " + position + " set.");

            // Get element from your dataset at this position and replace the contents of the view
            // with that element
            viewHolder.getPkg_name_textview().setText(installedAppStatus.get(position).packageName);
            viewHolder.getIcon_view().setImageDrawable(installedAppStatus.get(position).appIcon);
        }
        // END_INCLUDE(recyclerViewOnBindViewHolder)

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return installedAppStatus.size();
        }
    }

    class LoadInstalledPkgTask extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] objects) {
            installedAppStatus = getAppStatus();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            // Set CustomAdapter as the adapter for RecyclerView.
            mRecyclerView.setAdapter(mAdapter);
        }
    }


}
