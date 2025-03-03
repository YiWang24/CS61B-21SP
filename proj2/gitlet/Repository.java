package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;


/**
 * Represents a gitlet repository.
 * <p>
 * does at a high level.
 *
 * @author Yi Wang
 */
public class Repository implements Serializable {
    /**
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File COMMIT_DIR = new File(GITLET_DIR, "commits");
    public static final File BRANCH_DIR = new File(GITLET_DIR, "branches");
    public static final File BLOB_DIR = new File(GITLET_DIR, "blobs");
    public static final File STAGING_DIR = new File(GITLET_DIR, "stagingArea");
    public static final File HEAD = new File(GITLET_DIR, "HEAD");

    public void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system " +
                    "already exists in the current directory.");
            System.exit(0);
        }
        // create directory for GitLet
        GITLET_DIR.mkdir();
        COMMIT_DIR.mkdir();
        BRANCH_DIR.mkdir();
        STAGING_DIR.mkdir();
        BLOB_DIR.mkdir();
        // create initial commit
        Commit initCommit = new Commit();
        initCommit.saveCommit();
        //create default branch
        String branch = "master";
        saveBranch(branch, initCommit.getCommitId());
        //create head
        saveHead(branch);
    }

    public void add(String filename) {
        File addFile = new File(filename);

        if (!addFile.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }

        //create the class we need
        StagingArea stagingArea = StagingArea.loadStagingArea();
        Commit currentCommit = getCurrentCommit();
        Blob blob = new Blob(addFile);
        Map<String, String> currentBlobs = currentCommit.getBlobs();

        //check if the blobs is created
        if (currentBlobs.containsKey(filename) && currentBlobs.get(filename).equals(blob.getId())) {
            stagingArea.unstageFile(filename);
            return;
        }
        //stage the file in addition area
        blob.saveBlob();
        //Staging an already-staged file overwrites the previous entry in the staging area with the new contents.
        stagingArea.stageFile(filename, blob.getId());
    }

    public void commit(String message) {
        // check the message is existed
        if (message == null || message.isEmpty()) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        StagingArea stagingArea = StagingArea.loadStagingArea();
        // check staging area is not null
        if (stagingArea.getStagedForAddition().isEmpty()
                && stagingArea.getStagedForRemoval().isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        Commit parentCommit = getCurrentCommit();
        Map<String, String> parentBlobs = parentCommit.getBlobs();
        Map<String, String> stagedForAddition = stagingArea.getStagedForAddition();
        Set<String> stagedForRemoval = stagingArea.getStagedForRemoval();
        parentBlobs.putAll(stagedForAddition);
        for (String fileToRemove : stagedForRemoval) {
            parentBlobs.remove(fileToRemove);
        }
        Commit newCommit = new Commit(message, parentCommit.getCommitId(), null, parentBlobs);
        newCommit.saveCommit();
        newCommit.saveCommit();
        stagingArea.clear();
        saveBranch(getHead(), newCommit.getCommitId());
    }

    public void rm(String filename) {
        File removeFile = new File(filename);
        if (!removeFile.exists()) {
            System.exit(0);
        }
        StagingArea stagingArea = StagingArea.loadStagingArea();
        Commit currentCommit = getCurrentCommit();
        Map<String, String> stagedForAddition = stagingArea.getStagedForAddition();
        Map<String, String> blobs = currentCommit.getBlobs();
        // exit if stage or commit not include this file
        if (!stagedForAddition.containsKey(filename) && !blobs.containsKey(filename)) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        //Unstage the file if it is currently staged for addition.
        if (stagedForAddition.containsKey(filename)) {
            stagedForAddition.remove(filename);
            stagingArea.save();
            return;
        }
        //If the file is tracked in the current commit, stage it for removal and remove the file from the working directory
        if (blobs.containsKey(filename)) {
            stagingArea.stageRemoval(filename);
            Utils.restrictedDelete(removeFile);
        }
    }

    public void log() {
        Commit currentCommit = getCurrentCommit();
        while (currentCommit != null) {
            System.out.println(currentCommit);
            if (currentCommit.getParent() == null) {
                break;
            }
            currentCommit = Commit.load(currentCommit.getParent());
        }
    }

    public void globalLog() {
        Set<Commit> commits = getAllCommits();
        for (Commit commit : commits) {
            System.out.println(commit);
        }
    }

    public void find(String message) {
        Set<Commit> commits = getAllCommits();
        boolean found = false;
        for (Commit commit : commits) {
            if (commit.getMessage().equals(message)) {
                System.out.println(commit.getCommitId());
                found = true;
            }
        }
        if (!found) {
            System.out.println("Found no commit with that message.");
        }
    }

    public void status() {
        String currentBranch = getHead();
        List<String> branches = getAllBranch();
        StagingArea stagingArea = StagingArea.loadStagingArea();
        Commit currentCommit = getCurrentCommit();
        Map<String, String> stagedForAddition = stagingArea.getStagedForAddition();
        List<String> stagedFails = new ArrayList<>(stagedForAddition.keySet());
        Collections.sort(stagedFails);
        Set<String> stagedForRemoval = stagingArea.getStagedForRemoval();

        Map<String, String> currentBlobs = currentCommit.getBlobs();

        StringBuilder log = new StringBuilder("=== Branches ===" + "\n");
        for (String branch : branches) {
            if (branch.equals(currentBranch)) {
                log.append("*").append(branch).append("\n");
            } else {
                log.append(branch).append("\n");
            }
        }

        log.append("\n").append("=== Staged Files ===").append("\n");
        for (String stagedFile : stagedFails) {
            log.append(stagedFile).append("\n");
        }

        log.append("\n").append("=== Removed Files ===").append("\n");
        for(String blob :currentBlobs.keySet() ) {
            File file = new File(blob);
            if(!file.exists()) {
                if(!stagedForRemoval.contains(blob)) {
                    stagedForRemoval.add(blob);
                }
            }
        }
        List<String> removalFiles = new ArrayList<>(stagedForRemoval);
        Collections.sort(removalFiles);
        for (String removedFile : removalFiles) {
            log.append(removedFile).append("\n");
        }


        log.append("\n").append("=== Modifications Not Staged For Commit ===").append("\n");
        List<String> modifiedFiles = new ArrayList<>();
        for (String blob : currentBlobs.keySet()) {
            File file = new File(blob);
            String blobId = currentBlobs.get(blob);
            //Tracked in the current commit, changed in the working directory, but not staged;
            if (file.exists()
                    && !stagedForAddition.containsKey(blob)
                    && !Utils.sha1(Utils.readContentsAsString(file)).equals(blobId)) {
                modifiedFiles.add(blob + " (modified)");
            }
            //Not staged for removal, but tracked in the current commit and deleted from the working directory.
            if (!file.exists() && !stagedForRemoval.contains(blob)) {
                modifiedFiles.add(blob + " (deleted)");
            }
        }

        for (String blob : stagedForAddition.keySet()) {
            File file = new File(blob);
            String blobId = stagedForAddition.get(blob);
            //Staged for addition, but with different contents than in the working directory; or
            if (file.exists() && !Utils.sha1(Utils.readContentsAsString(file)).equals(blobId)) {
                modifiedFiles.add(blob + " (modified)");
            }
            //Staged for addition, but deleted in the working directory;
            if (!file.exists() && !stagedForRemoval.contains(blob)) {
                modifiedFiles.add(blob + " (deleted)");
            }
        }
        Collections.sort(modifiedFiles);
        for (String modifiedFile : modifiedFiles) {
            log.append(modifiedFile).append("\n");
        }

        log.append("\n").append("=== Untracked Files ===").append("\n");
        List<String> untrackedFiles = getUntrackedFileList();
        for (String untrackedFile : untrackedFiles) {
            log.append(untrackedFile).append("\n");
        }
        System.out.println(log);
    }

    public void checkout(String[] args) {
        if (args.length == 3 && args[1].equals("--")) {
            checkoutFile(getCurrentCommit(), args[2]);
        } else if (args.length == 4 && args[2].equals("--")) {
            checkoutFile(Commit.load(args[1]), args[3]);
        } else if (args.length == 2) {
            checkoutBranch(args[1]);
        } else {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
    }

    public void branch(String branch) {

    }

    private void checkoutFile(Commit commit, String fileName) {
        if (commit == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }

        if (!commit.getBlobs().containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }

        String blobId = commit.getBlobs().get(fileName);
        File blobFile = Blob.getBlob(blobId);
        if (!blobFile.exists()) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }

        String content = readContentsAsString(blobFile);
        File currentFile = new File(CWD, fileName);
        Utils.writeContents(currentFile, content);
    }

    private void checkoutBranch(String branch) {
        File branchFile = new File(BRANCH_DIR, branch);
        if (!branchFile.exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        if (branch.equals(getHead())) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        List<String> untrackedFiles = getUntrackedFileList();
        if (!untrackedFiles.isEmpty()) {
            System.out.println("There is an untracked file in the way; " +
                    "delete it, or add and commit it first.");
            System.exit(0);
        }
        String commitId = getBranch(branch);
        Commit commit = Commit.load(commitId);
        Map<String, String> blobs = commit.getBlobs();
        if (blobs != null) {
            for (String blob : blobs.keySet()) {
                File oldFile = Blob.getBlob(blobs.get(blob));
                File currentFile = new File(CWD, blob);
                Utils.writeContents(currentFile, Utils.readContentsAsString(oldFile));
            }
        }
        Commit currentCommit = getCurrentCommit();
        for (String blobId : currentCommit.getBlobs().keySet()) {
            if (!blobs.containsKey(blobId)) {
                Utils.restrictedDelete(blobId);
            }
        }
        StagingArea stagingArea = StagingArea.loadStagingArea();
        stagingArea.clear();
        updateHead(branch);
    }

    private List<String> getUntrackedFileList() {
        StagingArea stagingArea = StagingArea.loadStagingArea();
        Commit currentCommit = getCurrentCommit();
        Map<String, String> stagedForAddition = stagingArea.getStagedForAddition();
        Set<String> stagedForRemoval = stagingArea.getStagedForRemoval();
        Map<String, String> currentBlobs = currentCommit.getBlobs();

        List<String> untrackedFiles = new ArrayList<>();
        for (String file : Repository.CWD.list()) {
            File f = new File(file);
            if (f.isDirectory()) {
                continue;
            }

            boolean inCommit = currentBlobs.containsKey(file);
            boolean inStaging = stagedForAddition.containsKey(file);
            boolean inRemoval = stagedForRemoval.contains(file);

            //files present in the working directory but neither staged for addition nor tracked
            if (!inCommit && !inStaging && !inRemoval) {
                untrackedFiles.add(file);
            }

            // files that have been staged for removal, but then re-created without Gitlet’s knowledge.
            if (inRemoval && f.exists()) {
                untrackedFiles.add(file);
            }

        }
        Collections.sort(untrackedFiles);
        return untrackedFiles;
    }

    private Set<Commit> getAllCommits() {
        Set<Commit> commits = new HashSet<>();
        List<String> commitList = plainFilenamesIn(COMMIT_DIR);
        if (commitList == null) {
            return null;
        }
        for (String commitId : commitList) {
            Commit commit = Commit.load(commitId);
            commits.add(commit);
        }
        return commits;
    }

    private void saveBranch(String branch, String commit) {
        File branchFile = new File(BRANCH_DIR, branch);
        Utils.writeContents(branchFile, commit);
    }

    private String getBranch(String branch) {
        File branchFile = new File(BRANCH_DIR, branch);
        if (!branchFile.exists()) {
            return null;
        }
        return Utils.readContentsAsString(branchFile);
    }

    private List<String> getAllBranch() {
        List<String> branches = new ArrayList<>(Arrays.asList(Repository.BRANCH_DIR.list()));
        return branches;
    }

    private Commit getCurrentCommit() {
        String head = getHead();
        String branchCommit = getBranch(head);
        return Commit.load(branchCommit);
    }

    private void saveHead(String head) {
        Utils.writeContents(HEAD, head);
    }

    private String getHead() {
        if (!HEAD.exists()) {
            return null;
        }
        return Utils.readContentsAsString(HEAD);
    }

    private void updateHead(String head) {
        Utils.writeContents(HEAD, head);
    }
}
