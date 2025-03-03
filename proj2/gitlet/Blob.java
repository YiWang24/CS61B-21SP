package gitlet;

import java.io.File;

/**
 * @author WY
 * @version 1.0
 **/

public class Blob {
    private static final long serialVersionUID = 1L;
    private String content;
    private String id;

    public Blob(File file) {
        this.content = Utils.readContentsAsString(file);
        this.id = Utils.sha1(content);
    }


    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void saveBlob(){
        File blobDir = Repository.BLOB_DIR;
        File blobFileDir = new File(blobDir, id.substring(0, 2));
        if (!blobFileDir.exists()) {
            blobFileDir.mkdir();
        }
        File blobFile = new File(blobFileDir, id.substring(2));
        if(!blobFile.exists()){
            Utils.writeContents(blobFile, content);
        }
    }

    public static void removeBlob(String blobId){
        File blobDir = Repository.BLOB_DIR;
        File blobFile = getBlob(blobId);
        if(blobFile.exists()){
            blobFile.delete();
        }
        if(blobDir.isDirectory() && blobDir.list().length == 0){
            blobDir.delete();
        }
    }

    public static File getBlob(String blobId){
        File blobDir = Repository.BLOB_DIR;
        File blobFileDir = new File(blobDir, blobId.substring(0, 2));
        if (!blobFileDir.exists()) {
            return null;
        }
        return new File(blobFileDir, blobId.substring(2));
    }









}
