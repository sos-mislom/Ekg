package com.example.ext;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Activity_after_log extends AppCompatActivity {
    TextView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_log);
        String result = getIntent().getStringExtra("result");
        int BOOKSHELF_ROWS = 5;
        int BOOKSHELF_COLUMNS = 5;
        TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout);
        for (int i = 0; i < BOOKSHELF_ROWS; i++) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            for (int j = 0; j < BOOKSHELF_COLUMNS; j++) {
                imageView.setText(result);
                tableRow.addView(imageView, j);
            }

            tableLayout.addView(tableRow, i);
        }
    }
}