package swapper;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;

public class Result extends AnchorPane
{
    private static final String baseStyle = "-fx-background-color: WHITE; -fx-background-radius: 5 5 5 5;" +
            "-fx-border-radius: 5 5 5 5;";

    private static Result currentResult;

    public Result(String header, String description)
    {
        setStyle(Result.baseStyle);

        setPrefWidth(190.0);
        setMinWidth(190.0);

        setPrefHeight(60.0);
        setMinHeight(60.0);

        Label headerLabel = new Label();
        headerLabel.setText(header);
        headerLabel.setFont(new Font("System Bold", 16.0));
        setTopAnchor(headerLabel, 10.0);
        setLeftAnchor(headerLabel, 10.0);

        Label mods = new Label();
        mods.setText(description);
        setTopAnchor(mods, 40.0);
        setLeftAnchor(mods, 10.0);

        getChildren().addAll(headerLabel, mods);
    }

    public void select(boolean highlight)
    {
        if(highlight)
        {
            if(Result.currentResult != null)
            {
                if(Result.currentResult == this)
                    return;

                Result.currentResult.select(false);
            }

            setStyle(Result.baseStyle + " -fx-border-color: rgb(20, 20, 150);");
            Result.currentResult = this;
        }
        else
            setStyle(Result.baseStyle);
    }

}
