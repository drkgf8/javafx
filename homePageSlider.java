/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slidertimeline;

import java.util.ArrayList;
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author derek
 */
public class SliderTimeline extends Application {
    
    ArrayList<Rectangle> recList = new ArrayList<Rectangle>();
    ArrayList<Integer> distance = new ArrayList<Integer>();
    ArrayList<RadioButton> radButList;
    int leftRec = 0;
    int centerRec = 1;
    int rightRec = 2;
    int outRight = 3;
    int outLeft = -1;
    int highlighted = 0;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    
    @Override
    public void start(Stage stage) throws Exception {
        Group root = new Group();
        Scene scene = new Scene(root, 600, 700, Color.BLACK);
        scene.setFill(Color.WHITE);
        
        Button rightButton = new Button();
        Button leftButton = new Button();
        leftButton.setLayoutX(60);
        leftButton.setLayoutY(60);
        leftButton.setText("leftButton");
        rightButton.setLayoutX(500);
        rightButton.setLayoutY(60);
        rightButton.setText("rightButton");
        
        
        
        
        
        Random rand = new Random();
        int numRecs = rand.nextInt(6)+4;
        recCreate(numRecs);
        initDistance();
        radButList = makeRadButtons(numRecs);
        Group radButGroup = makeRadButtonGroup(radButList);
        System.out.println("recList.size: " + recList.size());
        System.out.println("distance.size:" + distance.size());
        double spacing = setLocation(scene.getHeight(), scene.getWidth());
        double spacingNeg = spacing * -1;
        /*for(int i = 0; i < recList.size(); ++i){
            Rectangle rec = (Rectangle)recList.get(i);
            System.out.println("rec"+i+": "+rec.getX()+" "+rec.getY());
        }*/
        
        Group recGroup = setGroup();
        leftButton.setOnAction(e -> makeTransition(spacingNeg));
        rightButton.setOnAction(e -> makeTransition(spacing));
        
        root.getChildren().addAll(recGroup, leftButton, rightButton, radButGroup);
        stage.setScene(scene);
        stage.show();
    }

    
    public void initDistance(){
        
        for(int i = 0; i < recList.size(); ++i){
            distance.add(0);
        }
    }
    public void makeTransition(double direction){
        
        
        int left = leftRec;
        int center = centerRec;
        int right = rightRec;
        int out = 0;
        if(direction > 0){
            out = outLeft;
        }
        else{
            out = outRight;
        }
        //System.out.println("MakeTransition: "+direction+" "+ left+" "+center+" "+right+" "+out);
        
        
        
        int go = incIdx(direction);
        
        //System.out.println("go: "+go);
        
        
        if(go == 1){
            fixDirection(left, center, right, out, direction);
            System.out.println("in go");
            Timeline timeline1 = makeTimeline(left); 
            Timeline timeline2 = makeTimeline(center);
            Timeline timeline3 = makeTimeline(right);
            Timeline timeline4 = makeTimeline(out);
            
            ParallelTransition pt = new ParallelTransition(timeline1, timeline2, timeline3, timeline4);

            pt.play();
            pt.setOnFinished(e -> setHighlighted(direction));
        }
        else{
            if(highlighted < recList.size()-1){
                setHighlighted(direction);
            }
            
        }
     
    }
    
    public void setHighlighted(double direction){
        if(direction > 0){
            if(highlighted > 0){
                Rectangle eraseHigh = recList.get(highlighted);
                eraseHigh.setStrokeWidth(0);
                --highlighted;
                Rectangle rec = recList.get(highlighted);
                rec.setStroke(Color.BLACK);
                rec.setStrokeWidth(3);
                setRadBut(highlighted);
            }
        }
        if(direction < 0){
            if(highlighted < recList.size()-1){
                Rectangle eraseHigh = recList.get(highlighted);
                eraseHigh.setStrokeWidth(0);
                ++highlighted;
                Rectangle rec = recList.get(highlighted);
                rec.setStroke(Color.BLACK);
                rec.setStrokeWidth(3);
                setRadBut(highlighted);
            }
        }
        
    }
    
    public void setRadBut(int highlighted){
        
        RadioButton rad = radButList.get(highlighted);
        rad.setSelected(true);
    }
    
