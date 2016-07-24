package motion;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.animation.PathTransition.OrientationType;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    
    Button startButton;
    Button pauseButton;
    Button resumeButton;
    Button stopButton;
    Ellipse ellipse;
    Rectangle rectangle;
    Rectangle rectangle2;

    Path path;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        
        
        rectangle2 = new Rectangle(50,50,50,50);
        rectangle = new Rectangle(50,50,50,50);
        rectangle.setLayoutX(100);
        rectangle.setLayoutY(100);
        rectangle2.setLayoutX(105);
        rectangle2.setLayoutY(100);
        rectangle.setFill(Color.BLACK);
        rectangle2.setFill(Color.BLUE);
        double blackBoxX = rectangle.getLayoutX();
        double blackBoxY = rectangle.getLayoutY();
        double blueBoxX = rectangle2.getLayoutX();
        double blueBoxY = rectangle2.getLayoutY();
        System.out.println("rectangle "+blackBoxX+":"+blackBoxY);
        System.out.println("rectangle2 "+blueBoxX+":"+blueBoxY);
        
        //final Image image1 = new Image(getClass().getResourceAsStream("duke_44x80.png"));
        //final ImageView imageView = new ImageView();
        //imageView.setImage(image1);
        
        Path path = new Path();
        path.getElements().add(new MoveTo(75, 75));
        path.getElements().add(new LineTo(35, 75));
        //path.getElements().add(new LineTo(100, 200));
        //path.getElements().add(new LineTo(100, 100));
        //path.getElements().add(new LineTo(200, 100));
        path.setStrokeWidth(1);
        path.setStroke(Color.BLACK);
        PathTransition anim = new PathTransition(new Duration(500.0), path, rectangle);
        anim.setOrientation(OrientationType.NONE);
        anim.setInterpolator(Interpolator.LINEAR);
        anim.setAutoReverse(true);
        anim.setCycleCount((int) 2.0);
        
        Path pathSlide = new Path();
        pathSlide.getElements().add(new MoveTo(80, 75));
        pathSlide.getElements().add(new LineTo(78,75));
        path.setStrokeWidth(1);
        PathTransition animSlide = new PathTransition(new Duration(2000.0), path, rectangle2);
        animSlide.setOrientation(OrientationType.NONE);
        animSlide.setInterpolator(Interpolator.EASE_IN);
        animSlide.setAutoReverse(false);
        animSlide.setCycleCount((int) 1.0);
        //animSlide.setOnFinished(e -> rectangle2.toFront());
        
        
        Rotate rotate = new Rotate(0.0, 0.0, 180.0, 0.0, Rotate.Y_AXIS);
        rectangle.getTransforms().add(rotate);
        RotateTransition rotateTransition = 
            new RotateTransition(Duration.millis(700), rectangle);
        rotateTransition.setByAngle(180f);
        rotateTransition.setCycleCount((int) 1.0);
        rotateTransition.setAutoReverse(true);
        rotateTransition.setAxis(Rotate.Y_AXIS);
        rotateTransition.setFromAngle(0);
        rotateTransition.setToAngle(180);
        rotateTransition.setInterpolator(Interpolator.LINEAR);
        rotateTransition.setRate(1);
        rotateTransition.setOnFinished(e -> rectangle.toBack());
        /**FadeTransition ft = new FadeTransition(Duration.millis(1000), rectangle);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setCycleCount(1);
        ft.setAutoReverse(false);**/
        
        
        ParallelTransition pt = new ParallelTransition(rectangle, rotateTransition, anim, animSlide);
        //pt.playFromStart();
        
        /**Text text = new Text("This is a test");
        text.setX(10);
        text.setY(50);
        text.setFont(new Font(20));

        text.getTransforms().add(new Rotate(30, 50, 30));
        text.setLayoutX(200);
        text.setLayoutY(20);**/
        
        Button startButtonRec = new Button("playRecFromStart");
        startButtonRec.setOnAction(e -> pt.playFromStart());
        Button resetButton = new Button("Reset");
        resetButton.setOnAction(e -> rectangle.toFront());
        //startButtonRec.setOnAction(e -> rectangle.toBack());
        startButtonRec.setLayoutX(60);
        startButtonRec.setLayoutY(420);
        resetButton.setLayoutX(60);
        resetButton.setLayoutY(320);
       
     
        pt.setOnFinished(e -> getFinalDetails());
        
       
        
       
        
     

    
       
        
        Group group = new Group(rectangle, rectangle2, startButtonRec, resetButton);
        rectangle.toFront();
        Scene scene = new Scene(group, 800, 800);

        

        stage.setScene(scene);
        
        stage.show();
    }
    
   public void getFinalDetails() {
        double blackBoxX = rectangle.getLayoutX();
        double blackBoxY = rectangle.getLayoutY();
        double blueBoxX = rectangle2.getLayoutX();
        double blueBoxY = rectangle2.getLayoutY();
        System.out.println("rectangle "+blackBoxX+":"+blackBoxY);
        System.out.println("rectangle2 "+blueBoxX+":"+blueBoxY);
   }
    
}
