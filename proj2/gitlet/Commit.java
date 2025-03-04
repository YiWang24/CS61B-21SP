package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.plainFilenamesIn;


/**
 * Represents a gitlet commit object.
 * <p>
 * does at a high level.
 *
 * @author Yi Wang
 */
public class Commit implements Serializable, Dumpable {
    /**
     *
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /**
     * The message of this Commit.
     */
    private static final long serialVersionUID = 1L;
    private String commitId;
    private String message;
    private Date timestamp;
    private String parent;
    private String mergeParent;
    private Map<String, String> blobs = new HashMap<>();


    public Commit() {
        message = "initial commit";
        parent = null;
        timestamp = new Date(0);
        commitId = Utils.sha1(Utils.serialize(this));
        saveCommit();
    }


    public Commit(String message, String parent, String mergeParent, Map<String, String> blobs) {
        this.message = message;
        this.parent = parent;
        this.mergeParent = mergeParent;
        this.timestamp = new Date();
        this.blobs = blobs;
        commitId = Utils.sha1(Utils.serialize(this));
        saveCommit();
    }

    public String getCommitId() {
        return commitId;
    }

    public String getMessage() {
        return message;
    }

    public String getParent() {
        return parent;
    }

    public String getMergeParent() {
        return mergeParent;
    }

    public Map<String, String> getBlobs() {
        return blobs;
    }

    public String getBlobId(String blobName) {
        if(!blobs.containsKey(blobName)) {
            return null;
        }
        return blobs.get(blobName);
    }

    public List<String> getBlobsList() {
        Set<String> blobsSet = blobs.keySet();
        List<String> blobsList = new ArrayList<>(blobsSet);
        Collections.sort(blobsList);
        return blobsList;
    }

    public static Commit load(String commitId) {
        File commitFile = new File(Repository.COMMIT_DIR, commitId);
        if (!commitFile.exists()) {
            return null;
        }
        return Utils.readObject(commitFile, Commit.class);
    }

    public static Set<Commit> getAllCommits() {
        Set<Commit> commits = new HashSet<>();
        List<String> commitList = plainFilenamesIn(Repository.COMMIT_DIR);
        if (commitList == null) {
            return null;
        }
        for (String commitId : commitList) {
            Commit commit = Commit.load(commitId);
            commits.add(commit);
        }
        return commits;
    }

    public static List<String> getCommitList(String commitId) {
        List<String> commitList = new ArrayList<>();
        Commit currentCommit = load(commitId);
        while (currentCommit != null) {
            commitList.add(currentCommit.getCommitId());
            if (currentCommit.getParent() == null) {
                break;
            }
            currentCommit = load(currentCommit.getParent());
        }
        return commitList;
    }

    public static String getParent(String commitId) {
        Commit commit = load(commitId);
        if (commit == null) {
            return null;
        }
        return commit.getParent();
    }

    public static String findCommitByPrefix(String prefix) {
        Set<Commit> commits = getAllCommits();
        if (commits == null) {
            return null;
        }
        for (Commit commit : commits) {
            if (commit.getCommitId().startsWith(prefix)) {
                return commit.getCommitId();
            }
        }
        return null;
    }


    public static void removeBlobsFromCommit(Map<String, String> blobs, Set<String> filesToRemove) {
        for (String fileToRemove : filesToRemove) {
            blobs.remove(fileToRemove);
        }
    }

    public Boolean isBlobExists(String filename, String blobId) {
        return blobs.containsKey(filename) && blobs.get(filename).equals(blobId);
    }

    public Boolean isBlobExists(String filename) {
        return blobs.containsKey(filename);
    }

    @Override
    public String toString() {
        String s = "===" + "\n";
        s += "commit " + commitId + "\n";
        if (parent != null && mergeParent != null) {
            String parentShort = parent.substring(0, 7);
            String mergeParentShort = mergeParent.substring(0, 7);
            s += "Merge: " + parentShort + " " + mergeParentShort + "\n";
        }
        // "Date" line
        s += "Date: " + getCurrentTimestamp(timestamp) + "\n";
        // message line
        s += message + "\n";

        return s;
    }


    @Override
    public void dump() {
        System.out.println("gitlet log of this commit:");
        System.out.println(this);
        System.out.println("map file name to blob:");
        for (String fileName : blobs.keySet()) {
            System.out.printf("file name: %s to blob ID: %s%n", fileName, blobs.get(fileName));
        }
        System.out.println("parent1: " + parent);
    }


    private static String getCurrentTimestamp(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z");
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        return simpleDateFormat.format(date);
    }

    private void saveCommit() {
        File commitFile = new File(Repository.COMMIT_DIR, commitId);
        Utils.writeObject(commitFile, this);
    }


}
