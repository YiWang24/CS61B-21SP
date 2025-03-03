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
            System.out.println("A Gitlet version-control system already exists in the current directory.");
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
        //create default branch
        saveBranch("master", initCommit.getCommitId());
        //create head
        saveHead("master");
    }

    public void add(String filename) {
        File addFile = readFile(filename);
        //create the class we need
        StagingArea stagingArea = StagingArea.loadStagingArea();
        Blob blob = new Blob(addFile);
        //check if the blobs is created
        if (getCurrentCommit().isBlobExists(filename, blob.getId())) {
            stagingArea.unstageFile(filename);
            System.exit(0);
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
        createCommit(message, stagingArea);
    }

    public void rm(String filename) {
        File removeFile = readFile(filename);
        StagingArea stagingArea = StagingArea.loadStagingArea();
        //Unstage the file if it is currently staged for addition.
        if (stagingArea.isStagedForAddition(filename)) {
            stagingArea.unstageFile(filename);
            return;
        }
        //If the file is tracked in the current commit, stage it for removal and remove the file from the working directory
        if (getCurrentCommit().isBlobExists(filename)) {
            stagingArea.stageRemoval(filename);
            Utils.restrictedDelete(removeFile);
            return;
        }
        // exit if stage or commit not include this file
        System.out.println("No reason to remove the file.");
        System.exit(0);
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
        assert commits != null;
        for (Commit commit : commits) {
            System.out.println(commit);
        }
    }

    public void find(String message) {
        Set<Commit> commits = getAllCommits();
        boolean found = false;
        assert commits != null;
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
        StagingArea stagingArea = StagingArea.loadStagingArea();
        StringBuilder log = new StringBuilder("=== Branches ===" + "\n");
        //Print all branches
        List<String> branches = getAllBranch();
        for (String branch : branches) {
            if (branch.equals(getHead())) {
                log.append("*").append(branch).append("\n");
            } else {
                log.append(branch).append("\n");
            }
        }
        //Print all staged files
        log.append("\n").append("=== Staged Files ===").append("\n");
        List<String> stagedFiles = stagingArea.getStagedForAdditionList();
        for (String stagedFile : stagedFiles) {
            log.append(stagedFile).append("\n");
        }
        //Print all removed files
        log.append("\n").append("=== Removed Files ===").append("\n");
        List<String> stagedForRemovalList = stagingArea.getStagedForRemovalList();
        for (String blob : getCurrentCommit().getBlobsList()) {
            File file = new File(blob);
            //Case: If file is in Commit but removed manually, add this file in staged for removal;
            if (!file.exists() && !stagingArea.isStagedForRemoval(blob)) {
                stagedForRemovalList.add(blob);
            }
        }
        for (String removedFile : stagedForRemovalList) {
            log.append(removedFile).append("\n");
        }
        //Print Modifications Not Staged For Commit
        log.append("\n").append("=== Modifications Not Staged For Commit ===").append("\n");
        List<String> modifiedFiles = getModificationsNotStagedList(getCurrentCommit(), stagingArea);
        for (String modifiedFile : modifiedFiles) {
            log.append(modifiedFile).append("\n");
        }
        //Print Untracked Files
        log.append("\n").append("=== Untracked Files ===").append("\n");
        List<String> untrackedFiles = getUntrackedFileList(stagingArea);
        assert untrackedFiles != null;
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
        if (getBranch(branch) != null) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        saveBranch(branch, getCurrentCommit().getCommitId());
    }

    public void rmBranch(String branch) {
        if (getBranch(branch) == null) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (getBranch(branch).equals(getHead())) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        deleteBranch(branch);
    }

    public void reset(String commitId) {
        checkoutCommit(commitId);
        saveBranch(getHead(), commitId);
    }

    private void checkoutFile(Commit commit, String fileName) {
        if (commit == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        if (!commit.isBlobExists(fileName)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        String blobId = commit.getBlobId(fileName);
        File blobFile = Blob.getBlob(blobId);
        if (blobFile == null || !blobFile.exists()) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        String content = readContentsAsString(blobFile);
        File currentFile = new File(CWD, fileName);
        Utils.writeContents(currentFile, content);
    }


    private void checkoutCommit(String commitId) {
        StagingArea stagingArea = StagingArea.loadStagingArea();
        List<String> untrackedFiles = getUntrackedFileList(stagingArea);
        assert untrackedFiles != null;
        if (!untrackedFiles.isEmpty()) {
            System.out.println("There is an untracked file in the way; " + "delete it, or add and commit it first.");
            System.exit(0);
        }
        Commit commit = Commit.load(commitId);
        if (commit == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        //Write content from branch to current blob
        for (String blob : commit.getBlobsList()) {
            File oldFile = Blob.getBlob(commit.getBlobId(blob));
            File FileToRewrite = new File(CWD, blob);
            Utils.writeContents(FileToRewrite, Utils.readContentsAsString(oldFile));
        }
        //Remove current blob if it is not exist in old branch
        for (String blob : getCurrentCommit().getBlobsList()) {
            if (!commit.isBlobExists(blob)) {
                File fileToDelete = new File(CWD, blob);
                Utils.restrictedDelete(fileToDelete);
            }
        }
        stagingArea.clear();
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
        checkoutCommit(getBranch(branch));
        saveHead(branch);
    }

    private List<String> getModificationsNotStagedList(Commit commit, StagingArea stagingArea) {
        List<String> modifiedFiles = new ArrayList<>();
        for (String blob : commit.getBlobsList()) {
            File file = new File(blob);
            String blobId = commit.getBlobId(blob);
            //Case: Tracked in the current commit, changed in the working directory, but not staged;
            if (file.exists() && !stagingArea.isStagedForAddition(blob) && !Utils.sha1(Utils.readContentsAsString(file)).equals(blobId)) {
                modifiedFiles.add(blob + " (modified)");
            }
        }
        for (String blob : stagingArea.getStagedForAdditionList()) {
            File file = new File(blob);
            String blobId = stagingArea.getStagedForAddition().get(blob);
            //Staged for addition, but with different contents than in the working directory;
            if (file.exists() && !Utils.sha1(Utils.readContentsAsString(file)).equals(blobId)) {
                modifiedFiles.add(blob + " (modified)");
            }
            //Staged for addition, but deleted in the working directory;
            if (!file.exists() && !stagingArea.isStagedForRemoval(blob)) {
                modifiedFiles.add(blob + " (deleted)");
            }
        }
        Collections.sort(modifiedFiles);
        return modifiedFiles;
    }

    private List<String> getUntrackedFileList(StagingArea stagingArea) {
        List<String> untrackedFiles = new ArrayList<>();
        String[] list = CWD.list();
        if (list == null) {
            return null;
        }
        for (String file : list) {
            File f = new File(file);
            if (f.isDirectory()) {
                continue;
            }
            boolean inCommit = getCurrentCommit().isBlobExists(file);
            boolean inStaging = stagingArea.isStagedForAddition(file);
            boolean inRemoval = stagingArea.isStagedForRemoval(file);
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

    private void createCommit(String message, StagingArea stagingArea) {
        //find the parent commit
        Commit parentCommit = getCurrentCommit();
        Map<String, String> parentBlobs = parentCommit.getBlobs();
        // check staging area is not null
        if (stagingArea.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        //Put all staged for addition into commit
        parentBlobs.putAll(stagingArea.getStagedForAddition());
        //Remove all staged for removal blobs from commit
        Commit.removeBlobsFromCommit(parentBlobs, stagingArea.getStagedForRemoval());
        //Create new commit
        Commit commit = new Commit(message, parentCommit.getCommitId(), null, parentBlobs);
        stagingArea.clear();
        saveBranch(getHead(), commit.getCommitId());
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

    private void saveBranch(String branch, String commitId) {
        File branchFile = new File(BRANCH_DIR, branch);
        Utils.writeContents(branchFile, commitId);
    }

    private String getBranch(String branch) {
        File branchFile = new File(BRANCH_DIR, branch);
        if (!branchFile.exists()) {
            return null;
        }
        return Utils.readContentsAsString(branchFile);
    }

    private void deleteBranch(String branch) {
        File branchFile = new File(BRANCH_DIR, branch);
        if (branchFile.exists()) {
            branchFile.delete();
        }
    }

    private List<String> getAllBranch() {
        return new ArrayList<>(Arrays.asList(Objects.requireNonNull(BRANCH_DIR.list())));
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


    private File readFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        return file;
    }

}
