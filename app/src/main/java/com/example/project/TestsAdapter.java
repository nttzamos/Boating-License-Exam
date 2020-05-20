package com.example.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class TestsAdapter extends RecyclerView.Adapter<TestsAdapter.ViewHolder> {
    private ArrayList<Integer> tests;
    private Context context;
    private OnQuestionListener onQuestionListener;

    public TestsAdapter(ArrayList<Integer> tests, Context context, OnQuestionListener onQuestionListener) {
        this.tests = tests;
        this.context = context;
        this.onQuestionListener = onQuestionListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_item, parent, false);
        return new ViewHolder(view, onQuestionListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText("Τεστ " + (position+1));
        int testScore = tests.get(position);
        //System.out.println(testScore);
        if (testScore >= 19)
            holder.verdict.setText("Επέτυχες");
        else holder.verdict.setText("Απέτυχες");
        holder.score.setText(testScore + "/20");
    }

    @Override
    public int getItemCount() {
        return tests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        TextView verdict;
        TextView score;
        ConstraintLayout parentLayout;
        OnQuestionListener onQuestionListener;

        public ViewHolder(@NonNull View itemView, OnQuestionListener onQuestionListener) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            verdict = itemView.findViewById(R.id.verdict);
            score = itemView.findViewById(R.id.score);
            parentLayout = itemView.findViewById(R.id.constraint);
            this.onQuestionListener = onQuestionListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onQuestionListener.onQuestionClick(getAdapterPosition());
        }
    }
    public interface OnQuestionListener{
        void onQuestionClick(int position);
    }

}