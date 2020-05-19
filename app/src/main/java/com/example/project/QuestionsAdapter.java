package com.example.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {
    private ArrayList<String> question;
    private ArrayList<Boolean> trueOrFalse;
    private Context context;
    private OnQuestionListener onQuestionListener;
    String code;

    public QuestionsAdapter(ArrayList<String> question, ArrayList<Boolean> trueOrFalse, String code, Context mContext, OnQuestionListener onQuestionListener) {
        this.question = question;
        this.trueOrFalse = trueOrFalse;
        this.code = code;
        this.context = mContext;
        this.onQuestionListener = onQuestionListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_new, parent, false);
        return new ViewHolder(view, onQuestionListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (!code.equals("saved_questions")) {
            if (trueOrFalse.get(position))
                holder.trueOrFalse.setImageResource(R.drawable.custom_correct);
            else holder.trueOrFalse.setImageResource(R.drawable.custom_wrong);
        }

        if(question.get(position).length()<95)
            holder.question.setText(question.get(position));
        else
            holder.question.setText(question.get(position).substring(0,85)+"...");


        holder.title.setText("Ερώτηση " + (position+1));
    }

    @Override
    public int getItemCount() {
        return question.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView trueOrFalse;
        TextView question;
        TextView title;
        ConstraintLayout parentLayout;
        OnQuestionListener onQuestionListener;

        public ViewHolder(@NonNull View itemView, OnQuestionListener onQuestionListener) {
            super(itemView);
            trueOrFalse = itemView.findViewById(R.id.trueOrFalse);
            question = itemView.findViewById(R.id.question);
            title = itemView.findViewById(R.id.title);
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