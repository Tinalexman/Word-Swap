package swapper;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BackgroundTask
{

    static class Join
    {
        public String path;
        public StringBuilder search;
        public StringBuilder replace;
        public int lineChanges;

        public Join(String path, StringBuilder search, StringBuilder replace, int lineChanges)
        {
            this.path = path;
            this.search = search;
            this.replace = replace;
            this.lineChanges = lineChanges;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Join join = (Join) o;
            return search.toString().equals(join.search.toString()) &&
                    replace.toString().equals(join.replace.toString()) &&
                    path.equals(join.path) &&
                    lineChanges == join.lineChanges;
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(search, replace, path, lineChanges);
        }
    }

    private static List<List<String>> selectedFiles;
    private static List<String> filePaths;
    private static List<Join> result;
    private static List<String> searchKeywords;
    private static List<String> replaceKeywords;

    public static void clear()
    {
        BackgroundTask.result = null;
        BackgroundTask.selectedFiles = null;
        BackgroundTask.searchKeywords = null;
        BackgroundTask.replaceKeywords = null;
        BackgroundTask.filePaths = null;
    }

    public static void removeSelection(String path, List<String> file)
    {
        if(file != null && path != null && BackgroundTask.hasFiles())
        {
            BackgroundTask.selectedFiles.remove(file);
            BackgroundTask.filePaths.remove(path);
        }
    }

    public static void addSelection(String path, List<String> file)
    {
        if(BackgroundTask.selectedFiles == null)
            BackgroundTask.selectedFiles = new ArrayList<>();

        if(BackgroundTask.filePaths == null)
            BackgroundTask.filePaths = new ArrayList<>();

        BackgroundTask.filePaths.add(path);
        BackgroundTask.selectedFiles.add(file);
    }

    public static void addKeywords(String search, String replacement)
    {
        if(BackgroundTask.searchKeywords == null)
            BackgroundTask.searchKeywords = new ArrayList<>();

        if(BackgroundTask.replaceKeywords == null)
            BackgroundTask.replaceKeywords = new ArrayList<>();

        BackgroundTask.searchKeywords.add(search);
        BackgroundTask.replaceKeywords.add(replacement);
    }

    public static boolean hasFiles()
    {
        return BackgroundTask.selectedFiles != null && !BackgroundTask.selectedFiles.isEmpty();
    }

    public static List<Join> getResult()
    {
        return BackgroundTask.result;
    }

    public static void startTask()
    {
        SearchService service = new SearchService(BackgroundTask.searchKeywords, BackgroundTask.replaceKeywords,
                BackgroundTask.filePaths, BackgroundTask.selectedFiles);
        service.start();

        service.setOnSucceeded(e ->
        {
            BackgroundTask.result = service.getValue();
            Controller.processed();
        });
    }

    public static void saveFiles()
    {
        try
        {
            for(Join join : BackgroundTask.result)
            {
                PrintWriter writer = new PrintWriter(join.path);
                writer.println(join.replace.toString());
                writer.flush();
                writer.close();
            }
        }
        catch (Exception ignored)
        {

        }
    }

    static class SearchService extends Service<List<Join>>
    {
        final List<String> search, replace;
        final List<List<String>> files;
        final List<String> paths;

        public SearchService(List<String> search, List<String> replace, List<String> paths, List<List<String>> files)
        {
            this.paths = paths;
            this.search = search;
            this.replace = replace;
            this.files = files;
        }

        @Override
        protected Task<List<Join>> createTask()
        {
            return new SearchTask(this.search, this.replace, this.paths, this.files);
        }
    }

    static class SearchTask extends Task<List<Join>>
    {
        final List<String> search, replace;
        final List<List<String>> files;
        final List<String> paths;

        public SearchTask(List<String> search, List<String> replace, List<String> paths, List<List<String>> files)
        {
            this.search = search;
            this.replace = replace;
            this.files = files;
            this.paths = paths;
        }

        private String findAndReplace(String currentLine)
        {
            for(int i = 0; i < this.search.size(); ++i)
            {
                String search = this.search.get(i);
                int index = currentLine.indexOf(search);

                if(index != -1)
                {
                    String replace = this.replace.get(i);
                    currentLine = currentLine.replace(search, replace);
                    break;
                }
            }
            return currentLine;
        }

        private Join processFile(String path, List<String> lines)
        {
            StringBuilder original = new StringBuilder();
            StringBuilder replaced = new StringBuilder();
            int changes = 0;

            for (String line : lines)
            {
                original.append(line).append("\n");

                String modified = findAndReplace(line);
                replaced.append(modified).append("\n");

                if (!modified.equals(line))
                    ++changes;
            }

            return new Join(path, original, replaced, changes);
        }

        @Override
        protected List<Join> call()
        {
            List<Join> result = new ArrayList<>();
            int size = this.files.size();
            updateProgress(0, size);

            for(int i = 0; i < size; ++i)
            {
                List<String> file = this.files.get(i);
                String path = this.paths.get(i);
                result.add(processFile(path, file));
                updateProgress((i + 1), size);
            }
            return result;
        }
    }


}
