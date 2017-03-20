package theappsnamegoeshere.apptest.testapp.camtag;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class FullScreenActivity extends GalleryActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);
        ImageView iv = (ImageView) findViewById(R.id.imageView2);

        int index = getIntent().getIntExtra("pos",-1);
        if (index != -1){
            Picasso.with(FullScreenActivity.this).load(R.drawable.bg1).into(iv);
        }
    }

}
