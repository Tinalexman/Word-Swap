package swapper;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;

import java.util.List;

public class Card extends AnchorPane
{
    private final FontAwesomeIcon cancel;
    public List<String> data;

    public Card(String header, String subtitle, String trailing)
    {
        this(header, subtitle, trailing, null);
    }

    public Card(String header, String subtitle, String trailing, List<String> data)
    {
        setStyle("-fx-background-color: WHITE; -fx-background-radius: 5 5 5 5;" +
                " -fx-border-color: rgb(150, 150, 150); -fx-border-radius: 5 5 5 5;");

        setPrefWidth(650.0);
        setMinWidth(650.0);

        setPrefHeight(75.0);
        setMinHeight(75.0);

        this.data = data;

        Label filename = new Label(header);
        filename.setFont(new Font("System Bold", 16.0));
        setTopAnchor(filename, 10.0);
        setLeftAnchor(filename, 20.0);

        Label path = new Label(subtitle);
        setTopAnchor(path, 30.0);
        setLeftAnchor(path, 20.0);

        Label lines;
        if(data != null)
        {
            int fileLines = data.size();
            lines = new Label(fileLines + (fileLines == 1 ? "line" : " lines"));
        }
        else
            lines = new Label();

        setBottomAnchor(lines, 10.0);
        setLeftAnchor(lines, 20.0);


        Label size = new Label(trailing);
        setBottomAnchor(size, 10.0);
        setRightAnchor(size, 20.0);

        this.cancel = new FontAwesomeIcon();
        this.cancel.setGlyphName("TIMES");
        setTopAnchor(this.cancel, 10.0);
        setRightAnchor(this.cancel, 20.0);

        getChildren().addAll(filename, lines, path, size, cancel);
    }

    public void setHandler(EventHandler<? super MouseEvent> handler)
    {
        this.cancel.setOnMouseClicked(handler);
    }
}
