package theappsnamegoeshere.apptest.testapp.camtag;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class editTags extends AppCompatActivity {
    List<String> poolTags,selectedTags;
    ArrayAdapter<String> assignedtagsAdapter,tagsPoolAdapter;
    ImageView iv;
    ListView lv;
    GridView gv;
    Button save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SQLiteDatabase databasex;
        final DBhandler dbx = new DBhandler(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tags);
        final String image = getIntent().getStringExtra("filename");
        iv  = (ImageView) findViewById(R.id.imgPrev);
        lv = (ListView) findViewById(R.id.assignedTags);
        gv  = (GridView) findViewById(R.id.tagsPool);
        save = (Button) findViewById(R.id.updateButton);
        File temp = new File(Environment.getExternalStorageDirectory().toString() + "/CamTag/" + image);
        if (!temp.exists()) {
            Toast.makeText(getApplicationContext(), "next load fail", Toast.LENGTH_LONG).show();
        } else {
            Picasso.with(editTags.this).load(temp).into(iv);

        }
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageTags holder = new ImageTags();
                holder.setImage(image);
                holder.setList(selectedTags);
                dbx.deleteImage(image);
                dbx.deleteImageLinks(image);
                dbx.addImage(image);
                dbx.addLinks(holder);
                Intent intent = new Intent(editTags.this,GalleryActivity.class);
                startActivity(intent);
            }
        });
        selectedTags = dbx.getImageTags(image);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView v = (TextView) view;
                String a = v.getText().toString();
                // when an item is tapped in the listview (selected tags) it is removed and placed back into the gridview pool of tags
                selectedTags.remove(a);
                assignedtagsAdapter =  new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, selectedTags);
                lv.setAdapter(assignedtagsAdapter);
                // adding the tag back to the displayed gridiew
                poolTags.clear();
                poolTags.addAll(dbx.getTagsList());
                for (String j : selectedTags){
                    poolTags.remove(j);
                }
                tagsPoolAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, poolTags);
                gv.setAdapter(tagsPoolAdapter);


            }
        });
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                TextView v = (TextView) view;

                String a = v.getText().toString();
                if (!selectedTags.contains(a))
                {
                    // else, we add the tag to the selected listview
                    selectedTags.add(a);
                    assignedtagsAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, selectedTags);
                    lv.setAdapter(assignedtagsAdapter);
                    // and remove it from the choice of tags in the gridview
                    poolTags.remove(a);
                    tagsPoolAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, poolTags);
                    gv.setAdapter(tagsPoolAdapter);


                    if (poolTags.contains(a) == true)
                    {
                        Toast.makeText(view.getContext(), "still here", Toast.LENGTH_LONG).show();
                    }
                }else
                {
                    Toast.makeText(view.getContext(), "tag already selected!", Toast.LENGTH_LONG).show();
                }
            }
        });

        assignedtagsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, selectedTags);
        lv.setAdapter(assignedtagsAdapter);
        poolTags = dbx.getTagsList();
        for(String j:selectedTags){
            try{poolTags.remove(j);}
            catch(Exception e){
                Toast.makeText(getApplicationContext(), "selection error", Toast.LENGTH_SHORT).show();
            }
        }
        tagsPoolAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, poolTags);
        gv.setAdapter(tagsPoolAdapter);

    }
}
