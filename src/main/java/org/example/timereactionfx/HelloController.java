package org.example.timereactionfx;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameScene;

public class HelloController extends GameApplication {
    private Random random;
    private String randomDirection;
    private Instant start;
    private Instant stop;
    private Button number;
    private Rectangle vBarre;
    private List<String> impairs;
    private List<String> pairs;
    private List<Button> results;
    private StringProperty stringProperty = new SimpleStringProperty("?");
    enum State {
        ZERO, INIT, START, WAIT, AFF, FINISH;
    }
    private State state = State.INIT;
    private int waitingTime;
    private Button meanRight;
    private StringProperty meanRightStrinProperty = new SimpleStringProperty("-");
    private Button meanLeft;
    private StringProperty meanLeftStrinProperty = new SimpleStringProperty("-");
    private Button mean;
    private StringProperty meanStrinProperty = new SimpleStringProperty("-");

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1000);
        settings.setHeight(800);
        settings.setTitle("Time Reaction");
        settings.setVersion("0.1");
    }

    @Override
    protected void initGame() {

        state = State.ZERO;
        random = new Random(Instant.now().toEpochMilli());
        number = new Button("?");
        number.setMinWidth(150);
        number.textProperty().bind(stringProperty);
        number.setFont(Font.font(76));
        number.setLayoutY(getGameScene().getAppHeight() * 0.15);
        number.setLayoutX(getGameScene().getAppWidth() / 2.0 - 76);
        number.setDisable(false);
        number.setOnMouseClicked(e -> state = State.START);

        vBarre = new Rectangle(5, 400);
        vBarre.setFill(Color.LIGHTGREY);
        vBarre.setLayoutY(getGameScene().getAppHeight() * 0.40);
        vBarre.setLayoutX(getGameScene().getAppWidth() / 2.0);
        vBarre.setDisable(true);

        meanLeft = new Button();
        meanLeft.textProperty().bind(meanLeftStrinProperty);
        meanLeft.setFont(Font.font(26));
        meanLeft.setMinWidth(200);
        meanLeft.setDisable(true);
        meanLeft.setLayoutY(25);
        meanLeft.setLayoutX(50);
        getGameScene().addUINode(meanLeft);

        meanRight = new Button();
        meanRight.textProperty().bind(meanRightStrinProperty);
        meanRight.setFont(Font.font(26));
        meanRight.setMinWidth(200);
        meanRight.setDisable(true);
        meanRight.setLayoutY(25);
        meanRight.setLayoutX(750);
        getGameScene().addUINode(meanRight);

        impairs = new ArrayList<>();
        pairs = new ArrayList<>();
        results = new ArrayList<>();
    }

    @Override
    protected void initUI() {
        getGameScene().addUINode(number);
        getGameScene().addUINode(vBarre);
    }

    @Override
    protected void initInput() {

        FXGL.onKeyDown(KeyCode.RIGHT, () -> {
            stop = Instant.now();
            if (randomDirection.equals(">")){
                addResult(false);
            } else {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        FXGL.onKeyDown(KeyCode.LEFT, () -> {
            stop = Instant.now();
            if (randomDirection.equals("<")){
                addResult(true);
            }
            else {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        });
    }

    private void addResult(boolean left){
        randomDirection = "!";
        Duration duration = Duration.between(start, stop);
        Button res = new Button("" + duration.toMillis());
        res.setFont(Font.font(30));
        res.setDisable(true);
        res.setMinWidth(300);
        res.setAlignment(Pos.CENTER);
        if (left) {
            impairs.add("" + duration.toMillis());
            res.setLayoutX(50);
            res.setLayoutY( getGameScene().getAppHeight() - 50 - impairs.size() * 60);
            int sum = impairs.stream().mapToInt(Integer::parseInt).sum();
            int mean = sum / impairs.size();
            meanLeftStrinProperty.setValue("" + mean);
        } else {
            pairs.add("" + duration.toMillis());
            res.setLayoutX(getGameScene().getAppWidth() / 2.0 + 150);
            res.setLayoutY( getGameScene().getAppHeight() - 50 - pairs.size() * 60);
            int sum = pairs.stream().mapToInt(Integer::parseInt).sum();
            int mean = sum / pairs.size();
            meanRightStrinProperty.setValue("" + mean);
        }

        getGameScene().addUINode(res);
        results.add(res);

        if (pairs.size() > 10 || impairs.size() > 10){
            affScores();
        } else {
            updateValue();
        }
    }

    private void affScores(){
        state = State.FINISH;
    }

    @Override
    protected void onUpdate(double tpf) {
        switch (state) {
            case ZERO -> {
                stringProperty.setValue("?");
            }
            case INIT -> {
                stringProperty.setValue("?");
                impairs.clear();
                pairs.clear();
                meanLeftStrinProperty.setValue("-");
                meanRightStrinProperty.setValue("-");
                results.forEach(r -> getGameScene().removeUINode(r));
                results.clear();
                number.setOnMouseClicked(e -> state = State.START);
            }
            case START -> {
                start = Instant.now();
                stringProperty.setValue("?");
                number.setDisable(true);
                state = State.WAIT;
            }
            case WAIT -> {
                if (Duration.between(start, Instant.now()).toMillis() > waitingTime){
                    randomDirection = random.nextBoolean() ? "<" : ">";
                    start = Instant.now();
                    state = State.AFF;
                }
            }
            case AFF -> {
                stringProperty.setValue(randomDirection);
            }
            case FINISH -> {
                stringProperty.setValue("#");
                number.setDisable(false);
                number.setOnMouseClicked(e -> state = State.INIT);
            }
        }
    }

    void updateValue() {
        state = State.START;
        waitingTime = random.nextInt(500, 2500);
        number.setDisable(true);
        start = Instant.now();
    }

    public static void main(String[] args) {
        launch(args);
    }
}