package com.example.newscollection.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.example.newscollection.R;

public class CnbetaFragment extends Fragment {
    private static final String TITLE = "title";
    private static final String LINK = "link";
    private static final String HTML = "html";
    private static final String PUBLISHED = "published";
    private String title;
    private String link;
    private String html;
    private String published;


    public CnbetaFragment() {
        // Required empty public constructor
    }

    public static CnbetaFragment newInstance(String title, String html, String link, String published) {
        CnbetaFragment fragment = new CnbetaFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(HTML, html);
        args.putString(LINK, link);
        args.putString(PUBLISHED, published);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cnbeta, container, false);
        WebView webView = view.findViewById(R.id.webview1);
        webView.getSettings().setDefaultTextEncodingName("UTF-8");
        webView.loadData(getArguments().getString(HTML), "text/html;charset=UTF-8", null);
        Toolbar toolbar = view.findViewById(R.id.toolbar4);
        toolbar.setTitle(getArguments().getString(TITLE));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        TextView textView = view.findViewById(R.id.textView5);
        textView.setText(getArguments().getString(TITLE));
        setHasOptionsMenu(true);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_share) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, getArguments().getString(TITLE) + getArguments().getString(LINK) + "\nFrom Cnbeta");
                    startActivity(Intent.createChooser(intent, "Share"));
                }
                return true;
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_content, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }
}
