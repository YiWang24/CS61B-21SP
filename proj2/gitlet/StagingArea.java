package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;


/**
 * Represents the GitLet Staging Area.
 * Stores files that are staged for addition or removal.
 *
 * @author WY
 * @version 1.0
 **/

public class StagingArea implements Serializable, Dumpable {
    private static final long serialVersionUID = 1L;

    private Map<String, String> stagedForAddition;
    private Set<String> stagedForRemoval;

    public StagingArea() {
        stagedForAddition = new HashMap<>();
        stagedForRemoval = new HashSet<>();
    }

    /**
     * load or create a staging area if not exist from disk.
     *
     * @return StagingArea
     */
    public static StagingArea loadStagingArea() {
        File stagingArea = new File(Repository.STAGING_DIR, "stagingArea");

        if (stagingArea.exists()) {
            return Utils.readObject(stagingArea, StagingArea.class);
        } else {
            StagingArea newStagingArea = new StagingArea();
            Utils.writeObject(stagingArea, newStagingArea);
            return newStagingArea;
        }
    }


    /**
     * Stage a file for addition
     *
     * @param fileName the file to stage
     * @param fileId   the file hash id
     */
    public void stageFile(String fileName, String fileId) {
        stagedForAddition.put(fileName, fileId);
        stagedForRemoval.remove(fileName);
        save();
    }

    /**
     * Stage a file for removal.
     *
     * @param fileName the file for removal
     */
    public void stageRemoval(String fileName) {
        if (!stagedForRemoval.contains(fileName)) {
            stagedForRemoval.add(fileName);
            stagedForAddition.remove(fileName);
            save();
        }
    }

    /**
     * clear the staging area.
     */
    public void clear() {
        stagedForAddition.clear();
        stagedForRemoval.clear();
        save();
    }


    public Map<String, String> getStagedForAddition() {
        return stagedForAddition;
    }

    public Set<String> getStagedForRemoval() {
        return stagedForRemoval;
    }

    /**
     * saveCommit the staging area to disk.
     */
    public void save() {
        File stagingArea = new File(Repository.STAGING_DIR, "stagingArea");
        Utils.writeObject(stagingArea, this);
    }

    /**
     * print the staging area state for debugging.
     */
    @Override
    public void dump() {
        System.out.println("=== Staging Area ===");
        System.out.println("Staged for Addition:");
        for (Map.Entry<String, String> entry : stagedForAddition.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
        System.out.println("Staged for Removal:");
        for (String file : stagedForRemoval) {
            System.out.println(file);
        }
    }


}
