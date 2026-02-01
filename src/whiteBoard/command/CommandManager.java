package whiteBoard.command;

import java.util.Stack;

public class CommandManager {
    private final Stack<DrawCommand> undoStack = new Stack<>();
    private final Stack<DrawCommand> redoStack = new Stack<>();

    public void executeCommand(DrawCommand command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear();
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public void undo() {
        if (canUndo()) {
            DrawCommand command = undoStack.pop();
            command.undo();
            redoStack.push(command);
        }
    }

    public void redo() {
        if (canRedo()) {
            DrawCommand command = redoStack.pop();
            command.execute();
            undoStack.push(command);
        }
    }

    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }
}
