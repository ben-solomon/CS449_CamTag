package theappsnamegoeshere.apptest.testapp.camtag;

import java.io.File;

/**
 * Created by Ben on 2/24/2017.
 */

public class Img {


        String _imgFileName;
        File _imgFile;
        String _width;
        String _height;

        public Img() {

        }

        public Img(int _id, String _imageFileName, File _imgFile){
            this._imgFile = _imgFile;
            this._imgFileName = _imageFileName;
        }

        public String get_imgFileName() {
            return _imgFileName;
        }

        public File get_imgFile() {
            return _imgFile;
        }

        public void set_imgFileName(String fname) {
            this._imgFileName = fname;
        }

        public void set_imgFile(File f) {
            this._imgFile = f;
        }

}
