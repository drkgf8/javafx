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
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextBoundsType;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.VBox;

/**
 *
 * @author derek
 */
public class Motion extends Application {
    
   
    public StringProperty stringData = new SimpleStringProperty();
    ArrayList<Rectangle> recList = new ArrayList<Rectangle>();
    ArrayList<String> frontResults = new ArrayList<String>();
    ArrayList<String> backResults = new ArrayList<String>();
    ArrayList<NoteCard> notecards = new ArrayList<NoteCard>();
    int frontCardIdx = 0;
    int dbBackIdx = 0;
    int dbFrontIdx = 1;
    Group afterOne;
    Group noteCards;
    String data = "";
    
    boolean isFront = true;

    public static void main(String[] args) {
        Application.launch(args);
        
        
    }

    @Override
    public void start(Stage stage) {
        
        fillFront();//fill front and back arrays with db data
        fillBack();
        
        Group root = new Group();
        Scene scene = new Scene(root, 600, 700, Color.WHITE);
        Random rand = new Random();
        int numRecs = rand.nextInt(6)+4;// this is to simulate different #s of notecards
        initNotecards(numRecs);
        
        //data = frontResults.get(0);//
        recCreate(numRecs);
        afterOne = makeGroup();
        /*double recHeight = scene.getHeight()/2-100;
        double startWidth = scene.getWidth()/2-50;
        System.out.println("startHeight:startWidth "+recHeight+":"+startWidth);
        setLocation(startWidth, recHeight);*/
        Button flipButton = new Button();
        //flipButton.setLayoutX(startWidth);
        //flipButton.setLayoutY(recHeight + 150);
        flipButton.setOnAction(e -> {
           rotateCard(); 
        });
        getDetails();
        noteCards = new Group(afterOne, notecards.get(0).getNoteCardFront());
        VBox vbox = new VBox(noteCards, flipButton);
        vbox.setLayoutX(300f);
        vbox.setLayoutY(200f);
        
        noteCards.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            makeAnimation();
            
        });
        /*Text recText = new Text(data);
         StringDataProperty().addListener((observable, oldValue, newValue) -> {
            recText.setText(notecard.getStringData());
        });
        
       
        recText.setFill(Color.WHITE);
        recText.setStyle("fx-font-size: 15px;");
        recText.setBoundsType(TextBoundsType.LOGICAL_VERTICAL_CENTER);
        StackPane sp = new StackPane();
        sp.getChildren().addAll(noteCards, recText);*/
        root.getChildren().addAll(sp, flipButton);
        stage.setScene(scene);
        stage.show();
    }
    
    public void fillFront(){
        PreparedStatement pstmt = null;
        Connection conn = null;
        Statement stmt = null;
        Statement stmtBack = null;
        try {
          // get connection to an Oracle database
          conn = getMySqlConnection();
          /*String query = "select front from notecard where id = ? AND subcategory1='os';";
          pstmt = conn.prepareStatement(query); // create a statement
          pstmt.setInt(1, 1); // set input parameter 1
          pstmt.executeUpdate();*/
          stmt = conn.createStatement();
          String sql;
          sql = "select front from notecard where subcategory1='os';";
          ResultSet rs = stmt.executeQuery(sql);
          while(rs.next()){
              frontResults.add(rs.getString("front"));
          }
              
          }
          
          catch (Exception e) {
          // handle the exception
          e.printStackTrace();
          System.exit(1);
        } finally {
          // release database resources
          try {
            conn.close();
          } catch (Exception ignore) {
          }
        }
          
    }
    
    public void fillBack(){
        PreparedStatement pstmt = null;
        Connection conn = null;
        Statement stmt = null;
        Statement stmtBack = null;
        try {
          // get connection to an Oracle database
          conn = getMySqlConnection();
          /*String query = "select front from notecard where id = ? AND subcategory1='os';";
          pstmt = conn.prepareStatement(query); // create a statement
          pstmt.setInt(1, 1); // set input parameter 1
          pstmt.executeUpdate();*/
          stmt = conn.createStatement();
          String sql;
          sql = "select back from notecard where subcategory1='os';";
          ResultSet rs = stmt.executeQuery(sql);
          while(rs.next()){
              backResults.add(rs.getString("back"));
          }
              
          }
          
          catch (Exception e) {
          // handle the exception
          e.printStackTrace();
          System.exit(1);
        } finally {
          // release database resources
          try {
            conn.close();
          } catch (Exception ignore) {
          }
        }
          
    }
    
    public void initNotecards(int numRecs){
        
        int spacing = 5;
        int startLocation = 5;
        for(int i = 0; i < numRecs; ++i){
            notecards.add(new NoteCard(i, startLocation));
            startLocation += spacing;
        }
    }
    
    public void rotateCard(){
        if(isFront == true){
            isFront = false;
        }
        else{
            isFront = true;
        }
        Rectangle frontCard = recList.get(frontCardIdx);
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
        
        rotateTransition.play();
        
        String cardString = "";
        if(isFront == true){
            cardString = frontResults.get(dbFrontIdx);
        }
        else {
            cardString = backResults.get(dbBackIdx);
        }
        
        notecard.setStringData(cardString);
        
        
    }
    
    public static Connection getMySqlConnection() throws Exception {
    String driver = "com.mysql.jdbc.Driver";//com.mysql.jdbc.Driver
    String url = "jdbc:mysql://localhost/capstonedb";
    String username = "root";
    String password = "Play2winya";
    //dbc:mysql://localhost:3306/capstonedb?zeroDateTimeBehavior=convertToNull [root on Default schema]
    Class.forName(driver);
    Connection conn = DriverManager.getConnection(url, username, password);
    return conn;
  }
  
  
    
    public void getDetails(){
        System.out.println("frontCard:seconCard "+ recList.get(0).getX()+":"+recList.get(1).getX());
    }
    
    public void makeAnimation(){
        double width = notecards.get(0).getRectX();
        double height = notecards.get(0).getRectY();
        Rectangle frontCard = notecards.get(frontCardIdx).getRect();
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
        
        
        ++dbFrontIdx;
        ++dbBackIdx;
        String cardString = frontResults.get(dbFrontIdx);
        setStringData(cardString);
        
        
        
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
        double outPlace = width-145;
        //float backPlace = (float)width+45+(float)frontCard.getWidth()/2;
        int backCardIdx = 0;
        if(frontCardIdx != 0){
            backCardIdx = frontCardIdx-1;
        }
        else{
            backCardIdx = recList.size()-1;
        }
        double backPlace = notecards.get(backCardIdx).getRectX()+205;
        double cardHeight = height + 200;
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
    
    public double getStackWidth(){
        
        double firstX = getFirstX();
        double lastX = getLastX();
        double widthX = lastX - firstX;
        
        return widthX;
    }
    
    public double getFirstX(){
        double min = 10000;
        for(int i = 0; i < notecards.size(); ++i){
            if(notecards.get(i).getRectX() < min){
                min = notecards.get(i).getRectX();
            }
        }
        return min;
    }
    
    public double getLastX(){
        double max = 0;
        for(int i = 0; i < notecards.size(); ++i){
            if(notecards.get(i).getRectX() > max){
                max = notecards.get(i).getRectX();
            }
        }
        return max;
    }
    
    public Group makeGroup() {
        //Group afterOne = new Group();
        for(int i = recList.size()-1; i >= 0; --i){
            if(i != frontCardIdx){
                //Rectangle rec = recList.get(i);
                afterOne.getChildren().add(notecards.get(i).getNoteCardFront());
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
    
    public final String getStringData(){return stringData.get();}
 
    // Define a setter for the property's value
    public final void setStringData(String value){stringData.set(value);}
 
     // Define a getter for the property itself
    public StringProperty StringDataProperty() {return stringData;}
 
   
    
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
   
   
   
   class NoteCard {
   public StringProperty frontData = new SimpleStringProperty();
   public StringProperty backData = new SimpleStringProperty();
   public BooleanProperty isFront = new SimpleBooleanProperty();
   Rectangle rect;
   
   public NoteCard(int idx, int x){
       isFront.set(true);
       setFrontData(frontResults.get(idx));
       setBackData(backResults.get(idx));
       setRectX(x);
       makeRectangle();
       
   }
   
   public final String getFrontData(){return frontData.get();}
 
    // Define a setter for the property's value
    public final void setFrontData(String value){frontData.set(value);}
 
     // Define a getter for the property itself
    public StringProperty FrontDataProperty() {return frontData;}
    
    public final String getBackData(){return backData.get();}
 
    // Define a setter for the property's value
    public final void setBackData(String value){backData.set(value);}
 
     // Define a getter for the property itself
    public StringProperty FrontBackProperty() {return backData;}
   
   
   public final boolean getisFront(){return isFront.get();}
 
    // Define a setter for the property's value
    public final void setisFront(boolean value){
           if(value == true){
               isFront.set(false);
           }
           else{
               isFront.set(true);
           }
                
    }
 
     // Define a getter for the property itself
    public BooleanProperty isFrontProperty() {return isFront;}
    
    
    public void makeRectangle(){
        rect = new Rectangle(200f,400f);
        rect.setStroke(Color.BLACK);
        rect.setStrokeWidth(2);
    }
    
    public StackPane getNoteCardFront(){
        StackPane stack = new StackPane();
        Text text = new Text();
        text.setText(getFrontData());
        text.setStyle("fx-width:150px;");
        text.setStyle("fx-text-align:center;");
        text.setStyle("fx-overflow:scroll;");
        stack.getChildren().addAll(rect, text);
        
        return stack;
        
    }
    
    public double getRectX(){
        return rect.getX();
    }
    
    public void setRectX(int x){
        rect.setX(x);
    }
    
    public double getRectY(){
        return rect.getY();
    }
    
    public Rectangle getRect(){
        return rect;
    }
 
   }
}
