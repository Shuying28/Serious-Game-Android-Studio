package com.project.quiz_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.project.quiz_app.R;

public class SelectAvatarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_avatar);

        ImageView avatar1 = findViewById(R.id.avatar1);
        ImageView avatar2 = findViewById(R.id.avatar2);
        ImageView avatar3 = findViewById(R.id.avatar3);
        ImageView avatar4 = findViewById(R.id.avatar4);
        ImageView avatar5 = findViewById(R.id.avatar5);
        ImageView avatar6 = findViewById(R.id.avatar6);
        ImageView avatar7 = findViewById(R.id.avatar7);
        ImageView avatar8 = findViewById(R.id.avatar8);
        ImageView avatar9 = findViewById(R.id.avatar9);


        avatar1.setOnClickListener(v -> selectAvatar(R.drawable.avatar1));
        avatar2.setOnClickListener(v -> selectAvatar(R.drawable.avatar2));
        avatar3.setOnClickListener(v -> selectAvatar(R.drawable.avatar3));
        avatar4.setOnClickListener(v -> selectAvatar(R.drawable.avatar4));
        avatar5.setOnClickListener(v -> selectAvatar(R.drawable.avatar5));
        avatar6.setOnClickListener(v -> selectAvatar(R.drawable.avatar6));
        avatar7.setOnClickListener(v -> selectAvatar(R.drawable.avatar7));
        avatar8.setOnClickListener(v -> selectAvatar(R.drawable.avatar8));
        avatar9.setOnClickListener(v -> selectAvatar(R.drawable.avatar9));
    }

    private void selectAvatar(int avatarResId) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("selectedAvatar", avatarResId);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
