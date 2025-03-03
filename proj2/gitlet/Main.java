package gitlet;


/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *
 * @author Yi Wang
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }

        Repository repository = new Repository();

        String firstArg = args[0];

        switch (firstArg) {
            case "init":
                validate(args, 1);
                repository.init();
                break;
            case "add":
                validate(args, 2);
                repository.add(args[1]);
                break;
            case "commit":
                validate(args, 2);
                repository.commit(args[1]);
                break;
            case "rm":
                validate(args, 2);
                repository.rm(args[1]);
                break;
            case "log":
                validate(args, 1);
                repository.log();
                break;
            case "global-log":
                validate(args, 1);
                repository.globalLog();
                break;
            case "find":
                validate(args, 2);
                repository.find(args[1]);
                break;
            case "status":
                validate(args, 1);
                repository.status();
                break;
            case "checkout":
                repository.checkout(args);
                break;
            case "branch":
                validate(args, 1);
                repository.branch(args[1]);
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }


    }

    public static void validate(String[] args, int n) {
        String firstArg = args[0];

        if (!firstArg.equals("init") && !Repository.GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }

        if (args.length != n) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }

        if (firstArg.equals("checkout")) {
            if (args.length == 3 && !args[1].equals("--")) {
                // java gitlet.Main checkout -- [file name]
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            if (args.length == 4 && !args[2].equals("--")) {
                // java gitlet.Main checkout [commit id] -- [file name]
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
        }

    }


}
