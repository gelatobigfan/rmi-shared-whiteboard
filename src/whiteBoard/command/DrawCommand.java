package whiteBoard.command;

public interface DrawCommand {
    void execute();

    void undo();
}
