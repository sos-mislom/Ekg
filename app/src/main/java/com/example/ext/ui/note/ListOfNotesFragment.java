package com.example.ext.ui.note;

import static com.example.ext.ConfigApiResponses.MARKS;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.ext.ui.ObjectOfSubject;
import com.example.ext.R;
import com.example.ext.databinding.FragmentNoteBinding;

import java.util.ArrayList;
import java.util.Map;


public class ListOfNotesFragment extends Fragment {

    private FragmentNoteBinding binding;
    private Handler HandlerCheckAllAccess = new Handler();
    private NoteViewModel notificationsViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNoteBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        if (MARKS == null){
            notificationsViewModel =
                    new ViewModelProvider(this).get(NoteViewModel.class);
            HandlerCheckAllAccess.post(CheckAllAccess);
            return root;
        } else {
            setUI(MARKS);
        }
        return root;
    }

    private final Runnable CheckAllAccess = new Runnable() {
        @Override
        public void run() {
            if (notificationsViewModel.getNoteMap().getValue() != null){
                if (notificationsViewModel.getNoteMap().getValue().keySet().size() < 2){
                    new NoteViewModel.asyncEXT().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    HandlerCheckAllAccess.postDelayed(this, 5000);
                } else{
                    HandlerCheckAllAccess.removeCallbacks(CheckAllAccess);
                }
                setUI(notificationsViewModel.getNoteMap().getValue());
            } else {
                HandlerCheckAllAccess.postDelayed(this, 5000);
            }
        }
    };
    public static void clearTableView(ViewGroup tblview){
        for (View i : getAllChildren(tblview)) {
            ((ViewGroup) tblview).removeView(i);
        }
    }
    public static ArrayList<View> getAllChildren(View v) {
        if (!(v instanceof ViewGroup)) {
            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            return viewArrayList;
        }
        ArrayList<View> result = new ArrayList<View>();
        ViewGroup viewGroup = (ViewGroup) v;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            viewArrayList.addAll(getAllChildren(child));
            result.addAll(viewArrayList);
        }
        return result;
    }
    @SuppressLint("ResourceType")
    private void setUI(Map<String, ArrayList> data){

        for (String subj: data.keySet()) {
            Bundle bundle = new Bundle();
            ObjectOfSubject obj = new ObjectOfSubject(subj,  data);
            bundle.putParcelable("subject_key", obj);

            NoteFragment youFragment = new NoteFragment();
            youFragment.setArguments(bundle);

            FragmentManager fragmentManager = getChildFragmentManager();

            fragmentManager.beginTransaction()
                    .add(R.id.tbl_note_content, youFragment)
                    .addToBackStack("myStack")
                    .commit();
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}