    public void fixDirection(int left, int center, int right, int out, double direction){
        
        int leftDis = distance.get(left);
        leftDis += direction;
        distance.set(left, leftDis);
        
        int centerDis = distance.get(center);
        centerDis += direction;
        distance.set(center, centerDis);
        
        int rightDis = distance.get(right);
        rightDis += direction;
        distance.set(right, rightDis);
        
        int outDis = distance.get(out);
        outDis += direction;
        distance.set(out, outDis);
        
    }
    
    public Timeline makeTimeline(int idx) {
        
        System.out.println("idx: " + idx);
        System.out.println("distance: " + distance.get(idx));
        Rectangle rec = (Rectangle)recList.get(idx);
        
        KeyValue keyValue = new KeyValue(rec.translateXProperty(), distance.get(idx));
        KeyFrame keyFrame = new KeyFrame(Duration.millis(1000), keyValue);
        
        
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(1);
        timeline.setAutoReverse(false);
        
        return timeline;
    }
    
    public int incIdx(double direction){
        if(direction > 0) {
            if(leftRec != 0){
                --leftRec;
                --centerRec;
                --rightRec;
                --outLeft;
                --outRight;
            }
            else{
                return 0;
            }
        }
        else{
            if(rightRec != recList.size()-1){
                ++leftRec;
                ++centerRec;
                ++rightRec;
                ++outLeft;
                ++outRight;
            }
            else{
                return 0;
            }
        }
        
        
        return 1;
    }
    
    
    public Group setGroup() {
        Group afterOne = new Group();
        for(int i = 0; i < recList.size(); ++i){
            afterOne.getChildren().add((Rectangle)recList.get(i));
        }
        
        return afterOne;
    }
    
    public double setLocation(double height, double width) {
        
        
        double centerLeft = width/2 - 50;
        double centerRight = width/2 + 50;
        double leftMark = centerLeft/2 - 50;
        double rightMark = centerRight+(width-centerRight)/2 - 50;
        double heightFinal = height/3;
        double spacing = centerLeft-leftMark;
        
        for(int i = 1; i <= recList.size(); ++i){
            Rectangle rec = (Rectangle)recList.get(i-1);
            if(i== 1){
               rec.setX(leftMark); 
            }
            else if(i == 2){
                rec.setX(centerLeft);
            }
            else if(i == 3){
                rec.setX(rightMark);
            }
            else {
                rec.setX(width);
                
            }
            rec.setY(heightFinal);
            recList.set(i-1, rec);
            
        }
        
        return spacing;
    }
    
    
    
    public Group makeRadButtonGroup(ArrayList<RadioButton> radButList){
        Group radButGroup = new Group();
        for(int i = 0; i < radButList.size();++i){
            radButGroup.getChildren().add(radButList.get(i));
        }
        return radButGroup;
    }
    
    public ArrayList<RadioButton> makeRadButtons(int numRecs){
        int radSpacing = 200;
        ArrayList<RadioButton> radButtonList = new ArrayList<RadioButton>();
        ToggleGroup group = new ToggleGroup();
        for(int i = 0; i < numRecs; ++i){
            RadioButton button = new RadioButton();
            button.setToggleGroup(group);
            button.setLayoutX(radSpacing);
            button.setLayoutY(600);
            if(i == 0){
                button.setSelected(true);
            }
            radButtonList.add(button);
            radSpacing += 50;
        }
        
        
        return radButtonList;
        
    }
    
    public void recCreate(int num){
        
     
        for(int i = 0; i < num; ++i){
            Rectangle rect = new Rectangle(100f,200f);
            if(i == 0 ){
               rect.setFill(Color.BLUE);
               rect.setStroke(Color.BLACK);
               rect.setStrokeWidth(3);
            }
            else if(i == 1){
                rect.setFill(Color.RED);
            }
            else if(i == 2){
                rect.setFill(Color.GREEN);
                
            }
            else if(i == 3){
                rect.setFill(Color.BLACK);
            }
            else {
                rect.setFill(Color.ORANGE);
            }
            
            recList.add(rect);
        }
        
        
    }
    
}
