package theappsnamegoeshere.apptest.testapp.camtag;


import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ImageView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.widget.TextView;
import java.io.File;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    // list view for selected tags
    //gridview for tags to choose from
    ListView lv;
    GridView gv;

    //holds items to be displayed in grid and list views
    List<String> visibleTags,selectedTags;
    ArrayAdapter<String> selectedListAdapter,visibleTagsAdapter;//selected tags, all tags
    ImageTags lastTaken = new ImageTags(); //temporary container for image file and its assigned tags

    // database handler
    DBhandler db;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // clear the container
        lastTaken.clearall();
        //launch camera
        dispatchTakePictureIntent();

        TextView lbl = (TextView) findViewById(R.id.selectedLabel);
        lbl.setVisibility(View.INVISIBLE);

        ListView list = (ListView) findViewById(R.id.selectedList);
        list.setVisibility(View.INVISIBLE);

        // instantiate database handler
        db = new DBhandler(this);

        // initial populating of selected tags - empty at first and handled dynamically based on user input
        lv = (ListView) findViewById(R.id.selectedList);
        selectedTags = new ArrayList<String>();
        selectedListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, selectedTags);
        lv.setAdapter(selectedListAdapter);

        // set gridview to allow long click for delete tag function
        // populate gridview on display
        gv = (GridView) findViewById(R.id.gvTag);
        gv.setLongClickable(true);
        visibleTags = db.getTagsList();
        visibleTagsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, visibleTags);
        gv.setAdapter(visibleTagsAdapter);

// GRIDVIEW long click function
        gv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView s = (TextView) view;
                final String str = s.getText().toString();
                // alert box on long click to allow user to choose yes or no to delete
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE: // yes clicked
                                // delete tag from database
                                db.deleteTag(str);
                                // refresh gridview to show updated tags from db
                                visibleTagsAdapter.remove(str);
                                visibleTagsAdapter.notifyDataSetChanged();
                                gv.setAdapter(visibleTagsAdapter);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE: // no clicked - closes dialog
                                //No button clicked
                                break;
                        }
                    }
                };
                // actually show alert dialog (defined above)
                AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this);
                builder2.setMessage("Delete " + str + " from tags?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
                return true;
            }
        });

// LIST VIEW single click function
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView v = (TextView) view;
                String a = v.getText().toString();
                // when an item is tapped in the listview (selected tags) it is removed and placed back into the gridview pool of tags
                selectedTags.remove(a);
                selectedListAdapter =  new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, selectedTags);
                lv.setAdapter(selectedListAdapter);
                // adding the tag back to the displayed gridiew
                visibleTags.clear();
                visibleTags.addAll(db.getTagsList());
                for (String j : selectedTags){
                    visibleTags.remove(j);
                }
                visibleTagsAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, visibleTags);
                gv.setAdapter(visibleTagsAdapter);


            }
        });

