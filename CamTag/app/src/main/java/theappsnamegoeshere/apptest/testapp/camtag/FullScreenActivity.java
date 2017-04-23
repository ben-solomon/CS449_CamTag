package theappsnamegoeshere.apptest.testapp.camtag;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.view.View;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class FullScreenActivity extends Activity {
    ArrayList<String> currentGallery;

    String cursorfname;
    int index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);
        final ImageView iv = (ImageView) findViewById(R.id.imageView2);
        ImageButton shareb = (ImageButton) findViewById(R.id.shareButton);
        ImageButton editb = (ImageButton) findViewById(R.id.imageButton2);
        editb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Edit Tags under construction!",Toast.LENGTH_SHORT).show();
            }
        });
        shareb.setVisibility(View.GONE);
        shareb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareFile(cursorfname);
            }
        });
        currentGallery = getIntent().getStringArrayListExtra("curgallery");
        cursorfname = getIntent().getStringExtra("curimage");
        index = currentGallery.indexOf(cursorfname);
        File temp = new File(Environment.getExternalStorageDirectory().toString()+"/CamTag/" + cursorfname);
        if (!temp.exists())
        {
            Toast.makeText(getApplicationContext(),"load fail", Toast.LENGTH_LONG).show();
        }
        else{
            Picasso.with(FullScreenActivity.this).load(temp).into(iv);

        }
iv.setOnTouchListener(new OnTouchListener() {
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        final ImageButton bb = (ImageButton) findViewById(R.id.backButton);
        final ImageButton nb = (ImageButton) findViewById(R.id.nextButton);
        final ImageButton sb = (ImageButton) findViewById(R.id.shareButton);
        final ImageButton eb = (ImageButton) findViewById(R.id.imageButton2); //edit button
        eb.setImageResource(R.drawable.edit);
        bb.setImageResource(R.drawable.back);
        nb.setImageResource(R.drawable.back);
        sb.setImageResource(R.drawable.share);
        eb.setVisibility(View.VISIBLE);
        sb.setVisibility(View.VISIBLE);
        bb.setVisibility(View.VISIBLE);
        nb.setVisibility(View.VISIBLE);
        eb.postDelayed(new Runnable() {
            public void run() {
                bb.setVisibility(View.GONE);
            }
        }, 5000);
        bb.postDelayed(new Runnable() {
            public void run() {
                bb.setVisibility(View.GONE);
            }
        }, 5000);
        sb.postDelayed(new Runnable() {
            public void run() {
                sb.setVisibility(View.GONE);
            }
        }, 5000);

        nb.postDelayed(new Runnable() {
            public void run() {
                nb.setVisibility(View.GONE);
            }
        }, 5000);
return false;}

});
        ImageButton bb = (ImageButton) findViewById(R.id.backButton);
        ImageButton nb = (ImageButton) findViewById(R.id.nextButton);
        nb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index = currentGallery.indexOf(cursorfname);
                index += 1;
                if (index == currentGallery.size()) {
                    cursorfname = currentGallery.get(0);
                    index = 0;
                } else {
                    cursorfname = currentGallery.get(index);
                }

                File temp = new File(Environment.getExternalStorageDirectory().toString() + "/CamTag/" + cursorfname);
                if (!temp.exists()) {
                    Toast.makeText(getApplicationContext(), "next load fail", Toast.LENGTH_LONG).show();
                } else {
                    Picasso.with(FullScreenActivity.this).load(temp).into(iv);

                }
            }
        });
        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index = currentGallery.indexOf(cursorfname);
                index -= 1;
                if (index < 0){
                    cursorfname = currentGallery.get(currentGallery.size()-1);
                    index = currentGallery.size()-1;
                    File temp = new File(Environment.getExternalStorageDirectory().toString()+"/CamTag/" + cursorfname);
                    if (!temp.exists())
                    {
                        Toast.makeText(getApplicationContext(),"next load fail", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Picasso.with(FullScreenActivity.this).load(temp).into(iv);

                    }
                }
                else{
                    cursorfname = currentGallery.get(index);
                    File temp = new File(Environment.getExternalStorageDirectory().toString()+"/CamTag/" + cursorfname);
                    if (!temp.exists())
                    {
                        Toast.makeText(getApplicationContext(),"next load fail", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Picasso.with(FullScreenActivity.this).load(temp).into(iv);
                    }
                }
            }
        });
    }
    public void shareFile(String filename){
        Intent email = new Intent(Intent.ACTION_SEND);
        email.setType("image/gif");
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{"to@example.com"});
        email.putExtra(Intent.EXTRA_SUBJECT, "");
        email.putExtra(Intent.EXTRA_TEXT,"Sent from CamTag");
        email.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///"+Environment.getExternalStorageDirectory().toString() + "/CamTag/"+filename));
        startActivity(email);
    }
    public void shareFiles(ArrayList<String> filenames){
        Intent email = new Intent(Intent.ACTION_SEND_MULTIPLE);
        email.setType("image/gif");
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{"to@example.com"});
        email.putExtra(Intent.EXTRA_SUBJECT, "");
        email.putExtra(Intent.EXTRA_TEXT,"Sent From CamTag");
        ArrayList<Uri> paths = new ArrayList<Uri>();
        for (String file : filenames)
        {
            File temp = new File(Environment.getExternalStorageDirectory().toString()+"/CamTag/" + file);
            Uri u = Uri.fromFile(temp);
            paths.add(u);
        }
        email.putParcelableArrayListExtra(Intent.EXTRA_STREAM, paths);
         startActivity(email);
    }
}