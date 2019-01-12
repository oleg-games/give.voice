package com.oleg.givevoice.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.oleg.givevoice.R;

public class AddNewQuestionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_question);
//        Button button = (Button) findViewById(R.id.button);
        Button button = (Button) findViewById(R.id.add_question_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.print("aaa");
                final EditText edit = (EditText) findViewById(R.id.question_text);
//                // выводим сообщение
////                Toast.makeText(this, edit.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

//    public void onAddQuestionClick(View view)
//    {
//        final EditText edit = (EditText) findViewById(R.id.questionText);
//        // выводим сообщение
//        Toast.makeText(this, edit.getText().toString(), Toast.LENGTH_SHORT).show();
//    }
}
