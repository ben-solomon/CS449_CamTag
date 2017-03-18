package theappsnamegoeshere.apptest.testapp.camtag;

/**
 * Created by Ben on 2/20/2017.
 */

public class Tags {
    int _id;
    String _tagName;

    public Tags() {
    this._id = 0;this._tagName = "nullx";
    }
    public Tags (String tagname)
    {
        this._tagName = tagname;
    }

    public Tags(int _id, String _tagName){
        this._id = _id;
        this._tagName = _tagName;
    }

    public int get_id() {
        return _id;
    }

    public String get_tagName() {
        return _tagName;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void set_tagName(String _tagName) {
        this._tagName = _tagName;
    }
}
