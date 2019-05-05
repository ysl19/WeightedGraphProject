import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class WeightedGraphApp extends Application {

	/** Creating UI for application */
	
	//creating an object for button
	private Button buttonMST = new Button("Show MST");
	//create textfield for source Vertex
	private TextField sourceVertexText = new TextField();
	//creating button for all SourcePoint
	private Button showAllSP = new Button("Show All SP From the Source");
	//create textfield for start and end
	private TextField startVertexText = new TextField();
	private TextField endVertexText = new TextField();
	//create button for ShortestPath
	private Button buttonSP = new Button("Show Shortest Path");
	//create arrayList of objs 
	private ArrayList<Vertex> list = new ArrayList<>();
	//create arrayList for edges
	private ArrayList<Edge> edgesList = new ArrayList<>();
	//create label for status
	private Label label = new Label();
	private WeightedGraph<Vertex> graphVar = null;
	//create obj graphview class
	private GraphView viewOb = new GraphView();
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		sourceVertexText.setPrefColumnCount(2);
		startVertexText.setPrefColumnCount(2);
		endVertexText.setPrefColumnCount(2);
		
		/** Creating buttons, labels, texts, vBox, hBox */
		//create HBox1
		HBox hBox1 = new HBox(5);
		//set style
		hBox1.setStyle("-border-color:black");
		hBox1.getChildren().addAll(new Label("Source vertex: "), sourceVertexText, buttonSP);
		//create HBox2
		HBox hBox2 = new HBox(5);
		hBox2.getChildren().addAll(new Label("Starting vertex: "), startVertexText, 
								new Label("Ending vertex: "), endVertexText, buttonSP);
		//create VBox1
		VBox vBox1 = new VBox(5);
		vBox1.setStyle("-border-color:black");
		vBox1.getChildren().addAll(new Label("Find the Shortest Path"), hBox2);
		
		HBox hTypeBox = new HBox(5);
		//setting alignment of box
		hTypeBox.setAlignment(Pos.CENTER);
		hTypeBox.getChildren().addAll(buttonMST, hBox1, vBox1);
		
		/** creating outline for all buttons/texts to be displayed */
		//create BorderPane
		BorderPane pane = new BorderPane();
		//align view to center
		pane.setCenter(viewOb);
		//set position in bottom
		pane.setBottom(hTypeBox);
		//set Label on top
		pane.setTop(label);
		//set alignment of the pane
		BorderPane.setAlignment(label, Pos.CENTER);
		//in the stage create scene and place it
		Scene sceneOb = new Scene(pane, 980, 350);
		//Setting Title
		primaryStage.setTitle("Weighted Graph Project");
		//set the scene
		primaryStage.setScene(sceneOb);
		//display the stage
		primaryStage.show();
		//repaintMethod()
		viewOb.repaintMethod();
		
		/** Action event for buttons */
		
		//buttonMST Action
		buttonMST.setOnAction(e ->
		{
			//call updateGraph() method
			updateGraph();
			WeightedGraph.MST tree = graphVar.getMinimumSpanningTree();
			//set tree
			viewOb.set_Tree(tree);
			//set path is null to repaintMethod()
			viewOb.set_Path(null);
		});
		
		//showAllSP Action
		showAllSP.setOnAction(e ->
		{
			try {
				int u = Integer.parseInt(sourceVertexText.getText().trim());
				if (u < 0 || u >= list.size())
					label.setText("Vertex" + u + " is not in the graph");
				updateGraph();
				AbstractGraph.Tree tree = graphVar.getShortestPath(u);
				//set the tree
				viewOb.set_Tree(tree);
				//call repaintMethod() Method
				viewOb.repaintMethod();
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
			
		});
		
		//buttonSP Action
		buttonSP.setOnAction(e ->
		{
			try {
				int u = Integer.parseInt(startVertexText.getText().trim());
				if (u < 0 || u >= list.size())
					label.setText("Vertex" + u + " is not in the graph");
				int v = Integer.parseInt(endVertexText.getText().trim());
				if (v < 0 || v >= list.size())
					label.setText("Vertex" + u + " is not in the graph");
				
				updateGraph();
				AbstractGraph.Tree tree = graphVar.getShortestPath(u);
				List path = tree.getPath(v);
				
				//set tree to null
				viewOb.set_Tree(null);
				viewOb.set_Path(path);
				
				//call repaint
				viewOb.repaintMethod();
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		});
	}
	
	//Creating updateGraph method
	private void updateGraph() {
		ArrayList listOfedgesObject = new ArrayList<>();
		for (int i = 0; i < edgesList.size(); i++) {
			int x = list.indexOf(edgesList.get(i).u);
			int y = list.indexOf(edgesList.get(i).v);
			int w = (int)Vertex.get_Distance(edgesList.get(i).u, edgesList.get(i).v);
			listOfedgesObject.add(new WeightedEdge(x, y, w));
			listOfedgesObject.add(new WeightedEdge(y, x, w));
		}
		
		graphVar = new WeightedGraph(list, listOfedgesObject);
		}
	
	//create GraphView that extends Pane
	class GraphView extends Pane{
		
		//set starting vertex to null
		private Vertex startingV = null;
		private boolean LineOnOrNot = false;
		private double endOfLineX, endOfLineY;
		
		//set tree to null
		private AbstractGraph.Tree tree = null;
		//set path to null
		private List<Vertex> path = null;
		//create line object
		private Line line = new Line();
		
		GraphView(){
			//set action to perform by pressing on mouse click
			setOnMousePressed(e->
			{
				if (e.getButton() == MouseButton.PRIMARY) {
					Vertex c = get_ContainingVertex(e.getX(), e.getY());
					//execute if its not null
					if (c != null) {
						if (!LineOnOrNot) {
							startingV = c;
							//set true
							LineOnOrNot = true;
							//set x and y to starting value
							line.setStartX(e.getX());
							line.setStartY(e.getY());
							//set x and y to ending value
							line.setEndX(e.getX());
							line.setEndX(e.getY());
						}
					}
					else {
						list.add(new Vertex(e.getX(), e.getY()));
						viewOb.set_Tree(null);
					}
				}
				else if (e.getButton() == MouseButton.SECONDARY) {
					//removes vertex
					Vertex ct = get_ContainingVertex(e.getX(), e.getY());
					if (ct != null) {
						list.remove(ct);
						removeAdjacentEdgesObject(ct);
						//set tree is null to invoke repaint method()
						viewOb.set_Tree(null);
					}
				}
			});
			
			setOnMouseReleased(e ->
			{
				Vertex c = get_ContainingVertex(e.getX(), e.getY());
				if (LineOnOrNot && c != null && !c.equals(startingV)) {
					//Add a new edge
					edgesList.add(new Edge(startingV, c));
					//set tree is null to invote repaint method()
					viewOb.set_Tree(null);
					//set path is null to invoke repaintMethod()
					viewOb.set_Path(null);
				}
				line.setStartX(0);
				line.setStartY(0);
				line.setEndX(0);
				line.setEndY(0);
				LineOnOrNot = false;
				//call repaintMethod
				repaintMethod();
			});
			
			//set mouse on vertex to drag
			setOnMouseDragged(e ->
			{
				if (e.isControlDown()) {
					LineOnOrNot = false;
					Vertex c = get_ContainingVertex(e.getX(), e.getY());
					if (c != null) {
						c.setX(e.getX());
						c.setY(e.getY());
						//set tree to null to invoke repaint
						viewOb.set_Tree(null);
						//set path to null to invoke repaint
						viewOb.set_Path(null);
					}
				}
				else if (LineOnOrNot) {
					line.setEndX(e.getX());
					line.setEndY(e.getY());
					repaintMethod();
				}
			});
		}
		
		//create set_Tree method
		public void set_Tree(AbstractGraph.Tree tree) {
			this.tree = tree;
			repaintMethod();
		}
		
		//create set_Path method
		public void set_Path(List path) {
			this.path = path;
			//call repaint();
			repaintMethod();
		}
		
		//create isCloseToVertex method
		boolean isCloseToVertex(double x, double y) {
			for (int iv = 0; iv < list.size(); iv++) {
				if (Vertex.get_Distance(list.get(iv).get_X(), list.get(iv).getY(), x, y) <= 2 * Vertex.RADIUS + 5) {
					return true;
				}
			}
			return false;
		}
		
		//create get_Containing vertex method
		Vertex get_ContainingVertex(double x, double y) {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).contains(x, y)) {
					return list.get(i);
				}
			}
			return null;
		}
		
		//create remove adj edges obj method
		void removeAdjacentEdgesObject(Vertex vertex) {
			for (int i = 0; i < edgesList.size(); i++) {
				if (edgesList.get(i).u.equals(vertex) || edgesList.get(i).v.equals(vertex)) {
					edgesList.remove(i--);
				}
			}
		}
		
		//create repaint method	
		protected void repaintMethod() {
			getChildren().clear();
			//Draw a line
			if (LineOnOrNot) {
				getChildren().add(line);
			}
			//draw edgesList
			for (int i = 0; i < edgesList.size(); i++) {
				double x1 = edgesList.get(i).u.get_X();
				double y1 = edgesList.get(i).u.getY();
				double x2 = edgesList.get(i).v.get_X();
				double y2 = edgesList.get(i).v.getY();
				getChildren().addAll(new Line(x1, y1, x2, y2));
				
				//draw distance
				double distance = Vertex.get_Distance(x1, y1, x2, y2);
				getChildren().addAll(new Text((x1 + x2)/2,  (y1 + y2)/2, (int)distance +""));
			}
			//call draw method
			draw_Method(20,20);
			//highlighting the edge Object in the spanning tree
			if (tree != null) {
				for (int i = 0; i < graphVar.getSize(); i++) {
					if (tree.getParent(i) != -1) {
						int v = tree.getParent(i);
						double x1 = graphVar.getVertex(i).get_X();
						double y1 = graphVar.getVertex(i).getY();
						double x2 = graphVar.getVertex(v).get_X();
						double y2 = graphVar.getVertex(v).getY();
						
						//call arrow method
						arrowLineDraw(x2, y2, x1, y1, Vertex.RADIUS, this);
					}
				}
			}
			else if(path != null) {
				//Display the path
				for (int i = 0; i < path.size(); i++) {
					double x1 = path.get(i).get_X();
					double y1 = path.get(i).getY();
					double x2 = path.get(i - 1).get_X();
					double y2 = path.get(i - 1).getY();
					arrowLineDraw(x1, y1, x2, y2, Vertex.RADIUS, this);
				}
			}
			//draw nodes
			for (int it = 0; it < list.size(); it++) {
				Circle circle = new Circle(list.get(it).get_X(), list.get(it).getY(), Vertex.RADIUS);
				circle.setFill(Color.WHITE);
				circle.setStroke(Color.BLACK);
				getChildren().addAll(circle, new Text(list.get(it).get_X() - 4, list.get(it).getY() + 4, it + ""));
				
			}			
		}
		final String[] instructions = {"INSTRUCTIONS", "Add:", "Left Click", "Move:", "Ctrl Drag",
				"Connect:", "Drag", "Remove:", "Right Click"};
		//Create draw_Method that instruction to draw
		void draw_Method(int x, int y) {
			Rectangle rectangle = new Rectangle(x, y, x + 150, y + 90);
			rectangle.setFill(Color.WHITE);
			rectangle.setStroke(Color.BLACK);
			
			//call getChildren() method to add
			getChildren().add(rectangle);
			getChildren().add(new Text(x + 10, y + 20, instructions[0]));
			for (int i = 1; i < instructions.length; i = i + 2) {
				getChildren().add(new Text(x + 10, y + 20 + (i + 1) * 10, instructions[i]));
				getChildren().add(new Text(x + 10, y + 20 + (i + 1) * 10, instructions[i + 1]));
			}
		}
	}
	
	/** Create arrowDrawLine method */
	
	private static void arrowLineDraw(double x1, double y1, double x2, double y2, double radius, Pane pane) {
		double d = Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
		int x11 = (int)(x1 - radius * (x1-x2)/ d);
		int y11 = (int)(y1 - radius * (y1-y2)/ d);
		int x21 = (int)(x2 - radius * (x1-x2)/ d);
		int y21 = (int)(y2 - radius * (y1-y2)/ d);
		arrowLineDraw(x11, y11, x21, y21, pane);
	}
	
	//create arrowDrawLine method
	
	public static void arrowLineDraw(double x1, double y1, double x2, double y2, Pane pane) {
		Line lineVar = new Line(x1, y1, x2, y2);
		lineVar.setStroke(Color.RED);
		//call the getChildren method too add line
		pane.getChildren().add(lineVar);
		//Find the slope
		double slope = ((((double) y1) - (double) y2)) / (((double) x1) - (((double) x2)));
		double arctan = Math.atan(slope);
		//This will flip the arrow 45 off a perpendicular line at pt x2
		double setVar = 1.57/2;
		//Arrow always pointed towards the i but not the i + 1
		if (x1 < x2) {
			//add 90 degrees to arrow
			setVar = -1.57 * 1.5;
		}
		
		//Initialize arrlen to 15
		int arrLen = 15;
		//Draw arrow on the line
		Line lineVar2 = new Line(x2, y2, (x2 + (Math.cos(arctan + setVar)* arrLen)), ((y2)) + (Math.sin(arctan + setVar) * arrLen));
		//set color to red
		lineVar2.setStroke(Color.RED);
		//call getchildren to add line2
		pane.getChildren().add(lineVar2);
		Line line3 = new Line(x2, y2, (x2 + (Math.cos(arctan - setVar) * arrLen)), ((y2)) + (Math.sin(arctan - setVar) * arrLen));
		//set color to red
		line3.setStroke(Color.RED);
		//call children to add line3
		pane.getChildren().add(line3);
	}
	
	//create Vertex class
	static class Vertex{
		final static int RADIUS = 20;
		double x, y;
		//create constructor
		public Vertex() {};
		
		//create second constructor
		public Vertex(double x, double y) {
			this.x = x;
			this.y = y;
		}
		
		//create third constructor
		public Vertex(Point p) {
			this(p.x, p.y);
		}
		
		//create get_X() method for double type
		public double get_X() {
			return x;
		}
		
		//create setX() method
		public void setX(double x) {
			this.x = x;
		}
		
		//Create getY() method of double type
		public double getY() {
			return y;
		}
		
		//create setY() 
		public void setY(double y) {
			this.y = y;
		}
		
		//create equals boolean method
		public boolean equals(Object o) {
			Vertex c = (Vertex) o;
			return c.get_X() == x && c.getY() == y;
		}
		
		//create get_Distance() method 1 para
		public double get_Distance(Vertex c) {
			return get_Distance(x, y, c.x, c.y);
		}
		
		//create get_Distance() method with 2 para
		public static double get_Distance(Vertex c1, Vertex c2) {
			return get_Distance(c1.x, c1.y, c2.x, c2.y);
		}
		
		//create with 4 para
		public static double get_Distance(double x1, double y1, double x2, double y2) {
			return Math.sqrt((x1-x2) * (x1-x2) + (y1-y2)* (y1-y2));
		}
		
		public boolean contains(double x1, double y1) {
			return get_Distance(x,y,x1, y1) <= RADIUS;
		}
	}
	
	//create Edge class
	public class Edge{
		Vertex u, v;
		//create constructor
		public Edge (Vertex u, Vertex v) {
			//assign values to u and v
			this.u = u;
			this.v = v;
		}
	}
	
	
	
	public static void main(String[] args) {
		launch(args);
	}
	
}
