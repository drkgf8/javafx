/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package motion;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.animation.PathTransition.OrientationType;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author derek
 */
public class Motion extends Application {
    
    
    ArrayList<Rectangle> recList = new ArrayList<Rectangle>();
    int frontCardIdx = 0;
    Group afterOne;
    Group noteCards;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        
        Group root = new Group();
        Scene scene = new Scene(root, 600, 700, Color.WHITE);
        Random rand = new Random();
        int numRecs = rand.nextInt(6)+4;
        recCreate(numRecs);
        afterOne = makeGroup(frontCardIdx);
        double recHeight = scene.getHeight()/2-100;
        double startWidth = scene.getWidth()/2-50;
        System.out.println("startHeight:startWidth "+recHeight+":"+startWidth);
        setLocation(startWidth, recHeight);
        
        getDetails();
        noteCards = new Group(afterOne, recList.get(0));
        noteCards.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            makeAnimation(startWidth, recHeight);
            
        });
        root.getChildren().addAll(noteCards);
        stage.setScene(scene);
        stage.show();
    }
    public void getDetails(){
        System.out.println("frontCard:seconCard "+ recList.get(0).getX()+":"+recList.get(1).getX());
    }
    
    public void makeAnimation(double width, double height){
        Rectangle frontCard = recList.get(frontCardIdx);
        System.out.println("frontCard.getX: " + frontCard.getX());
        SequentialTransition frontAnim = makeFrontAnimation(frontCard, width, height);
        RotateTransition rotate = makeRotateAnimation(frontCard);
        PathTransition groupSlide = makeSlideAnimation(width+5, height);
        ParallelTransition pt = new ParallelTransition(frontAnim, rotate, groupSlide);
        if(frontCardIdx == recList.size()-1){
            frontCardIdx = 0;
        }
        else{
            ++frontCardIdx;
        }
        
        Timeline timeline = new Timeline(
                new KeyFrame(
                        new Duration(500),
                        new EventHandler<ActionEvent>(){
                            @Override
                            public void handle(ActionEvent event){
                                pt.play();
                            }
                        }
                
                )
        
        );
        timeline.play();
        
    }
    
    public PathTransition makeSlideAnimation(double groupStart, double height){
        
        double x1Width = recList.get(1).getX();
        double xlastWidth = recList.get(recList.size()-1).getX();
        double finalWidth = xlastWidth - x1Width;
        System.out.println("finalWidth: " + finalWidth);
        float recHeight = (float)height + 100;
        System.out.println("groupHeight: " + recHeight);
        System.out.println("groupStart: " + groupStart);
        float midRec = (float)groupStart + 60;
        System.out.println("midRec: " + midRec);
        float slideTo = midRec - 5;
        Path pathSlide = new Path();
        pathSlide.getElements().add(new MoveTo(midRec, recHeight));
        pathSlide.getElements().add(new LineTo(slideTo,recHeight));
        
        PathTransition animSlide = new PathTransition(new Duration(1000.0), pathSlide, afterOne);
        animSlide.setOrientation(OrientationType.NONE);
        animSlide.setInterpolator(Interpolator.EASE_IN);
        animSlide.setAutoReverse(false);
        animSlide.setCycleCount((int) 1.0);
        
        return animSlide;
    }
    
    public RotateTransition makeRotateAnimation(Rectangle frontCard){
        
        Rotate rotate = new Rotate(0.0, 0.0, 180.0, 0.0, Rotate.Y_AXIS);
        frontCard.getTransforms().add(rotate);
        RotateTransition rotateTransition = 
            new RotateTransition(Duration.millis(700), frontCard);
        rotateTransition.setByAngle(180f);
        rotateTransition.setCycleCount((int) 1.0);
        rotateTransition.setAutoReverse(true);
        rotateTransition.setAxis(Rotate.Y_AXIS);
        rotateTransition.setFromAngle(0);
        rotateTransition.setToAngle(180);
        rotateTransition.setInterpolator(Interpolator.LINEAR);
        rotateTransition.setRate(1);
        rotateTransition.setOnFinished(e -> frontCard.toBack());
        FadeTransition ft = new FadeTransition(Duration.millis(1000), frontCard);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setCycleCount(1);
        ft.setAutoReverse(false);
        
        return rotateTransition;
        
    }
    
    public SequentialTransition makeFrontAnimation(Rectangle frontCard, double width, double height){
        
        //float outPlace = (float)width-40+(float)frontCard.getWidth()/2;
        float outPlace = 230f;
        //float backPlace = (float)width+45+(float)frontCard.getWidth()/2;
        int backCardIdx = 0;
        if(frontCardIdx != 0){
            backCardIdx = frontCardIdx-1;
        }
        else{
            backCardIdx = recList.size()-1;
        }
        float backPlace = (float)recList.get(backCardIdx).getX() + 105;
        float cardHeight = (float)height+(float)frontCard.getHeight()/2;
        System.out.println("getHeight: " + (float)frontCard.getHeight()/2);
        System.out.println("outPlace:backPlace:cardHeight "+outPlace+":"+backPlace+":"+cardHeight);
        
        
        Path path = new Path();
        path.getElements().add(new MoveTo(width, cardHeight));
        path.getElements().add(new LineTo(outPlace, cardHeight));
        path.setStrokeWidth(1);
        path.setStroke(Color.BLACK);
        PathTransition anim = new PathTransition(new Duration(500.0), path, frontCard);
        anim.setOrientation(OrientationType.NONE);
        anim.setInterpolator(Interpolator.LINEAR);
        anim.setAutoReverse(false);
        anim.setCycleCount((int) 1.0);
        
        Path returnPath = new Path();
        returnPath.getElements().add(new MoveTo(outPlace, cardHeight));
        returnPath.getElements().add(new LineTo(backPlace, cardHeight));
        returnPath.setStrokeWidth(1);
        returnPath.setStroke(Color.BLACK);
        PathTransition returnAnim = new PathTransition(new Duration(500.0), returnPath, frontCard);
        returnAnim.setOrientation(OrientationType.NONE);
        returnAnim.setInterpolator(Interpolator.LINEAR);
        returnAnim.setAutoReverse(false);
        returnAnim.setCycleCount((int) 1.0);
       
        
        
        SequentialTransition seqTrans = new SequentialTransition(frontCard, anim, returnAnim);
        
        return seqTrans;
        
    }
    
    public Group makeGroup(int frontCard) {
        Group afterOne = new Group();
        for(int i = recList.size()-1; i >= 0; --i){
            if(i != frontCard){
                Rectangle rec = recList.get(i);
                afterOne.getChildren().add(rec);
            }
            
        }
        
        return afterOne;
    }
    
    
    public void setLocation(double startWidth, double recHeight){
        
        
        int spacing = 5;
        
        for(int i = 0; i < recList.size(); ++i){
            Rectangle rec = recList.get(i);
            rec.setX(startWidth);
            rec.setY(recHeight);
            startWidth += spacing;
        }
        
    }
    
   public void recCreate(int num){
        
     
        for(int i = 0; i < num; ++i){
            Rectangle rect = new Rectangle(100f,200f);
            if(i == 0 ){
               rect.setFill(Color.BLUE);
               
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
