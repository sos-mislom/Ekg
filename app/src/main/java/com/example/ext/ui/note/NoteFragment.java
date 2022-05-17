package com.example.ext.ui.note;

import static com.example.ext.ConfigApiResponses.MARKS;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ext.R;
import com.example.ext.databinding.FragmentNoteBinding;
import com.example.ext.ui.dialogs.NoteInfoDialogFragment;
import com.example.ext.ui.dialogs.SubjInfoDialogFragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;


public class NoteFragment extends Fragment {

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
    private void setUI(Map<String, ArrayList> data){
        Typeface typefaceRoboto = Typeface.createFromAsset(getContext().getAssets(), "Roboto-Regular.ttf");
        final TableLayout tbl_other_content = binding.tblNoteContent;

        TableLayout.LayoutParams tlLayoutParams = new TableLayout.LayoutParams();
        tlLayoutParams.setMargins(20,20,20,20);

        for (String subj: data.keySet()) {
            int count = 0;
            double nominator = 0.0;
            double denominator = 0.0;
            double weight;
            TableLayout tableLayout = new TableLayout(getContext());
            tableLayout.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.less_rounded_corners));
            tableLayout.setElevation(5);

            TableRow row_of_subj_name = new TableRow(getContext());

            TableLayout tbl_of_subj_notes = new TableLayout(getContext());
            TableRow row_of_notes = new TableRow(getContext());
            TextView average_note = new TextView(getContext());
            TextView subj_name = new TextView(getContext());

            String subj_name_str;
            if (subj.length() > 26) { subj_name_str = subj.substring(0, 23)+"...";}else  subj_name_str = subj;
            subj_name.setText(subj_name_str);
            subj_name.setTypeface(typefaceRoboto);
            subj_name.setTextColor(ContextCompat.getColor(getContext(), R.color.abc_search_url_text_pressed));
            subj_name.setTextSize(22);
            subj_name.setPadding(25,0,10,45);

            row_of_subj_name.addView(subj_name);

            row_of_subj_name.addView(average_note);
            ArrayList<ArrayList<String>> array = data.get(subj);
            int start = 0;
            if (array.size() >= 8){
                start = array.size()-7;
            }

            for (int j = 0; j < array.size(); j++) {
                TextView mark_tv = new TextView(getContext());
                mark_tv.setTextSize(26);
                mark_tv.setTextColor(Color.WHITE);
                mark_tv.setGravity(Gravity.CENTER);
                mark_tv.setMinWidth(85);
                mark_tv.setHeight(85);
                mark_tv.setTypeface(typefaceRoboto);
                mark_tv.setElevation(5f);

                TableRow.LayoutParams trRowParams = new TableRow.LayoutParams();
                trRowParams.setMargins(20, 10, 20, 30);

                Drawable back_gradient;
                switch (array.get(j).get(2)) {
                    case ("4"):
                        back_gradient = ContextCompat.getDrawable(getContext(), R.drawable.average_mark_background_gradient_ok);
                        break;
                    case ("3"):
                        back_gradient = ContextCompat.getDrawable(getContext(), R.drawable.average_mark_background_gradient_normal);
                        break;
                    case ("5"):
                        back_gradient = ContextCompat.getDrawable(getContext(), R.drawable.average_mark_background_gradient_good);
                        break;
                    case ("2"):
                        back_gradient = ContextCompat.getDrawable(getContext(), R.drawable.average_mark_background_gradient_bad);
                        break;
                    default:
                        back_gradient = ContextCompat.getDrawable(getContext(), R.drawable.average_mark_background_gradient_default);
                        break;
                }

                LayerDrawable finalDrawable = new LayerDrawable(new Drawable[] {back_gradient});
                if (!array.get(j).get(1).equals("null")){
                    Drawable dotIcon = ContextCompat.getDrawable(getContext(), R.drawable.oval);
                    finalDrawable = new LayerDrawable(new Drawable[] {back_gradient, dotIcon});
                    finalDrawable.setLayerInset(0, 0, 0, 0, 0);
                    finalDrawable.setLayerInset(1,65,0,0,65);
                }
                mark_tv.setBackgroundDrawable(finalDrawable);

                weight = Double.parseDouble(array.get(j).get(3));
                if (array.get(j).get(2).matches("[0-9]")) {
                    count++;
                    nominator += Integer.parseInt(array.get(j).get(2)) * weight;
                    denominator += weight;
                }
                mark_tv.setText(array.get(j).get(2));

                int finalJ = j;
                mark_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NoteInfoDialogFragment newFragment = new NoteInfoDialogFragment(array.get(finalJ));
                        newFragment.show(getChildFragmentManager().beginTransaction(), "info");
                    }
                });
                if (j >= start){
                    row_of_notes.addView(mark_tv, trRowParams);
                }

            }

            if (count > 2) {
                String formattedDouble = new DecimalFormat("#0.00").format(nominator / denominator);
                average_note.setText(formattedDouble);
            } else {
                average_note.setText("0.00");
            }

            average_note.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.my_border_tv));
            average_note.setTextSize(20);
            average_note.setGravity(Gravity.CENTER);
            average_note.setPadding(10, 10, 10, 10);
            average_note.setTextColor(ContextCompat.getColor(getContext(), R.color.camo_green));


            //row_of_notes.addView(average_note, 0);
//            ImageView imageView = new ImageView(getContext());
//            imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.more_res));
//            row_of_notes.addView(imageView, 0);


            tbl_of_subj_notes.addView(row_of_notes);


            tableLayout.addView(row_of_subj_name, tlLayoutParams);
            tableLayout.addView(tbl_of_subj_notes, tlLayoutParams);

            tableLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SubjInfoDialogFragment newFragment = new SubjInfoDialogFragment(data.get(subj), subj);
                    newFragment.show(getChildFragmentManager().beginTransaction(), "info");
                }
            });

            tbl_other_content.addView(tableLayout, tlLayoutParams);
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}