// GRIDVIEW single click function - this is adding a tag to the selected tags list
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                TextView v = (TextView) view;

                String a = v.getText().toString();
                ImageButton b = (ImageButton) findViewById(R.id.saveButton);
                if(!b.isShown()){
                    return;
                }
                if (!selectedTags.contains(a))
                {
                    // else, we add the tag to the selected listview
                    selectedTags.add(a);
                    selectedListAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, selectedTags);
                    lv.setAdapter(selectedListAdapter);
                    // and remove it from the choice of tags in the gridview
                    visibleTags.remove(a);
                    visibleTagsAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, visibleTags);
                    gv.setAdapter(visibleTagsAdapter);


                    if (visibleTags.contains(a) == true)
                    {
                        Toast.makeText(view.getContext(), "still here", Toast.LENGTH_LONG).show();
                    }
                }else
                {
                    Toast.makeText(view.getContext(), "tag already selected!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    // temporary file created, if user does not save it is deleted. else it is kept.
    private File createTemporaryFile(String part, String ext) throws Exception {
        // set path to SD card (physical or emulated)
        File tempDir = Environment.getExternalStorageDirectory();
        tempDir = new File(tempDir.getAbsolutePath() + "/CamTag/");
        // check if path exists, and if not create directory
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        return File.createTempFile(part, ext, tempDir);
    }
    // uri is used to keep track of image file
    private Uri picuri;

    // when a picture is take, a random filename is created
    public void dispatchTakePictureIntent() {
        // check if directory "CamTag" exists on SD card, if not creates it
        File folder = new File(Environment.getExternalStorageDirectory().toString()+"/CamTag");
        if (!folder.exists())
        {
            folder.mkdirs();
        }
        // random numbers to generate filenames
        String rn1,rn2,rn3;
        Random fn1 = new Random();
        int n = 1000;
        int n2 = 1000;
        int n3 = 1000;
        n = fn1.nextInt(n);
        rn1 = Integer.toString(n);
        n2 = fn1.nextInt(n2);
        rn2 = Integer.toString(n2);
        n3 = fn1.nextInt(n3);
        rn3 = Integer.toString(n3);
        String fname = "Img-" + rn1+rn2+rn3 +".jpg";

        // set global Image/Tag container object to the current filename (used to create DB links)
        lastTaken.setImage(fname);
        // image file
        File pic = new File(Environment.getExternalStorageDirectory().toString() + "/CamTag");

        try {
            pic = new File(Environment.getExternalStorageDirectory().toString() + "/CamTag", fname);
            pic.delete();

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error saving file! Try again", Toast.LENGTH_LONG);
        }
        //get path of file created
        picuri = Uri.fromFile(pic);
        // this allows the Camera intent to save its captured bitmap to the file created above
        Intent a = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // save captured bitmap to file
        a.putExtra(MediaStore.EXTRA_OUTPUT, picuri); // sets the image taken to be the file created at the given path
        startActivityForResult(a, 8);

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        ContentResolver aa = this.getContentResolver();
        switch (requestCode) {
            case 8:


                if (resultCode == RESULT_OK) {
                    // if capture returns no errors, same code as above (set image preview), just a 2nd place to catch this event

                    ImageView v = (ImageView) findViewById(R.id.picMain);
                    v.setImageURI(picuri);

// rotates preivew image if necessary (4:3 vs 16:9)
                    Bitmap b = ((BitmapDrawable)v.getDrawable()).getBitmap();
                    double w = b.getWidth();
                    double h = b.getHeight();
                    double f = w/h;
                    // need to add code to add width and height to image object
                    String jj = String.valueOf(f);
                    if (f > 1.5)
                    {
                        v.setRotation(90);
                        v.isShown();
                    }

                    TextView lbl = (TextView) findViewById(R.id.selectedLabel);
                    lbl.setVisibility(View.VISIBLE);

                    ListView list = (ListView) findViewById(R.id.selectedList);
                    list.setVisibility(View.VISIBLE);

                    //after image is taken, the save button becomes active
                    ImageButton bt = (ImageButton) findViewById(R.id.saveButton);
                    bt.setVisibility(View.VISIBLE);
                }
                else{
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE: // Gallery
                                    toGallery();
                                    finish();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE: // Exit app
                                    //No button clicked
                                    finish();
                                    break;
                            }
                        }
                    };
                    // actually show alert dialog (defined above)
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this);
                    builder2.setCancelable(false);
                    builder2.setMessage("").setPositiveButton("Gallery", dialogClickListener).setNegativeButton("Exit", dialogClickListener).show();

                }
                break;

        }
    }
    // database links are created (for gallery search) and view is reset
    public void saved(View v){
        // all items in users selected tags list get added to container object with picture
        lastTaken.setList(selectedTags);
        db.addImage(lastTaken.getimagename());
        // create database links
        db.addLinks(lastTaken);
        //clear container for next image
        lastTaken.clearall();

        ImageView vw = (ImageView) findViewById(R.id.picMain);
        vw.setRotation(-90);
        vw.setImageResource(R.drawable.preview1);

        selectedTags.clear();
        selectedListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,selectedTags);
        lv.setAdapter(selectedListAdapter);

        visibleTags.clear();
        visibleTags = db.getTagsList();
        visibleTagsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,visibleTags);
        gv.setAdapter(visibleTagsAdapter);

        ImageButton b = (ImageButton) findViewById(R.id.saveButton);
        b.setVisibility(View.GONE);

        TextView lbl = (TextView) findViewById(R.id.selectedLabel);
        lbl.setVisibility(View.INVISIBLE);

        ListView list = (ListView) findViewById(R.id.selectedList);
        list.setVisibility(View.INVISIBLE);
        toGallery();

    }

    public void dispatchTakeVideoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 1);

        }
    }

    //actually clears entire DB, only used in testing.
    public void deleteTags(View v)
    {
        db.clearTable("Tags");
        db.clearTable("Images");
        db.clearTable("Links");
    }

    // Adding a tag
    public void tagInsert(View v){
        EditText t = (EditText) findViewById(R.id.addtagbox);
        String str = t.getText().toString();
        // get text from text box
        str = str.trim();
        // check if empty (not currently working for some reason, allows empty string to be added when it shouldnt
        if(TextUtils.isEmpty(str)){
            t.clearFocus();
            return;
        }
        // check if tag already exists in database
        if (!db.containsTag(str)){
            // if not tag is added to DB
            db.addTag(new Tags(str));

            // gets current list of selected tags (as a new tag can be added whenever)
            // To prevent selected tags from showing up in the gridview again on databind,
            // we check populate the gridview with all tags from the DB (after insert) and remove
            // anything already being displayed in the selected tags listview
            List<String> x = db.getTagsList();
            for (String j: selectedTags){
                x.remove(j);
            }

            visibleTagsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, x);
            gv.setAdapter(visibleTagsAdapter);
            // bind data and clear text box text
            t.setText("");
        }
        else
        {
            Toast.makeText(getApplicationContext(),"tag already exists!",Toast.LENGTH_SHORT).show();
        }
    }
    // potentially unused, but will maintain view between switching activities
    public void onResume(View v){
        super.onResume();
        v.requestLayout();
    }
    // calls camera intent
    protected void cam(View v) {
        dispatchTakePictureIntent();
        ImageButton sb = (ImageButton) findViewById(R.id.saveButton);
        if(sb.isShown())
        {

            File temp = new File(Environment.getExternalStorageDirectory().toString()+"/CamTag/" + lastTaken.getimagename());

            if (temp.exists())
            {
                temp.delete();
            }
            lastTaken.clearall();
        }
    }
    // calls video intent, currently unused - video not being supported atm
    protected void vid(View v) {
        dispatchTakeVideoIntent();

    }
    public void toGallery(){
        ImageButton sb = (ImageButton) findViewById(R.id.saveButton);
        ImageView pv = (ImageView) findViewById(R.id.picMain);
        GridView tv = (GridView) findViewById(R.id.gvTag);
        ListView sl = (ListView)findViewById(R.id.selectedList);
        TextView at = (TextView) findViewById(R.id.selectedLabel);
        if(sb.isShown())
        {
            File temp = new File(Environment.getExternalStorageDirectory().toString()+"/CamTag/" + lastTaken.getimagename());
            if (temp.exists())
            {
                temp.delete();
            }
            lastTaken.clearall();
            pv.setImageResource(R.drawable.preview1);
            pv.setVisibility(View.GONE);
            visibleTagsAdapter.clear();
            visibleTagsAdapter.addAll(db.getTagsList());
            selectedListAdapter.clear();
            sl.setVisibility(View.GONE);
            at.setVisibility(View.GONE);

        }
        Intent myIntent = new Intent(MainActivity.this,GalleryActivity.class);
        MainActivity.this.startActivity(myIntent);
        finish();

    }
    public void toGallery(View v){
        ImageButton sb = (ImageButton) findViewById(R.id.saveButton);
        ImageView pv = (ImageView) findViewById(R.id.picMain);
        GridView tv = (GridView) findViewById(R.id.gvTag);
        ListView sl = (ListView)findViewById(R.id.selectedList);
        TextView at = (TextView) findViewById(R.id.selectedLabel);
        if(sb.isShown())
        {
            File temp = new File(Environment.getExternalStorageDirectory().toString()+"/CamTag/" + lastTaken.getimagename());
            if (temp.exists())
            {
                temp.delete();
            }
            lastTaken.clearall();
            pv.setImageResource(R.drawable.preview1);
            pv.setVisibility(View.GONE);
            visibleTagsAdapter.clear();
            visibleTagsAdapter.addAll(db.getTagsList());
            selectedListAdapter.clear();
            sl.setVisibility(View.GONE);
            at.setVisibility(View.GONE);

        }
        Intent myIntent = new Intent(MainActivity.this,GalleryActivity.class);
        MainActivity.this.startActivity(myIntent);
    }

}


