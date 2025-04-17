package Messages;

public class Messages {
    //Tool bar button tool tip texts
    private static final String OPEN_TOOL_TIP = "Load a file";
    private static final String RUN_TOOL_TIP = "Run the shuffler";
    private static final String STOP_TOOL_TIP = "Stop the shuffler";
    private static final String DELAY_TOOL_TIP = "Seconds between each sentence";
    private static final String EXIT_TOOL_TIP = "Exit the program";
    private static final String SETDIRECTORY_TOOL_TIP = "Set the workspace directory";

    public static String getToolTipMessage(String button_name) {
        switch(button_name.toLowerCase()) {
        case "open": return Messages.OPEN_TOOL_TIP;
        case "run": return Messages.RUN_TOOL_TIP;
        case "stop": return Messages.STOP_TOOL_TIP;
        case "delay": return Messages.DELAY_TOOL_TIP;
        case "setdirectory": return Messages.SETDIRECTORY_TOOL_TIP;
        default: return Messages.EXIT_TOOL_TIP;
        }
    }

    public static final String NO_SENTENCES_DIALOG_NAME = "No sentences";
	public static final String NO_SENTENCES_DIALOG = "There are no sentences to shuffle through";
	
	public static final String EXIT_DIALOG_NAME = "Quit";
	public static final String EXIT_DIALOG = "Are you sure you want to quit?";
	
	public static final String FILE_ERROR_DIALOG_NAME = "File error";
	public static final String FILE_NOT_FOUND_DIALOG = "File not found";
    public static final String FILE_LOAD_ERROR_DIALOG = "File could not be loaded";
}
