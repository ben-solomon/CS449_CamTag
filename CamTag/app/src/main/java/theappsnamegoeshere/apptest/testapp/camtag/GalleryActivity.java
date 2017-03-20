package theappsnamegoeshere.apptest.testapp.camtag;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SQLiteDatabase databasex;
        final DBhandler dbx = new DBhandler(this);
        databasex = dbx.getReadableDatabase();
        List<String> tags = dbx.getTagsList();
        ArrayAdapter<String> tagsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tags);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        GridView galleryGV = (GridView) findViewById(R.id.galleryGrid);
        GridView tagGV = (GridView) findViewById(R.id.galleryTagsGrid);
        CustomGrid cgAdapter = new CustomGrid(this, dbx.getImageList());
        galleryGV.setAdapter(cgAdapter);
        tagGV.setAdapter(tagsAdapter);
        galleryGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
         public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent fullScreen = new Intent(GalleryActivity.this, FullScreenActivity.class);
                fullScreen.putExtra("pos",i);
                startActivity(fullScreen);

        }
        });
        TextWatcher inputTextWatcher = new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               // Toast.makeText(getApplicationContext(),s.toString(),Toast.LENGTH_SHORT).show();
            }
        };
        EditText et = (EditText) findViewById(R.id.filter);
        et.addTextChangedListener(inputTextWatcher);
    }
    public class Holder{
        File fileHolder;
        void Holder(File f){
            fileHolder = f;
        }

    }
    public class CustomGrid extends BaseAdapter {

        private Context mContext;

        private List<String> galleryList;

        public void fillList(List<String> ls) {
            this.galleryList = ls;
        }

        public CustomGrid(Context c) {
            this.mContext = c;
            this.galleryList = null;
        }

        public CustomGrid(Context c, List<String> fnames) {
            this.mContext = c;
            this.galleryList = fnames;
        }

        @Override
        public int getCount() {
            return galleryList.size();
        }

        @Override
        public Object getItem(int i) {
            return galleryList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }


        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ImageView imageView;


            if (view == null) { // if it's not recycled, initialize some
                // attributes
                imageView = new ImageView(mContext);
                imageView.setTag(i);
                //imageView.setLayoutParams(new GridView.LayoutParams(220, 220));
                //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                //imageView.setPadding(5, 5, 5, 5);
            } else {
                imageView = (ImageView) view;
                imageView.setTag(i);
            }

                File temp = new File(Environment.getExternalStorageDirectory().toString() + "/CamTag/" + galleryList.get(i)+".jpg");
                if (!temp.exists())
                {
                    Toast.makeText(getApplicationContext(),"load fail", Toast.LENGTH_LONG).show();
                }
                else
                {
                   // Toast.makeText(getApplicationContext(),"file found", Toast.LENGTH_LONG).show();
                }
                Picasso.with(getApplicationContext())
                        .load(temp).placeholder(R.drawable.load).centerCrop().resize(150,150).into(imageView);


            return imageView;
        }
    }
}
