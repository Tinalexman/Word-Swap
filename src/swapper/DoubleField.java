package swapper;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class DoubleField extends AnchorPane
{
    private final FontAwesomeIcon cancel;
    private final TextField search;
    private final TextField replace;
    private final Label message;

    public DoubleField()
    {
        setStyle("-fx-background-color: WHITE; -fx-background-radius: 5 5 5 5;" +
                " -fx-border-color: rgb(150, 150, 150); -fx-border-radius: 5 5 5 5;");

        setPrefWidth(640.0);
        setMinWidth(640.0);
        setMaxHeight(640.0);

        setPrefHeight(75.0);
        setMinHeight(75.0);
        setMaxHeight(75.0);

        this.search = new TextField();
        this.search.setPromptText("Search");
        this.search.setAlignment(Pos.CENTER);
        this.search.setMinWidth(200.0);
        this.search.setPrefWidth(200.0);
        this.search.setFocusTraversable(false);
        setLeftAnchor(this.search, 40.0);
        setTopAnchor(this.search, 25.0);

        this.replace = new TextField();
        this.replace.setFocusTraversable(false);
        this.replace.setPromptText("Replace");
        this.replace.setAlignment(Pos.CENTER);
        this.replace.setMinWidth(200.0);
        this.replace.setPrefWidth(200.0);
        setTopAnchor(this.replace, 25.0);
        setRightAnchor(this.replace, 40.0);

        this.message = new Label();
        this.message.setTextFill(Color.RED);
        this.message.setAlignment(Pos.CENTER);
        this.message.setMinWidth(700.0);
        setTopAnchor(this.message, 55.0);

        this.cancel = new FontAwesomeIcon();
        this.cancel.setGlyphName("TIMES");
        setTopAnchor(this.cancel, 3.0);
        setRightAnchor(this.cancel, 10.0);

        getChildren().addAll(this.search, this.replace, this.cancel, this.message);
    }

    public boolean validate()
    {
        String searchText = this.search.getText().trim();
        String replaceText = this.replace.getText().trim();

        if(searchText.isEmpty() && replaceText.isEmpty())
        {
            this.message.setText("Missing Search And Replace Keywords");
            return false;
        }
        else if(searchText.isEmpty())
        {
            this.message.setText("Missing Search Keyword");
            return false;
        }
        else if(replaceText.isEmpty())
        {
            this.message.setText("Missing Replace Keyword");
            return false;
        }
        else
        {
            this.message.setText("");
            return true;
        }
    }

    public String[] getKeywords()
    {
        return new String[] {this.search.getText().trim(), this.replace.getText().trim()};
    }

    public void setHandler(EventHandler<? super MouseEvent> handler)
    {
        this.cancel.setOnMouseClicked(handler);
    }
}
