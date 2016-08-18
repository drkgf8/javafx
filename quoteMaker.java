/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quotemaker;

import javafx.beans.property.*;
import javafx.application.Application;
import javafx.beans.value.*;
import javafx.event.*;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.ArrayList;

import java.util.Random;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;


public class QuoteMaker extends Application {
  
  final Random random = new Random();
  
  final Colors colors = new Colors();
  
  Stack focusStack;
  Label newQuote = new Label();
  StackPane sp = new StackPane();
  final ObjectProperty<Label> selectedQuote = new SimpleObjectProperty<>();
  String cardIdx;
  MessageBoard messageBoard;
  Label background = new Label();

  public static void main(String[] args) throws Exception { launch(args); }
  public void start(final Stage stage) throws Exception {
    
    messageBoard = new MessageBoard();
    int depth = 70; //Setting the uniform variable for the glow width and height
 
    DropShadow borderGlow= new DropShadow();
    borderGlow.setOffsetY(0f);
    borderGlow.setOffsetX(0f);
    borderGlow.setColor(Color.BLUE);
    borderGlow.setWidth(depth);
    borderGlow.setHeight(depth);

    //messageBoard.setEffect(borderGlow); //Apply the borderGlow effect to the JavaFX node
    messageBoard.setStyle("-fx-background-color: cornsilk; -fx-background-insets: 10; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, purple, 10, 0, 0, 0);");
    messageBoard.setPrefSize(1000, 700);
    String stackParam1 = "os";
    String stackParam2 = "chapter1";
    //focusStack = new Stack();
    makeStack(stackParam1, stackParam2);
    stage.setTitle("NotePad Breeze");

    

    // create a control panel for the message board.
    VBox controls = new VBox(10);
    
    //#8080ff
    //#b3b3ff
    controls.setStyle("-fx-background-color: #ccccff; -fx-padding: 10;");
    controls.setAlignment(Pos.TOP_CENTER);

    // create some sliders to modify properties of the existing quote.
    final LabeledSlider widthSlider = new LabeledSlider("Width", 100, 1000, 100);
    final LabeledSlider heightSlider = new LabeledSlider("Height", 100, 700, 100);
    final LabeledSlider layoutXSlider = new LabeledSlider("X Pos", 0, messageBoard.getWidth(), 0);
    layoutXSlider.slider.maxProperty().bind(messageBoard.widthProperty());
    final LabeledSlider layoutYSlider = new LabeledSlider("Y Pos", 0, messageBoard.getHeight(), 0);
    layoutYSlider.slider.maxProperty().bind(messageBoard.heightProperty());
   
    Text relatedTitle = new Text();
    relatedTitle.setText("Related Stacks");
    
    relatedTitle.setStyle("-fx-text-fill: #6666ff; -fx-font: 16px 'Times New Roman';");
    initMessageBoard(widthSlider, heightSlider);
    final Label quotedText = new Label();
    quotedText.setWrapText(true);
    quotedText.setStyle("-fx-font-size: 16px;");
    
    selectedQuote.addListener(new ChangeListener<Label>() {
      @Override public void changed(ObservableValue<? extends Label> observableValue, Label oldQuote, final Label newQuote) {
        if (oldQuote != null) {
          // disassociate the sliders from the old quote.
          widthSlider.slider.valueProperty().unbindBidirectional(oldQuote.prefWidthProperty());
          heightSlider.slider.valueProperty().unbindBidirectional(oldQuote.prefHeightProperty());
          layoutXSlider.slider.valueProperty().unbindBidirectional(sp.layoutXProperty());
          layoutYSlider.slider.valueProperty().unbindBidirectional(sp.layoutYProperty());
        }

        if (newQuote != null) {
          // associate the sliders with the new quote.
          widthSlider.slider.valueProperty().bindBidirectional(newQuote.prefWidthProperty());
          heightSlider.slider.valueProperty().bindBidirectional(newQuote.prefHeightProperty());
          
          layoutXSlider.slider.valueProperty().bindBidirectional(sp.layoutXProperty());
          layoutYSlider.slider.valueProperty().bindBidirectional(sp.layoutYProperty());

          
          quotedText.textProperty().bind(newQuote.textProperty());
        }
      }
    });
    
    
    //postMessageBoard(messageBoard, widthSlider, heightSlider);
    //Button animate = new Button("animate");
    ImageView logo = new ImageView(new Image("logo1.PNG"));
    logo.setFitWidth(180);
    logo.setFitHeight(60);
    VBox relatedNotecards = getRelated();
    relatedNotecards.setStyle(" -fx-background-color: cornsilk; -fx-background-insets: 3; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, #ccccff, 10, 0, 0, 0);");
    Text dimensions = new Text();
    dimensions.setText("Notecard\nDimensions");
    dimensions.setStyle("-fx-text-fill: #6666ff; -fx-font: 16px 'Times New Roman';");
    controls.getChildren().addAll(logo, relatedTitle, new Separator(), relatedNotecards, new Separator(), dimensions, new Separator(), widthSlider, heightSlider, layoutXSlider, layoutYSlider, new Separator());
    controls.setPrefWidth(180);
    controls.setMinWidth(180);
    controls.setMaxWidth(Control.USE_PREF_SIZE);

    

    // layout the scene.
    HBox layout = new HBox();
    layout.getChildren().addAll(controls, messageBoard);
    HBox.setHgrow(messageBoard, Priority.ALWAYS);
    final Scene scene = new Scene(layout);

    // allow the selected quote to be deleted.
    scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
      @Override public void handle(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.DELETE)) {
          if (selectedQuote.get() != null) {
            messageBoard.getChildren().remove(selectedQuote.get());
          }
        }
      }
    });

    // show the stage.
    stage.setScene(scene);
    stage.show();
    
    //animate.setOnAction(e-> makeAnimation(newQuote));
  }
  
  public void makeStack(String param1, String param2){
      String title = param1 +" " + param2;
      Stack stack = new Stack(title);
      ArrayList<String> front = fillFront(param1, param2);
      ArrayList<String> back = fillBack(param1, param2);
      stack.related = getRelatedQuery();
      for(int i = 0; i < front.size(); ++i){
          stack.notecards.add(new Notecard(front.get(i), back.get(i)));
      }
      
      focusStack = stack;
      
      if(selectedQuote.get() != null){
          postMessageBoard();
      }
      
  }
  //init and post message boards have different signatures because of constraints with the change listener in start
  public void initMessageBoard(LabeledSlider widthSlider, LabeledSlider heightSlider){
      String myLabel = "myLabel";
      String backLabel = "back";
      final StackPane sp = messageBoard.post(focusStack.notecards.get(focusStack.index).getFrontData(), colors.next());
        //focusStack.incIndex();
        for(int i = 0; i < 3; ++i){
            if(myLabel.equals(sp.getChildren().get(i).getId())){
                newQuote = (Label)sp.getChildren().get(i);
            }
            
        }
        
        selectedQuote.set(newQuote);
        // make the new quote the selected quote when it is been clicked.
        sp.setOnMouseClicked(new EventHandler<MouseEvent>() {
          @Override public void handle(MouseEvent mouseEvent) {
            selectedQuote.set(newQuote);
            sp.toFront();
          }
        });
        
        widthSlider.slider.valueProperty().bindBidirectional(newQuote.prefWidthProperty());
        heightSlider.slider.valueProperty().bindBidirectional(newQuote.prefHeightProperty());
        
  }
  
  public void postMessageBoard(){
      System.out.println("in post");
      String myLabel = "myLabel";
      final StackPane sp = messageBoard.post(focusStack.notecards.get(focusStack.index).getFrontData(), colors.next());
      
      //int numChildren = messageBoard.getChildren().size();
      //System.out.println("numChildren " + numChildren);
        //focusStack.incIndex();
        for(int i = 0; i < 3; ++i){
            if(myLabel.equals(sp.getChildren().get(i).getId())){
                newQuote = (Label)sp.getChildren().get(i);
            }
        }
        
        selectedQuote.set(newQuote);
        // make the new quote the selected quote when it is been clicked.
        sp.setOnMouseClicked(new EventHandler<MouseEvent>() {
          @Override public void handle(MouseEvent mouseEvent) {
            selectedQuote.set(newQuote);
            sp.toFront();
          }
        });
        
        //widthSlider.slider.valueProperty().bindBidirectional(newQuote.prefWidthProperty());
        //heightSlider.slider.valueProperty().bindBidirectional(newQuote.prefHeightProperty());
  }
  
  
  public VBox getRelated(){
      VBox temp = new VBox();
     
      
      for(int i = 0; i < focusStack.related.size(); ++i){
          Label label = new Label();
          String relatedResults = focusStack.related.get(i);
          String[] resultsArr = relatedResults.split(" ");//split up the db keywords and send them to makeStack
          final String param1 = resultsArr[0];
          final String param2 = resultsArr[1];
          label.setOnMouseClicked(new EventHandler<MouseEvent>() {
          @Override public void handle(MouseEvent mouseEvent) {
            makeStack(param1, param2);
          }
        });
          label.setText(relatedResults);
          //String bgColor = "#" + color.deriveColor(color.getHue(), color.getSaturation(), color.getBrightness(), random.nextDouble() * 0.5 + 0.5).toString().substring(2, 10);
          label.setStyle("-fx-background-radius: 5; -fx-background-color: #6666ff; -fx-text-fill: white; -fx-font: 12px 'Segoe Script'; -fx-padding:10;");
          
          //label.setStyle("-fx-box-shadow: 0 0 0 3px #fff, 0 0 0 5px #ddd, 0 0 0 10px #fff, 0");
          label.setWrapText(true);
          label.setAlignment(Pos.CENTER);
          label.setTextAlignment(TextAlignment.CENTER);
          final DropShadow dropShadow = new DropShadow();
          final Glow glow = new Glow();
          label.setEffect(dropShadow);
          label.setWrapText(true);
          label.setMaxWidth(Double.MAX_VALUE);
          temp.getChildren().add(label);
      }
      
      return temp;
  }
  

  
  
  public ArrayList<String> getRelatedQuery(){
        ArrayList<String> related = new ArrayList<String>();
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
          sql = "select distinct subcategory1, stackname from notecard;";
          ResultSet rs = stmt.executeQuery(sql);
          while(rs.next()){
              String subcategory = (rs.getString("subcategory1"));
              String stackname = (rs.getString("stackname"));
              String labelTitle = subcategory + " " + stackname;
              related.add(labelTitle);
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
        
        return related;
  }

  class LabeledSlider extends HBox {
    Label label;
    Slider slider;
    LabeledSlider(String name, double min, double max, double value) {
      slider = new Slider(min, max, value);
      label = new Label(name);
      label.setPrefWidth(60);
      label.setLabelFor(slider);
      this.getChildren().addAll(label, slider);
    }
  }

  // a board on which you can place messages.
  class MessageBoard extends Pane {
    MessageBoard() {
      setId("messageBoard");
    }

    StackPane post(String quote, Color color) {
      System.out.println("in messageboard.post");
      final Label label = new Label(quote);
      label.setId("myLabel");
      //-fx-background-image: url('flipThickGrey.png');ladder(" + bgColor +", lavender 49%, midnightblue 50%);-fx-opacity: 0.5;
      String bgColor = "#" + color.deriveColor(color.getHue(), color.getSaturation(), color.getBrightness(), random.nextDouble() * 0.5 + 0.5).toString().substring(2, 10);
      label.setStyle("-fx-background-radius: 5; -fx-background-color: linear-gradient(to bottom, " + bgColor + ", derive(" + bgColor + ", 20%)); -fx-text-fill:black;  -fx-font: 18px 'Segoe Script'; -fx-font-weight: bold;; -fx-padding:10; -fx-border-color: white; -fx-border-width: 4px; -fx-background-image: url('notecardBackFixed.png');");
      label.setWrapText(true);
      label.setAlignment(Pos.CENTER);
      label.setTextAlignment(TextAlignment.CENTER);
      final DropShadow dropShadow = new DropShadow();
      final Glow glow = new Glow();
      label.setEffect(dropShadow);
      /*final Label labelBack = new Label();
      labelBack.setStyle("-fx-background-image: url('borderRope.jpg');");
      labelBack.setPrefSize(236, 283);
      labelBack.setId("back");*/

      // give the quote a random fixed size and position.
      label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
      int prefSizeX = 500;
      int prefSizeY = 300;
      //int prefSizeX = random.nextInt(150) + 300;
      //int prefSizeY = random.nextInt(150) + 75;
      label.setPrefSize(prefSizeX, prefSizeY);
      label.setMaxSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
      int x = random.nextInt((int) Math.floor(this.getPrefWidth() - label.getPrefWidth()));
      int y = random.nextInt((int) Math.floor(this.getPrefHeight() - label.getPrefHeight()));
      
      label.relocate(
        x,y
      );
      
      
      // allow the label to be dragged around.
      StackPane sp = new StackPane();
      final Delta dragDelta = new Delta();
      label.setOnMousePressed(new EventHandler<MouseEvent>() {
        @Override public void handle(MouseEvent mouseEvent) {
          // record a delta distance for the drag and drop operation.
          dragDelta.x = label.getLayoutX() - mouseEvent.getSceneX();
          dragDelta.y = label.getLayoutY() - mouseEvent.getSceneY();
          label.setCursor(Cursor.MOVE);
        }
      });
      label.setOnMouseReleased(new EventHandler<MouseEvent>() {
        @Override public void handle(MouseEvent mouseEvent) {
          label.setCursor(Cursor.HAND);
        }
      });
      label.setOnMouseDragged(new EventHandler<MouseEvent>() {
        @Override public void handle(MouseEvent mouseEvent) {
          label.setLayoutX(mouseEvent.getSceneX() + dragDelta.x);
          label.setLayoutY(mouseEvent.getSceneY() + dragDelta.y);
        }
      });
      label.setOnMouseEntered(new EventHandler<MouseEvent>() {
        @Override public void handle(MouseEvent mouseEvent) {
          label.setCursor(Cursor.HAND);
          dropShadow.setInput(glow);
        }
      });
      label.setOnMouseExited(new EventHandler<MouseEvent>() {
        @Override public void handle(MouseEvent mouseEvent) {
          dropShadow.setInput(null);
        }
      });
      
      ////////////////////////////////////////////
      sp.setOnMousePressed(new EventHandler<MouseEvent>() {
        @Override public void handle(MouseEvent mouseEvent) {
          // record a delta distance for the drag and drop operation.
          dragDelta.x = sp.getLayoutX() - mouseEvent.getSceneX();
          dragDelta.y = sp.getLayoutY() - mouseEvent.getSceneY();
          sp.setCursor(Cursor.MOVE);
        }
      });
      sp.setOnMouseReleased(new EventHandler<MouseEvent>() {
        @Override public void handle(MouseEvent mouseEvent) {
          sp.setCursor(Cursor.HAND);
        }
      });
      sp.setOnMouseDragged(new EventHandler<MouseEvent>() {
        @Override public void handle(MouseEvent mouseEvent) {
          sp.setLayoutX(mouseEvent.getSceneX() + dragDelta.x);
          sp.setLayoutY(mouseEvent.getSceneY() + dragDelta.y);
        }
      });
      sp.setOnMouseEntered(new EventHandler<MouseEvent>() {
        @Override public void handle(MouseEvent mouseEvent) {
          sp.setCursor(Cursor.HAND);
          dropShadow.setInput(glow);
        }
      });
      sp.setOnMouseExited(new EventHandler<MouseEvent>() {
        @Override public void handle(MouseEvent mouseEvent) {
          dropShadow.setInput(null);
        }
      });
      //Button icon = new Button();
      ImageView imageIcon = new ImageView(new Image("Icon.PNG"));
      imageIcon.setFitWidth(40);
      imageIcon.setFitHeight(40);
      
      String title = focusStack.title.toUpperCase();
      
      Text notecardTitle = new Text();
      notecardTitle.setText(title);
      notecardTitle.setStyle("-fx-text-fill: #6666ff; -fx-font: 16px 'Times New Roman';");
      String totalCards = Integer.toString(focusStack.notecards.size());
      Text numNotecards = new Text();
      System.out.println("focuStack.index: " + focusStack.index);
      
      focusStack.IdxProperty().addListener((observable, oldValue, newValue) -> {
          System.out.println("fs.get " + focusStack.getIdx());
          cardIdx = Integer.toString(focusStack.getIdx());
            
            numNotecards.setText(cardIdx+"|"+totalCards);
      });
      cardIdx = Integer.toString(focusStack.getIdx());
      
      numNotecards.setText(cardIdx+"|"+totalCards);
      
      Button flipIt = new Button();
      ImageView im = new ImageView(new Image("flipThickGrey.png"));
      im.setFitWidth(20);
      im.setFitHeight(20);
      flipIt.setGraphic(im);
      flipIt.setOnAction(e -> {
          if(focusStack.notecards.get(focusStack.index).isFront){
              label.setText(focusStack.notecards.get(focusStack.index).getBackData());
              focusStack.notecards.get(focusStack.index).setisFront(false);
          }
          else{
              label.setText(focusStack.notecards.get(focusStack.index).getFrontData());
              focusStack.notecards.get(focusStack.index).setisFront(true);
          }
          
              });
      Button next = new Button();
      ImageView imNext = new ImageView(new Image("rightArrowGrey.png"));
      imNext.setFitWidth(20);
      imNext.setFitHeight(20);
      next.setGraphic(imNext);
      next.setOnAction(e -> {
          if(focusStack.index < focusStack.notecards.size()-1){//index has to restart when it hits the size of the array
              focusStack.incIndex();
          }
          else{
              focusStack.resetIndex();
          }
          label.setText(focusStack.notecards.get(focusStack.index).getFrontData());
              });
      //StackPane sp = new StackPane();
      StackPane.setAlignment(flipIt, Pos.BOTTOM_LEFT);
      StackPane.setAlignment(next, Pos.BOTTOM_RIGHT);
      StackPane.setAlignment(imageIcon, Pos.TOP_LEFT);
      StackPane.setAlignment(notecardTitle, Pos.TOP_CENTER);
      StackPane.setAlignment(numNotecards, Pos.TOP_RIGHT);
      sp.getChildren().addAll(label, flipIt, next, imageIcon, notecardTitle, numNotecards);
      sp.relocate(x, y);
      System.out.println("sp x:y " +sp.getLayoutX() + ":" + sp.getLayoutY());
      this.getChildren().addAll(sp);
      
      

      return sp;
    }
  }

  // records relative x and y co-ordinates.
  class Delta { double x, y; }

 

  // a selection of colors for the text boxes.
  class Colors {
    final String[][] smallPalette = {
      {"aliceblue", "#f0f8ff"},{"antiquewhite", "#faebd7"},{"aqua", "#00ffff"},{"aquamarine", "#7fffd4"},
      {"azure", "#f0ffff"},{"beige", "#f5f5dc"},{"bisque", "#ffe4c4"},{"black", "#000000"},
      {"blanchedalmond", "#ffebcd"},{"blue", "#0000ff"},{"blueviolet", "#8a2be2"},{"brown", "#a52a2a"},
      {"burlywood", "#deb887"},{"cadetblue", "#5f9ea0"},{"chartreuse", "#7fff00"},{"chocolate", "#d2691e"},
      {"coral", "#ff7f50"},{"cornflowerblue", "#6495ed"},{"cornsilk", "#fff8dc"},{"crimson", "#dc143c"},
      {"cyan", "#00ffff"},{"darkblue", "#00008b"},{"darkcyan", "#008b8b"},{"darkgoldenrod", "#b8860b"},
    };

    private Color next() {
      return Color.valueOf(smallPalette[random.nextInt(smallPalette.length)][0]);
    }
  }
  
  public ArrayList<String> fillFront(String param1, String param2){
        ArrayList<String> front = new ArrayList<String>();
        PreparedStatement pstmt = null;
        Connection conn = null;
        Statement stmt = null;
        Statement stmtBack = null;
        try {
          // get connection to an Oracle database
          conn = getMySqlConnection();
          
          
          stmt = conn.createStatement();
          String sql;
          String os = "os";
          sql = "select front from notecard where subcategory1=? AND stackname=?;";
          pstmt = conn.prepareStatement(sql); // create a statement
          pstmt.setString(1, param1); // set input parameter 1
          pstmt.setString(2, param2);
          ResultSet rs = pstmt.executeQuery();
          //ResultSet rs = stmt.executeQuery(sql);
          while(rs.next()){
              
              front.add(rs.getString("front"));
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
     return front;     
    }
    
    public ArrayList<String> fillBack(String param1, String param2){
        ArrayList<String> back = new ArrayList<String>();
        PreparedStatement pstmt = null;
        Connection conn = null;
        Statement stmt = null;
        Statement stmtBack = null;
        try {
          // get connection to an Oracle database
          conn = getMySqlConnection();
          
          stmt = conn.createStatement();
          String sql;
          sql = "select back from notecard where subcategory1=? AND stackname=?;";
          pstmt = conn.prepareStatement(sql); // create a statement
          pstmt.setString(1, param1); // set input parameter 1
          pstmt.setString(2, param2);
          ResultSet rs = pstmt.executeQuery();
          
          while(rs.next()){
              //.backs.add(rs.getString("back"));
              back.add(rs.getString("back"));
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
     return back;     
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
    
     
     class Notecard {
         String frontData;
         String backData;
         boolean isFront;
         int index = 0;
         
         public Notecard(String front, String back){
            setisFront(true);
            setFrontData(front);
            setBackData(back);
         }
         
         public void setFrontData(String data){
             frontData = data;
         }
         
         public String getFrontData(){
             return frontData;
         }
         
         public void setBackData(String data){
             backData = data;
         }
         
         public String getBackData(){
             return backData;
         }
         
         public void setisFront(boolean bool){
             isFront = bool;
         }
         
         public boolean getisFront(){
             return isFront;
         }
     }
     
     class Stack {
         int index;
         public IntegerProperty Idx = new SimpleIntegerProperty();
         String title;
         ArrayList<Notecard> notecards;
         ArrayList<String> related = new ArrayList<String>(); // These are query keywords, unitialized stacks. 
         public Stack(String stackTitle){
             notecards = new ArrayList<Notecard>();
             title = stackTitle;
             resetIndex();
         }
         
         public int getIdx(){
             return Idx.get();
         }
         
         public void setIdx(int value){
             System.out.println("value: "+value);
             Idx.set(value);
         }
         
         public IntegerProperty IdxProperty(){
             return Idx;
         }
         
         public void incIndex(){
             index += 1;
             System.out.println("incIndex: " + index);
             setIdx(index+1);
         }
         
         public void decIndex(){
             index -= 1;
             System.out.println("decIndex: " + index);
             setIdx(index+1);
         }
         
         public void resetIndex(){
             index = 0;
             System.out.println("resIndex: " + index);
             setIdx(index+1);
         }
         
         
     }
    
    
}