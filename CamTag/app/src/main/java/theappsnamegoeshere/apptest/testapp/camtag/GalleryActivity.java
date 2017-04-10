package theappsnamegoeshere.apptest.testapp.camtag;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends Activity{
    SQLiteDatabase databasex;
    final DBhandler dbx = new DBhandler(this);
    List<String> shownImages;
    List<String> selectedTags = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        //Toast.makeText(this,myapp.getName(),Toast.LENGTH_SHORT);
        databasex = dbx.getReadableDatabase();
        // tags to filter gallery by
        shownImages = dbx.getImageList();
        //ArrayAdapter<String> tagsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tags);

        // gridview for gallery images
        final GridView galleryGV = (GridView) findViewById(R.id.galleryGrid);
        shownImages = dbx.getImageList();
        // filter the tags list if desired (search for specific tag in a large tag list)
        EditText et = (EditText) findViewById(R.id.filterSearch);
        final GridView tagGV = (GridView) findViewById(R.id.galleryTagsGrid);
        EditText text = (EditText)findViewById(R.id.filterSearch);
        final FloatingActionButton fa = (FloatingActionButton) findViewById(R.id.searchButton);
        text.setVisibility(View.VISIBLE);
        text.bringToFront();
        text.setHint("search tags...");
        // custom grid adapter to show imageviews in gridview
        final CustomGrid cgAdapter = new CustomGrid(this, shownImages);
        galleryGV.setAdapter(cgAdapter);
        final CustomGridTags cgAdapterTags = new CustomGridTags(this,dbx.getTagsList());
        tagGV.setVisibility(View.VISIBLE);
        tagGV.setAdapter(cgAdapterTags);
        tagGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(view.getContext(),"clicked",Toast.LENGTH_SHORT).show();
                TextView tv = (TextView) view.findViewById(R.id.textViewtags);
                CheckBox cb = (CheckBox) view.findViewById(R.id.checkBoxtags);
                Toast.makeText(view.getContext(),tv.getText().toString(),Toast.LENGTH_SHORT).show();
               if (cb.isChecked()){
                    //Toast.makeText(view.getContext(),"incheck",Toast.LENGTH_LONG).show();
                    cb.setChecked(false);
                    try{selectedTags.remove(tv.getText().toString());}
                    catch (Exception e){
                        Toast.makeText(view.getContext(),"tag does not exists",Toast.LENGTH_LONG).show();
                    }

                }else
                {
                   // Toast.makeText(view.getContext(),"incheck",Toast.LENGTH_LONG).show();
                    cb.setChecked(true);
                    selectedTags.add(tv.getText().toString());
                }
                shownImages = dbx.getContainsImageList(selectedTags);
                for (String j:shownImages){
                    Toast.makeText(view.getContext(),j,Toast.LENGTH_SHORT).show();
                }
                cgAdapter.fillList(shownImages);
                galleryGV.setAdapter(cgAdapter);
                cgAdapter.notifyDataSetChanged();


            }

        });


        // search is hidden until search button is pressed-- gallery starts out fullscreen
       // tagGV.setVisibility(View.GONE);

        galleryGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            // working on getting the image index passed to fullscreen activity
         public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Intent fullScreen = new Intent(GalleryActivity.this, FullScreenActivity.class);
                String fname = view.getTag(R.id.gvitem).toString();
                if (fname != null)
                {
                    Toast.makeText(view.getContext(),view.getTag(R.id.gvitem).toString(), Toast.LENGTH_SHORT).show();
                }

                try {
                   fullScreen.putExtra("curimage",fname);
                    ArrayList<String> temp = new ArrayList<String> ();
                    for (String j:shownImages)
                    {
                        temp.add(j);
                    }
                   fullScreen.putStringArrayListExtra("curgallery",temp);
                    startActivity(fullScreen);
                }catch (Exception e){
                    Log.i("msg",e.getMessage());
                    Log.i("fail","FAIL ******** FAIL ************* FAIL *************");
                    Toast.makeText(view.getContext(), "fullscreen error", Toast.LENGTH_SHORT).show();
                }

        }
        });
        // filters tags as you type in the tag search
        TextWatcher inputTextWatcher = new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               cgAdapterTags.fillList(dbx.getCustomTagsList(s.toString()));
                tagGV.setAdapter(cgAdapterTags);
            }
        };

        et.addTextChangedListener(inputTextWatcher);
        et.setVisibility(View.INVISIBLE);
    }
    // toggle to hide and show search functions
    public void showSearch(View v){
        EditText et = (EditText) findViewById(R.id.filterSearch);
        GridView tagGV = (GridView) findViewById(R.id.galleryTagsGrid);
        GridView galGV = (GridView) findViewById(R.id.galleryGrid);
        FloatingActionButton fl = (FloatingActionButton) findViewById(R.id.searchButton);

        if (tagGV.isShown())
        {
            shownImages = dbx.getImageList();
            galGV.setAdapter(new CustomGrid(this,shownImages));
            tagGV.setVisibility(View.GONE);
            et.setVisibility(View.GONE);
            fl.setImageResource(android.R.drawable.ic_menu_search);
        }
        else
        {
            shownImages.clear();
            tagGV.setVisibility(View.VISIBLE);
            et.setVisibility(View.VISIBLE);
            fl.setImageResource(android.R.drawable.arrow_up_float);
        }
    }
    public class holder2 {
        ImageView iv;
        holder2(View v)
        {
            iv = (ImageView) v;
        }
    }
    public class CustomGridTags extends BaseAdapter{
        private Context mContext;
        private List<String> tagsList;
        List<String> filters;
        TextView text;
        CheckBox check;
        public List<String> getCurrentFilterList()
        {
            return this.filters;
        }
        public void fillList(List<String> ls){this.tagsList = ls;}
        public void addRemoveFilter(String s, int i){
            if (i == 0) //remove
            {
                filters.remove(s);
            }
            else if (i == 1) //add
            {
                filters.add(s);
            }
        }
        public CustomGridTags(Context c){
            this.mContext = c;
            this.tagsList = null;

        }
        public CustomGridTags(Context c, List<String> ls){
            this.mContext = c;
            this.tagsList = ls;
        }
        @Override
        public int getCount() {
            return tagsList.size();
        }
        @Override
        public Object getItem(int i) {
            return tagsList.get(i);
        }
        @Override
        public long getItemId(int i) {
            return i;
        }
        private class holder {
            TextView text;
            CheckBox check;
        }
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
             holder holder = null;

            if (view == null)
            {
                holder = new holder();
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.tags_gallery_adapter, null);

                holder.text = (TextView) view.findViewById(R.id.textViewtags);
                holder.check = (CheckBox) view.findViewById(R.id.checkBoxtags);
                view.setTag(holder);
            }
            else
            {
                holder = (holder) view.getTag();
            }
            final holder thold = holder;

            String a = this.tagsList.get(i).toString();
            holder.text.setText(a);
            view.setTag(R.id.tag1,a);
            return view;
        }
    }
    public class CustomGrid extends BaseAdapter {
        private Context mContext;
        private List<String> galleryList;

        public void fillList(List<String> ls) {
            this.galleryList.clear();
            for (String j: ls){
                this.galleryList.add(j);
            }
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
            holder2 holder2 = null;

            ImageView imageView;
            if (view == null) { // if it's not recycled, initialize some
                holder2 = new holder2(view);
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.gallery_adapter_layout, null);
                holder2.iv = (ImageView) view.findViewById(R.id.imagePrev);
                view.setTag(R.id.holdergv,holder2);
            } else {
                holder2 = (holder2) view.getTag(R.id.holdergv);
            }

                // set path to gallery location
                String path = Environment.getExternalStorageDirectory().toString() + "/CamTag/" + galleryList.get(i);
                File temp = new File(path);
                if (!temp.exists())
                {
                    Toast.makeText(getApplicationContext(),"load fail", Toast.LENGTH_LONG).show();
                }
                // load image into imageview
                Picasso.with(getApplicationContext())
                        .load(temp).placeholder(R.drawable.load).centerCrop().resize(150,150).into(holder2.iv);
           view.setTag(R.id.gvitem,galleryList.get(i));
            return view;
        }
    }
    @Override
    public void onBackPressed() {
        Intent setIntent = new Intent(GalleryActivity.this, MainActivity.class);
        startActivity(setIntent);
        finish();
    }
}
