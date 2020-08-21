import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

public class DemoAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        final Project project = (Project) event.getData(PlatformDataKeys.PROJECT);
        final Editor mEditor = (Editor) event.getData(PlatformDataKeys.EDITOR);
        if (null == mEditor) {
            return;
        }
    }
}
