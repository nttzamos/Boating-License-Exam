package com.example.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private ArrayList<String> mQuestion;
    private ArrayList<Boolean> mTrueOrFalse;
    private Context mContext;
    private OnQuestionListener mOnQuestionListener;
    String code;

    public Adapter(ArrayList<String> question, ArrayList<Boolean> trueOrFalse, String code, Context mContext, OnQuestionListener onQuestionListener) {
        mQuestion = question;
        mTrueOrFalse = trueOrFalse;
        this.code = code;
        this.mContext = mContext;
        this.mOnQuestionListener = onQuestionListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (code.equals("tests_list"))
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_test, parent, false);
        else view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_new, parent, false);
        return new ViewHolder(view, mOnQuestionListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (!code.equals("tests_list") && !code.equals("saved_questions")) {
            if (mTrueOrFalse.get(position))
                holder.trueOrFalse.setImageResource(R.drawable.custom_correct);
            else holder.trueOrFalse.setImageResource(R.drawable.custom_wrong);
        }

        if(mQuestion.get(position).length()<95)
            holder.question.setText(mQuestion.get(position));
        else
            holder.question.setText(mQuestion.get(position).substring(0,85)+"...");

        if (code.equals("tests_list"))
            holder.title.setText("Τεστ "+ (position+1));
        else holder.title.setText("Ερώτηση "+ (position+1));
    }

    @Override
    public int getItemCount() {
        return mQuestion.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView trueOrFalse;
        TextView question;
        TextView title;
        RelativeLayout parentLayout;
        OnQuestionListener onQuestionListener;

        public ViewHolder(@NonNull View itemView, OnQuestionListener onQuestionListener) {
            super(itemView);
            trueOrFalse = itemView.findViewById(R.id.trueOrFalse);
            question = itemView.findViewById(R.id.question);
            title = itemView.findViewById(R.id.title);
            parentLayout = itemView.findViewById(R.id.relativeLayout);
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