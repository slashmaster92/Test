package com.stefandinic.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import static com.stefandinic.test.MainActivity.DESC;
import static com.stefandinic.test.MainActivity.TITLE;
import static  com.stefandinic.test.MainActivity.IMG;

public class SecondActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.description);

        String title = "";
        String desc = "";
        String image = "";

        Intent i = getIntent();

        if (i != null) {
            title = i.getStringExtra(TITLE);
            desc = i.getStringExtra(DESC);
            image = i.getStringExtra(IMG);
        }

        TextView textView = (TextView) findViewById(R.id.textView);
        TextView textView2 = (TextView) findViewById(R.id.textView2);
        ImageView imageId = (ImageView) findViewById(R.id.imageId);

        //Displaying image when clicked on an item
        ImageLoader.getInstance().displayImage(image, imageId);
        //Displaying title and description when clicked on an item
        textView.setText(title);
        textView2.setText(desc);

    }
}
