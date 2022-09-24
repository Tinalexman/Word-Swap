package swapper;

import animatefx.animation.AnimationFX;
import animatefx.animation.SlideInLeft;
import animatefx.animation.SlideInRight;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable
{
    private static Controller controller;

    @FXML
    private AnchorPane filePane, backPane, waitPane, swapPane;

    @FXML
    private ScrollPane replacePane, searchPane;

    @FXML
    private VBox selectedFiles, swapFields, listFiles;

    @FXML
    private Text originalFileText, replacedFileText;

    private Label defaultFileLabel, defaultSwapLabel;


    public Controller()
    {
        Controller.controller = this;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        this.defaultFileLabel = new Label("No Selected File(s)");
        this.defaultFileLabel.setPrefWidth(680.0);
        this.defaultFileLabel.setMaxWidth(680.0);
        this.defaultFileLabel.setPrefHeight(360.0);
        this.defaultFileLabel.setMaxHeight(360.0);
        this.defaultFileLabel.setAlignment(Pos.CENTER);

        this.defaultSwapLabel = new Label("No Parameters");
        this.defaultSwapLabel.setPrefWidth(680.0);
        this.defaultSwapLabel.setMaxWidth(680.0);
        this.defaultSwapLabel.setPrefHeight(360.0);
        this.defaultSwapLabel.setMaxHeight(360.0);
        this.defaultSwapLabel.setAlignment(Pos.CENTER);
    }

    public void selectFiles(MouseEvent event)
    {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Files");
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));

        List<File> files = chooser.showOpenMultipleDialog(Main.mainStage);
        ObservableList<Node> children = this.selectedFiles.getChildren();

        if(files != null)
        {
            children.clear();

            for(File file : files)
            {
                try
                {
                    String path = file.getAbsolutePath();
                    List<String> data = Files.readAllLines(Path.of(path));
                    BackgroundTask.addSelection(path, data);
                    Card card = new Card(file.getName(), path, sizeToString(file.length()), data);
                    card.setHandler(e ->
                    {
                        children.remove(card);
                        BackgroundTask.removeSelection(path, card.data);
                        if(children.isEmpty())
                            children.add(this.defaultFileLabel);
                    });
                    children.add(card);
                }
                catch(Exception ignored)
                {
                    Card card = new Card(file.getName(), "File cannot be opened", "");
                    card.setHandler(e ->
                    {
                        children.remove(card);
                        BackgroundTask.removeSelection(null, null);
                        if(children.isEmpty())
                            children.add(this.defaultFileLabel);
                    });
                    children.add(card);
                }
            }
        }

        if(children.isEmpty())
            children.add(this.defaultFileLabel);

        event.consume();
    }

    private String sizeToString(long size)
    {
        int mb = (int) (size * 0.00000095367431640625); // Equivalent to size / 1024 ^ 2
        long rm = size - ((long) mb * 1024 * 1024);
        double kb = rm * 0.0009765625; // Equivalent to rm / 1024
        return (mb + kb) + " KB";
    }

    public void focus(MouseEvent event)
    {
        ((Node) event.getSource()).requestFocus();
        event.consume();
    }

    public void toProcessFiles(MouseEvent event)
    {
        boolean isValidated = true;

        ObservableList<Node> children = this.swapFields.getChildren();
        if(children.get(0) instanceof Label)
            return;

        for(Node node : children)
            isValidated = isValidated && ((DoubleField) node).validate();

        if(isValidated)
        {
            for (Node child : children)
            {
                DoubleField doubleField = (DoubleField) child;
                String[] keywords = doubleField.getKeywords();
                BackgroundTask.addKeywords(keywords[0], keywords[1]);
            }

            BackgroundTask.startTask();
            this.waitPane.toFront();
            AnimationFX slide = new SlideInRight(this.waitPane).setSpeed(0.85);
            slide.setOnFinished(val ->
            {
                children.clear();
                children.add(this.defaultSwapLabel);
            });
            slide.play();

        }

        event.consume();
    }

    public static void processed()
    {
        Controller.controller.toResults();
        new SlideInRight(Controller.controller.backPane).setSpeed(0.85).play();
    }

    public void toSwapPane(MouseEvent event)
    {
        if(BackgroundTask.hasFiles())
        {
            this.swapPane.toFront();

            AnimationFX slide = new SlideInRight(this.swapPane).setSpeed(0.9);
            slide.setOnFinished(val -> {
                this.selectedFiles.getChildren().clear();
                this.selectedFiles.getChildren().add(defaultFileLabel);
            });
            slide.play();
        }

        event.consume();
    }

    public void home(MouseEvent event)
    {
        this.filePane.toFront();
        this.filePane.requestFocus();

        AnimationFX slide = new SlideInLeft(this.filePane).setSpeed(0.85);
        slide.setOnFinished(val -> BackgroundTask.clear());
        slide.play();

        event.consume();
    }

    private void toResults()
    {
        this.backPane.toFront();
        this.searchPane.hvalueProperty().bindBidirectional(this.replacePane.hvalueProperty());
        this.searchPane.vvalueProperty().bindBidirectional(this.replacePane.vvalueProperty());

        List<BackgroundTask.Join> result = BackgroundTask.getResult();
        ObservableList<Node> children = this.listFiles.getChildren();
        children.clear();

        for(BackgroundTask.Join join : result)
        {
            String filename = join.path.substring(join.path.lastIndexOf("\\") + 1);
            Result res = new Result(filename, join.lineChanges + " operations");
            EventHandler<? super MouseEvent> handler = event -> onResultSelect(res, join);
            res.setOnMouseClicked(handler);
            children.add(res);
        }

        onResultSelect(((Result) children.get(0)), result.get(0));
    }

    private void onResultSelect(Result res, BackgroundTask.Join join)
    {
        res.select(true);
        originalFileText.setText("");
        replacedFileText.setText("");
        processStrings(join.search.toString(), join.replace.toString());
    }

    private void processStrings(String search, String replace)
    {
        String[] searchArray = search.split(" ");
        String[] replaceArray = replace.split(" ");

        for(int i = 0; i < searchArray.length; ++i)
        {
            String searchWord = searchArray[i];
            String replaceWord = replaceArray[i];
            if(searchWord.equals(replaceWord))
            {
                this.originalFileText.setFill(Color.RED);
                this.replacedFileText.setFill(Color.GREEN);
            }
            else
            {
                this.originalFileText.setFill(Color.BLACK);
                this.replacedFileText.setFill(Color.BLACK);
            }

            String ext = ((i + 1) == searchArray.length) ? "" : " ";
            this.originalFileText.setText(this.originalFileText.getText() + searchWord + ext);
            this.replacedFileText.setText(this.replacedFileText.getText() + replaceWord + ext);
        }
    }

    public void addFields(MouseEvent event)
    {
        ObservableList<Node> children = this.swapFields.getChildren();

        if(children.get(0) instanceof Label)
            children.clear();

        DoubleField field = new DoubleField();
        field.setHandler(e ->
        {
            this.swapFields.getChildren().remove(field);
            if(children.isEmpty())
                children.add(this.defaultSwapLabel);
        });
        children.add(field);

        event.consume();
    }

    public void save(MouseEvent event)
    {
        this.waitPane.toFront();

        BackgroundTask.saveFiles();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Word Swap");
        alert.setContentText("The changes have been successfully saved to the file(s).");
        alert.show();
        home(event);
    }

}
