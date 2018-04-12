
package com.example.newscollection.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.example.newscollection.Beans.ZhihuComments;
import com.example.newscollection.Fragments.CnbetaFragment;
import com.example.newscollection.R;
import com.example.newscollection.Fragments.ZhihuFragment;

public class ContentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        final String type = intent.getStringExtra("type");
        setContentView(R.layout.activity_content);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = null;
        final Intent intent1 = new Intent(getApplicationContext(), CommentActivity.class);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab1);
        switch (type) {
            case "Zhihu": {
                final String title = intent.getStringExtra("title");
                final String id = intent.getStringExtra("id");
                fragment = ZhihuFragment.newInstance(id, title);
                intent1.putExtra("id", id);
                intent1.putExtra("type", type);
                break;
            }
            case "Cnbeta": {
                fab.setVisibility(View.GONE);
                final String title = intent.getStringExtra("title");
                final String html = intent.getStringExtra("html");
                final String link = intent.getStringExtra("link");
                final String published = intent.getStringExtra("published");
                fragment = CnbetaFragment.newInstance(title, html, link, published);
                break;
            }
            default:
                break;
        }
        fragmentTransaction.add(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent1);
            }
        });
    }
}
