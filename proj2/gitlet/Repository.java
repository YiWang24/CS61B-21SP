package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static gitlet.Utils.join;
import static gitlet.Utils.readContentsAsString;


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
    public static final File REMOTE_DIR = new File(GITLET_DIR, "remotes");


    public void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system "
                    + "already exists in the current directory.");
            System.exit(0);
        }
        // create directory for GitLet
        GITLET_DIR.mkdir();
        COMMIT_DIR.mkdir();
        BRANCH_DIR.mkdir();
        STAGING_DIR.mkdir();
        BLOB_DIR.mkdir();
        REMOTE_DIR.mkdir();
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
        //Staging an already-staged file overwrites the previous
        stagingArea.stageFile(filename, blob.getId());
    }

    public void commit(String message) {
        // check the message is existed
        if (message == null || message.isEmpty()) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        StagingArea stagingArea = StagingArea.loadStagingArea();
        createCommit(message, stagingArea, null);
    }

    public void rm(String filename) {
        File removeFile = new File(CWD, filename);
        StagingArea stagingArea = StagingArea.loadStagingArea();
        //Unstage the file if it is currently staged for addition.
        if (stagingArea.isStagedForAddition(filename)) {
            stagingArea.unstageFile(filename);
            return;
        }
        //If the file is tracked in the current commit
        //stage it for removal and remove the file from the working directory
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
        Set<Commit> commits = Commit.getAllCommits();
        assert commits != null;
        for (Commit commit : commits) {
            System.out.println(commit);
        }
    }

    public void find(String message) {
        Set<Commit> commits = Commit.getAllCommits();
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
            String commitId = args[1];
            if (commitId.length() == 8) {
                commitId = Commit.findCommitByPrefix(commitId);
            }
            checkoutFile(Commit.load(commitId), args[3]);
        } else if (args.length == 2) {
            String commitId = args[1];
            if (commitId.length() == 8) {
                commitId = Commit.findCommitByPrefix(commitId);
            }
            checkoutBranch(commitId);
        } else {
            System.out.println("Incorrect operands.");
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
        File branchFile = new File(BRANCH_DIR, branch);
        if (branchFile.getName().equals(getHead())) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        deleteBranch(branch);
    }

    public void reset(String commitId) {

        checkoutCommit(commitId);
        saveBranch(getHead(), commitId);
    }

    public void merge(String branch) {
        StagingArea stagingArea = StagingArea.loadStagingArea();
        if (!stagingArea.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        if (getBranch(branch) == null) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        Commit mergeCommit = Commit.load(getBranch(branch));
        Commit currentCommit = getCurrentCommit();
        assert currentCommit != null;
        if (currentCommit.getCommitId().equals(mergeCommit.getCommitId())) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
        if (!Objects.requireNonNull(getUntrackedFileList(stagingArea)).isEmpty()) {
            System.out.println("There is an untracked file in the way;"
                    + " delete it, or add and commit it first.");
            System.exit(0);
        }
        String splitPoint = findSplitPoint(currentCommit, mergeCommit);
        Commit splitCommit = Commit.load(splitPoint);
        assert splitPoint != null;
        if (splitPoint.equals(mergeCommit.getCommitId())) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }
        if (splitPoint.equals(getCurrentCommit().getCommitId())) {
            checkoutBranch(branch);
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }
        AtomicBoolean isMerged = new AtomicBoolean(false);
        Set<String> allFiles = new HashSet<>();
        assert splitCommit != null;
        allFiles.addAll(splitCommit.getBlobsList());
        allFiles.addAll(currentCommit.getBlobsList());
        allFiles.addAll(mergeCommit.getBlobsList());
        for (String file : allFiles) {
            mergeFile(stagingArea, file, splitCommit, currentCommit, mergeCommit, isMerged);
        }
        String message = String.format("Merged %s into %s.", branch, getHead());

        createCommit(message, stagingArea, mergeCommit.getCommitId());

        if (isMerged.getPlain()) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    public void addRemote(String name, String path) {
        getRemoteFile(name, "A remote with that name already exists.", false);
        Utils.writeContents(new File(REMOTE_DIR, name), path.replace("/", File.separator));
    }

    public void removeRemote(String name) {
        getRemoteFile(name, "A remote with that name does not exist.", true);
        new File(REMOTE_DIR, name).delete();
    }

    public void push(String name, String branch) {
        String remotePath = getRemoteFile(name, "Remote directory not found.", true);
        File remoteFile = new File(remotePath);
        if (!remoteFile.exists()) {
            System.out.println("Remote directory not found.");
            System.exit(0);
        }
        File remoteBranchFile = new File(remotePath, "branches" + File.separator + branch);
        String localHead = getCurrentCommit().getCommitId();
        if (!remoteBranchFile.exists()) {
            Utils.writeContents(remoteBranchFile, localHead);
            copyFile(GITLET_DIR, localHead, remoteFile, null);
            return;
        }
        String remoteHead = Utils.readContentsAsString(remoteBranchFile);
        Set<Commit> commits = Commit.getAllCommits();
        boolean branchExists = false;
        for (Commit commit : commits) {
            if (commit.getCommitId().equals(remoteHead)) {
                branchExists = true;
            }
        }
        if (!branchExists) {
            System.out.println("Please pull down remote changes before pushing.");
            System.exit(0);
        }
        copyFile(GITLET_DIR, localHead, remoteFile, remoteHead);
        Utils.writeContents(remoteBranchFile, localHead);

    }

    public void fetch(String name, String branch) {
        String remotePath = getRemoteFile(name, "Remote directory not found.", true);
        File remoteFile = new File(remotePath);
        if (!remoteFile.exists()) {
            System.out.println("Remote directory not found.");
            System.exit(0);
        }
        File remoteBranchFile = new File(remotePath, "branches" + File.separator + branch);
        if (!remoteBranchFile.exists()) {
            System.out.println("That remote does not have that branch.");
            System.exit(0);
        }
        String remoteHead = Utils.readContentsAsString(remoteBranchFile);
        copyFile(remoteFile, remoteHead, GITLET_DIR, null);
        File localRemoteBranch = new File(BRANCH_DIR, name + File.separator + branch);
        localRemoteBranch.getParentFile().mkdir();
        Utils.writeContents(localRemoteBranch, remoteHead);
    }

    public void pull(String name, String branch) {
        fetch(name, branch);
        merge(name + File.separator + branch);
    }

    private void copyFile(File fromPath, String fromHead, File toPath, String toHead) {
        File fromCommitDir = new File(fromPath, "commits");
        File toCommitDir = new File(toPath, "commits");

        while (!Objects.equals(fromHead, toHead)) {
            copySingleFile(fromCommitDir, toCommitDir, fromHead, true);
            Commit commit = Utils.readObject(new File(fromCommitDir, fromHead), Commit.class);
            fromHead = commit.getParent();
        }

        File fromBlobsDir = new File(fromPath, "blobs");
        File toBlobsDir = new File(toPath, "blobs");

        for (String dir : Objects.requireNonNull(fromBlobsDir.list())) {
            copySingleFile(fromBlobsDir, toBlobsDir, dir, false);
            for (String blob : Objects.requireNonNull(new File(fromBlobsDir, dir).list())) {
                copySingleFile(new File(fromBlobsDir, dir), new File(toBlobsDir, dir), blob, false);
            }
        }
    }

    private void copySingleFile(File fromDir, File toDir, String filename, boolean isCommit) {
        File fromFile = new File(fromDir, filename);
        File toFile = new File(toDir, filename);
        if (!toFile.exists()) {
            if (fromFile.isDirectory()) {
                toFile.mkdir();
                return;
            }
            if (!isCommit) {
                Utils.writeContents(toFile, Utils.readContentsAsString(fromFile));
            } else {
                Utils.writeObject(toFile, Utils.readObject(fromFile, Commit.class));
            }
        }
    }

    private String getRemoteFile(String name, String message, Boolean shouldExist) {
        File remoteFile = new File(REMOTE_DIR, name);
        if (shouldExist && !remoteFile.exists()) {
            System.out.println(message);
            System.exit(0);
        } else if (!shouldExist && remoteFile.exists()) {
            System.out.println(message);
            System.exit(0);
        }
        return remoteFile.exists() ? Utils.readContentsAsString(remoteFile) : null;
    }

    private void mergeFile(StagingArea stagingArea, String file, Commit splitCommit,
                           Commit currentCommit, Commit mergeCommit, AtomicBoolean isMerged) {
        String splitBlob = splitCommit.getBlobId(file);
        String currentBlob = currentCommit.getBlobId(file);
        String mergeBlob = mergeCommit.getBlobId(file);
        //Any files that have been modified in the given branch since the split point,
        //but not modified in the current branch
        if (splitCommit.isBlobExists(file)
                && currentCommit.isBlobExists(file)
                && mergeCommit.isBlobExists(file)
                && !Objects.equals(mergeBlob, splitBlob)
                && Objects.equals(currentBlob, splitBlob)) {
            checkoutFile(mergeCommit, file);
            stagingArea.stageFile(file, mergeBlob);
            return;
        }
        //Any files that were not present at the split point
        //and are present only in the given branch should be checked out and staged.
        if (!splitCommit.isBlobExists(file)
                && !currentCommit.isBlobExists(file)
                && mergeCommit.isBlobExists(file)) {
            checkoutFile(mergeCommit, file);
            stagingArea.stageFile(file, mergeBlob);
            return;
        }
        //Any files present at the split point, unmodified in the current branch,
        //and absent in the given branch should be removed (and untracked).
        if (splitCommit.isBlobExists(file)
                && Objects.equals(currentBlob, splitBlob)
                && !mergeCommit.isBlobExists(file)) {
            rm(file);
            stagingArea.stageRemoval(file);
            return;
        }
        if ((!Objects.equals(currentBlob, mergeBlob)
                && mergeCommit.isBlobExists(file)
                && currentCommit.isBlobExists(file)
                && !Objects.equals(mergeBlob, splitBlob))
                || (!Objects.equals(currentBlob, splitBlob)
                && !mergeCommit.isBlobExists(file)
                && currentCommit.isBlobExists(file)
                && splitCommit.isBlobExists(file))
                || (!Objects.equals(mergeBlob, splitBlob)
                && mergeCommit.isBlobExists(file)
                && !currentCommit.isBlobExists(file))
                && splitCommit.isBlobExists(file)) {
            String conflictContent = "<<<<<<< HEAD" + "\n";
            if (currentBlob != null) {
                conflictContent += readContentsAsString(new File(CWD, file));
            }
            conflictContent += "=======" + "\n";
            if (mergeBlob != null) {
                conflictContent += readContentsAsString(Blob.getBlob(mergeBlob));
            }
            conflictContent += ">>>>>>>" + "\n";
            File conflictFile = new File(CWD, file);
            Utils.writeContents(conflictFile, conflictContent);
            stagingArea.stageFile(file, new Blob(conflictFile).getId());
            isMerged.set(true);
        }

    }

    private String findSplitPoint(Commit currentBranch, Commit mergeBranch) {
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();

        queue.add(currentBranch.getCommitId());
        queue.add(mergeBranch.getCommitId());

        while (!queue.isEmpty()) {
            String currentCommitId = queue.poll();
            if (visited.contains(currentCommitId)) {
                return currentCommitId;
            }
            visited.add(currentCommitId);
            Commit currentCommit = Commit.load(currentCommitId);
            if (currentCommit != null) {
                if (currentCommit.getParent() != null) {
                    queue.add(currentCommit.getParent());
                }
                if (currentCommit.getMergeParent() != null) {
                    queue.add(currentCommit.getMergeParent());
                }
            }
        }
        return null;
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
        List<String> stagedForAdditionList = stagingArea.getStagedForAdditionList();
        assert untrackedFiles != null;
        if (!untrackedFiles.isEmpty()) {
            System.out.println("There is an untracked file in the way; "
                    + "delete it, or add and commit it first.");
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
            File fileToRewrite = new File(CWD, blob);
            Utils.writeContents(fileToRewrite, Utils.readContentsAsString(oldFile));
        }
        //Remove current blob if it is not exist in old branch
        for (String blob : getCurrentCommit().getBlobsList()) {
            if (!commit.isBlobExists(blob)) {
                File fileToDelete = new File(CWD, blob);
                Utils.restrictedDelete(fileToDelete);
            }
        }
        //Remove tracked files that are not present in that commit
        for (String stagedFile : stagedForAdditionList) {
            if (!commit.isBlobExists(stagedFile)) {
                File fileToDelete = new File(CWD, stagedFile);
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
            File file = new File(CWD, blob);
            String blobId = commit.getBlobId(blob);
            //Case: Tracked in the current commit, changed in the working directory, but not staged;
            if (file.exists()
                    && !stagingArea.isStagedForAddition(blob)
                    && !Utils.sha1(Utils.readContentsAsString(file)).equals(blobId)) {
                modifiedFiles.add(blob + " (modified)");
            }
            //Not staged for removal, but tracked in the current commit
            //and deleted from the working directory.
            if (!file.exists() && !stagingArea.isStagedForRemoval(blob)) {
                modifiedFiles.add(blob + " (deleted)");
            }
        }
        for (String blob : stagingArea.getStagedForAdditionList()) {
            File file = new File(CWD, blob);
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
            File f = new File(CWD, file);
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
            // files that have been staged for removal
            // but then re-created without Gitlet’s knowledge.
            if (inRemoval && f.exists()) {
                untrackedFiles.add(file);
            }
        }
        Collections.sort(untrackedFiles);
        return untrackedFiles;
    }

    private void createCommit(String message, StagingArea stagingArea, String mergeParent) {
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
        Commit commit = new Commit(message, parentCommit.getCommitId(), mergeParent, parentBlobs);
        stagingArea.clear();
        saveBranch(getHead(), commit.getCommitId());
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
        File file = new File(CWD, fileName);
        if (!file.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        return file;
    }

}
