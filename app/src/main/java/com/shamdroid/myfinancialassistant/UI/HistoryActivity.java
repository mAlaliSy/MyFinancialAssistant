package com.shamdroid.myfinancialassistant.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shamdroid.myfinancialassistant.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryActivity extends AppCompatActivity implements HistoryRecyclerAdapter.DataLoaderListener {


    @BindView(R.id.historyRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.progHistoryLoading)
    ProgressBar progLoading;

    @BindView(R.id.noTransactions)
    LinearLayout noTransactions;

    HistoryRecyclerAdapter historyRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ButterKnife.bind(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);

        historyRecyclerAdapter = new HistoryRecyclerAdapter(this,recyclerView);

        recyclerView.setAdapter(historyRecyclerAdapter);

    }

    @Override
    public void onDataLoadedFirstTime() {
        progLoading.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void noDataFound() {

        progLoading.setVisibility(View.GONE);
        noTransactions.setVisibility(View.VISIBLE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == HistoryRecyclerAdapter.EDITING_ACTIVITY_CODE){
            historyRecyclerAdapter.onEditResult();
        }


    }
}
