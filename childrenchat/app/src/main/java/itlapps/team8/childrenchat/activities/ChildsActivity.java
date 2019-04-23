package itlapps.team8.childrenchat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.r0adkll.slidr.Slidr;

import itlapps.team8.childrenchat.R;

public class ChildsActivity extends AppCompatActivity {
    private FloatingActionButton floatingActionButtonAddChild;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_childs);
        Slidr.attach(this);

        floatingActionButtonAddChild = findViewById(R.id.fb_addchild);

        floatingActionButtonAddChild.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddChildActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
