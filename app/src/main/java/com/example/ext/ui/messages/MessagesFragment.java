package com.example.ext.ui.messages;

import static com.example.ext.ConfigApiResponses.MESSAGES;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
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
import com.example.ext.databinding.FragmentMessagesBinding;
import com.example.ext.ui.note.NoteViewModel;

import java.util.ArrayList;
import java.util.Map;


public class MessagesFragment extends Fragment {
    private final Handler HandlerCheckAllAccess = new Handler();
    private MessagesViewModel notificationsViewModel;
    private FragmentMessagesBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMessagesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        if (MESSAGES == null){
            notificationsViewModel =
                    new ViewModelProvider(this).get(MessagesViewModel.class);
            HandlerCheckAllAccess.post(CheckAllAccess);
            return root;
        } else {
            setUI(MESSAGES);
        }

        return root;
    }

    private final Runnable CheckAllAccess = new Runnable() {
        @Override
        public void run() {
            if (notificationsViewModel.getMapMessages().getValue() != null){
                if (notificationsViewModel.getMapMessages().getValue().keySet().size() < 2){
                    new NoteViewModel.asyncEXT().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    HandlerCheckAllAccess.postDelayed(this, 5000);
                } else{
                    HandlerCheckAllAccess.removeCallbacks(CheckAllAccess);
                }
                setUI(notificationsViewModel.getMapMessages().getValue());
            } else {
                HandlerCheckAllAccess.postDelayed(this, 5000);
            }
        }
    };

    public void setUI(Map<String, ArrayList> result){
        TableLayout tbl_other_content;
        if (binding != null ){
            tbl_other_content = binding.tblMessagesContent;
            TableLayout.LayoutParams trRowParams = new TableLayout.LayoutParams();
            trRowParams.setMargins(20, 20, 20, 30);
            for (String teacher: result.keySet()) {
                TableRow row_of_msg_teacher = new TableRow(getContext());
                TableLayout tbl_of_msg = new TableLayout(getContext());
                tbl_of_msg.setPadding(20,20,20,20);
                tbl_of_msg.setElevation(5);
                tbl_of_msg.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.less_rounded_corners_transp));
                //50
                ArrayList<ArrayList<String>> array = result.get(teacher);
                TextView tt = new TextView(getContext());
                tt.setText(teacher);
                tt.setTextSize(21);
                tt.setTextColor(ContextCompat.getColor(getContext(), R.color.background_material_dark));
                tt.setPadding(20, 0, 0, 0);
                row_of_msg_teacher.addView(tt);
                tbl_other_content.addView(row_of_msg_teacher);

                for (int j = 0; j < array.size(); j++) {
                    TableRow row_of_msg = new TableRow(getContext());
                    row_of_msg.setPadding(10,20,10,20);
                    row_of_msg.setElevation(5);
                    row_of_msg.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.less_rounded_corners));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        row_of_msg.setAutofillHints(array.get(j).get(1) + " " + array.get(j).get(0));
                    }

                    String message = array.get(j).get(1);
//                    String str ="";
//                    if(message.length() >= 45) {
//                        int total = message.length() / 45;
//                        List<String> arr = new ArrayList<>();
//                        for (int i = 0; i < total; i++) {
//                            arr.add(message.substring(45*i, 45+45*i));
//                        }
//                        arr.add(message.substring(45*total));
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                            str = arr.stream()
//                                    .map(String::valueOf)
//                                    .collect(Collectors.joining("\n"));
//                        }
//                    } else {
//                        str = str+ message;
//                    }
                    TextView msg = new TextView(getContext());
                    msg.setText(message);
                    msg.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                    msg.setPadding(40, 5, 40, 10);
                    msg.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT, 1f));
                    msg.setPadding(15, 10, 15, 10);
                    msg.setTextSize(17);

                    TextView date = new TextView(getContext());
                    date.setText(array.get(j).get(0));
                    date.setPadding(25, 10, 20, 0);
                    date.setTextSize(20);
                    date.setTextColor(ContextCompat.getColor(getContext(), R.color.white_two));
                    tbl_of_msg.addView(date);

                    row_of_msg.addView(msg);
                    tbl_of_msg.addView(row_of_msg, trRowParams);
                }
                tbl_other_content.addView(tbl_of_msg, trRowParams);
            }
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}