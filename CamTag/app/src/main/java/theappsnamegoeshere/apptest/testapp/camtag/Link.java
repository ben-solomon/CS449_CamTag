package theappsnamegoeshere.apptest.testapp.camtag;

/**
 * Created by Ben on 2/24/2017.
 */

public class Link {
    Img Image;
    Tags Tag;

    public void Link(){};
    public void Link(Img a, Tags b){
        this.Image = a;
        this.Tag = b;
    }

    public void Link(Img a)
    {
        this.Image = a;
        this.Tag = new Tags(0,"show_all");
    }
    public Tags getLinkTag(){return Tag;}
    public Img getLinkImage(){return Image;}
}
