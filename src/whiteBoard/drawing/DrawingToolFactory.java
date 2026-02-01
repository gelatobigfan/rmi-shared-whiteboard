package whiteBoard.drawing;

import utils.Config;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;


public class DrawingToolFactory {
    private static final Map<String, Supplier<DrawingTool>> toolSuppliers = new HashMap<>();

    static {
        registerTool(Config.LINE, LineTool::new);
        registerTool(Config.RECTANGLE, RectangleTool::new);
        registerTool(Config.CIRCLE, CircleTool::new);
        registerTool(Config.OVAL, OvalTool::new);
        registerTool(Config.DRAW, DrawTool::new);
        registerTool(Config.ERASER, EraserTool::new);
        registerTool(Config.TEXT, TextTool::new);
    }

    public static void registerTool(String toolType, Supplier<DrawingTool> supplier) {
        toolSuppliers.put(toolType, supplier);
    }

    public static DrawingTool getTool(String toolType) {
        Supplier<DrawingTool> supplier = toolSuppliers.get(toolType);
        return supplier != null ? supplier.get() : null;
    }


    public static TextTool getTextTool(String text, int fontSize) {
        TextTool textTool = (TextTool) getTool(Config.TEXT);
        if (textTool != null) {
            textTool.setText(text);
            textTool.setFontSize(fontSize);
        }
        return textTool;
    }
}