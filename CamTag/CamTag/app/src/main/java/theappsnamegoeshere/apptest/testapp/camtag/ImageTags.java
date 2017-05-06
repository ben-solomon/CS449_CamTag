package theappsnamegoeshere.apptest.testapp.camtag;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben on 2/24/2017.
 */

public class ImageTags {
    List<String> tagslist;
    String filename;
    public void ImageTags(){
        this.filename = "nullfname";
        this.tagslist = new ArrayList<String>(){};
    }
    public void ImageTags(String f){
        this.filename = f;
        this.tagslist = new ArrayList<String>(){};
    }
    public void ImageTags(String f, List<String> l){
        this.filename = f;
        this.tagslist = l;
    }

    public void setImage(String fname){
        this.filename = fname;
    }
    public void setList(List<String> f){
        this.tagslist = f;
    }
    public void Tagadd(String a){
        tagslist.add(a);
    }
    public List<String> listoftags()
    {
        return tagslist;
    }
    public String getimagename()
    {
        return filename;
    }
    public void clearall(){
        this.filename="nullfname";
        this.tagslist = new ArrayList<String>(){};
    }


}
