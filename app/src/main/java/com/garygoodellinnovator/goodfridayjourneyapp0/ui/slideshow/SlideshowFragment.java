package com.garygoodellinnovator.goodfridayjourneyapp0.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.garygoodellinnovator.goodfridayjourneyapp0.CustomAdapter;
import com.garygoodellinnovator.goodfridayjourneyapp0.DataModel;
import com.garygoodellinnovator.goodfridayjourneyapp0.MainActivity;
import com.garygoodellinnovator.goodfridayjourneyapp0.MyData;
import com.garygoodellinnovator.goodfridayjourneyapp0.R;
import com.garygoodellinnovator.goodfridayjourneyapp0.ui.gallery.GalleryViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class SlideshowFragment extends Fragment {

    public static RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView.Adapter adapter;
    private static ArrayList<DataModel> data;
    static View.OnClickListener myOnClickListener;
    private static ArrayList<Integer> removedItems;
    private ImageView ivIcon;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

        myOnClickListener = new MyOnClickListener();

        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(root.getContext(),
                RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        SnapHelper helper = new PagerSnapHelper();
        helper.attachToRecyclerView(recyclerView);

        data = new ArrayList<DataModel>();
        for (int i = 0; i < MyData.numberOfStationsToLoad; i++) {
            data.add(new DataModel(
                    MyData.nameArray[i],
                    MyData.timeOfDayArray[i],
                    MyData.iconArray[i],
                    MyData.pictureArray[i],
                    MyData.soundArray[i],
                    MyData.headingArray[i],
                    MyData.scriptureArray[i],
                    MyData.prayerArray[i],
                    MyData.snackbarArray[i],
                    MyData._id[i]));
        }

        removedItems = new ArrayList<Integer>();

        adapter = new CustomAdapter(data);
        recyclerView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        int pos = ((MainActivity)getActivity()).eventIndex;
        Toast.makeText(getContext(), "Moving on to event #" + pos,
                Toast.LENGTH_LONG).show();

        recyclerView.scrollToPosition(pos);
    }

    public static View.OnClickListener myOnClickListener() {
        return new MyOnClickListener();
    }

    public static class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Snackbar.make(v, "Replace with your own action",
                    Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

        //TODO: Use in gallery view for filtering journey landmark cards
        private void removeItem(View v) {
            int selectedItemPosition = recyclerView.getChildAdapterPosition(v);
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(selectedItemPosition);
            TextView textViewName = viewHolder.itemView.findViewById(R.id.tvName);
            String selectedName = textViewName.getText().toString();
            int selectedItemId = -1;
            for (int i = 0; i < MyData.nameArray.length; i++) {
                if (selectedName.equals(MyData.nameArray[i])) {
                    selectedItemId = MyData._id[i];
                }
            }
            //removedItems.add(selectedItemId);
            //data.remove(selectedItemPosition);
            //adapter.notifyItemRemoved(selectedItemPosition);

        }
    }
}