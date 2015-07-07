
/*
import java.awt.*;

import java.awt.geom.*;
import javax.swing.*;

public class tests extends JPanel {

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.draw(new Rectangle2D.Double(115, 95, 50,50));
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Simple Java2D Example");
        frame.add(new tests());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(280, 240);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
}
*/ //the above is a simple display of a shape in a window titled "Simple Java2d Example"



//below are the import statements for the example visualization with prefuse
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JFrame;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.util.io.gml.GMLReader;

import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.layout.RandomLayout;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;

class nodeStruct{
	
	public String nodeId;
	public String label;
	
	public nodeStruct(String nodeId,String label) {
		this.nodeId = nodeId;
		this.label = label;
		
	}
	
	public String toString(){
		return nodeId + label;
	}
	
}
public class graphLayoutProject extends GMLread{


	private static Graph graph; //data
	private static Visualization vis; //the visualize
	private static Display d; // display

	public static void main (String[] args) {
	
	//setUpData(0,null,null,null);
	GMLread();
	setUpVisualization();
	setUpRenderers();
	setUpActions();
	setUpDisplay();
	
	
	//need to set up window for visualization 
	JFrame frame = new JFrame ("Toy Jim");  //txt to be displayed in menubar
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  //ensure app exits when window closed
	frame.add(d); //display object(d) can be added to JFrames with add method
	frame.pack(); //prepares the window
	frame.setVisible(true); //shows the window
	
	//start ActionLists that we added to visualization?????
	vis.run("color");
	vis.run("layout");
}


public static void GMLread() {
	TinkerGraph g = new TinkerGraph();
	
	ArrayList<String> edgeStart = new ArrayList<String>(); 
	ArrayList<String> edgeEnd = new ArrayList<String>();
//	ArrayList<String> nodeIds = new ArrayList<String>();
//	ArrayList<String> nodeLabels = new ArrayList<String>();
	
	List<nodeStruct> nodeProperties = new ArrayList<nodeStruct>();
	
	try{
		//this inputs the data file and uses what is stored in the file
		InputStream in = new FileInputStream("/Users/cgonzale/RuleBender-workspace/DiSCoBio(RuleBender)/results/"
				+ "toy-jim/2015-06-16_15-35-52/toy-jim_contactmap.gml");
		GMLReader.inputGraph(g, in);
	}
	//test
	//another test comment
	//yet another test comment
	catch(IOException exception){}
	
	//iterates through graph and calculates the number value of nodes present and 
	//stores the id value for each node in arraylist nodes
	System.out.println("Vertices of: " + g);
	for (Vertex vertex:g.getVertices()){
		System.out.println(vertex);
		nodeStruct temp = new nodeStruct((String)vertex.getId(), vertex.getProperty("label"));
		nodeProperties.add(temp);
	}
	
//	System.out.println(nodeLabels);
	
//	System.out.println(nodeIds);
	
	//iterates through graph and adds the starting edge values and the ending
	//values to their respective arraylists
	System.out.println("Edges of: " + g);
	for (Edge edge:g.getEdges()){
		System.out.println(edge);
		edgeEnd.add((String) edge.getVertex(Direction.IN).getId());
		edgeStart.add((String) (edge.getVertex(Direction.OUT).getId()));
	} 
	
	//pass all arraylists and ints to setUpData
	setUpData(edgeEnd, edgeStart, nodeProperties); 
	
	
	
}

//load data
//create and populate Graph data structure...here we will create nodes and randomly connect them but you can read 
//in a data structure
public static void setUpData(ArrayList<String> edgeEnd, ArrayList<String> edgeStart, List<nodeStruct> properties){

	ArrayList<String> edge1 = edgeEnd;
	ArrayList<String> edge0 = edgeStart;
	List<nodeStruct> nodeProperties = properties;
	 
	Collections.sort(nodeProperties, new idComparator());
//	Collections.sort(nodeLabels);
	
	System.out.println(nodeProperties);
	//adding more information to our graph structure
	graph = new Graph();
	
	//try this maybe graph.addColumn(VisualItem.LABEL, String.class);
	graph.addColumn("nodeId", String.class);
	graph.addColumn("nodeLabel", String.class);

	//adds the correct number of nodes to the graph 
	//and sets the node Id to be displayed when node 
	//is clicked on
	for (int i = 0; i<nodeProperties.size(); i++){
		Node n = graph.addNode();
		nodeStruct temp = nodeProperties.get(i);
		n.set("nodeId", temp.nodeId); 
		n.set("nodeLabel", temp.label);
	}
	
	 
	//adds the correct number of edges required to graph
	//and connects them to their respective start and end 
	//nodes
	for (int i = 0; i<edge0.size(); i++){
		graph.addEdge(Integer.parseInt((edge0).get(i)), Integer.parseInt((edge1).get(i)));
		System.out.println(Integer.parseInt((edge0).get(i)) + " " + Integer.parseInt((edge1).get(i)));
	}
	
}


//the visualization
public static void setUpVisualization() //simple.. create the obj then add graph obj we just made
{
	//visualization obj
	vis = new Visualization();
	
	//add graph obj to this method and graph gets label so we can reference it later on
	vis.add("graph", graph);
//	vis.setInteractive("graph.edges", null, false);
}


public static void setUpRenderers() //creates ShapeRenderer and tells visualization to use it by default
{
	//final String GRAPH = "graph";
	//final String GRAPH_NODES = GRAPH + ".nodes";
	//final String GRAPH_EDGES = GRAPH + ".edges";
	//final String GRAPH_LABELS = GRAPH + ".labels";
	
	//ShapeRenderer r = new ShapeRenderer(); //default shaperenderer
	//create DefaultRendererFactory and will use the shape renderer for all of its nodes
	//vis.setRendererFactory(new DefaultRendererFactory(r));
	
	
//	LabelRenderer r = new LabelRenderer("nodeId");
	LabelRenderer re = new LabelRenderer("nodeLabel");
	//r.setRoundedCorner(10,10); 
	
    //DefaultRendererFactory rendererFactory = new DefaultRendererFactory();
    //rendererFactory.add(new InGroupPredicate(GRAPH_NODES), nodesRenderer);
    //rendererFactory.add(new InGroupPredicate(GRAPH_LABELS), new LabelRenderer("label"));
 //   vis.setRendererFactory(new DefaultRendererFactory(r));
    vis.setRendererFactory(new DefaultRendererFactory(re));
	
    //return vis;

}

//the actions
public static void setUpActions() //node color, edge color, and layouts...after objs are made they are added to Actionlist
//then the lists are added to visualization
{
	//This stuff permanently changes color FOR NODES
	//color of nodes...we refer to nodes using the TEXT LABEL OF GRAPH then adding ".nodes" same for ".edges"
	ColorAction fill = new ColorAction("graph.nodes", VisualItem.FILLCOLOR, ColorLib.rgb(0,0,255));
	ColorAction text = new ColorAction("graph.nodes", VisualItem.TEXTCOLOR, ColorLib.rgb(255,255,0));
	
	//same for edges
	ColorAction edges = new ColorAction("graph.edges", VisualItem.STROKECOLOR, ColorLib.gray(0));
	
	//create action list containing color assignments... these are used for actions that will be executed at the same time
	ActionList color = new ActionList();
	color.add(fill);
	color.add(edges);
	color.add(text);
	
	ActionList layout = new ActionList();
	//ActionList layout = new ActionList(Activity.INFINITY);
	
	layout.add(new RandomLayout("graph"));
	//layout.add(new ForceDirectedLayout("graph", true));
	
	layout.add(new RepaintAction());
	
	vis.putAction("color",color);
	vis.putAction("layout",layout);
}

//the display
public static void setUpDisplay() //creates display object, sets a size for it, and adds interactive capabilities
{
	//create display obj and pass it the vis that it will hold
	d = new Display(vis);
	
	//set size of the display
	d.setSize(720, 500);
	
	//addControlListener method to set up interaction
	//DragControl is a built in class for moving nodes with the mouse
	d.addControlListener(new DragControl());
	//pan with left click drag on background
	d.addControlListener(new PanControl());
	//zoom with right click drag
	d.addControlListener(new ZoomControl());
	
	//d.addControlListener(new FinalControlListener());
}
}

class idComparator implements Comparator<nodeStruct>{
	@Override
	public int compare(nodeStruct a, nodeStruct b) {
        return Integer.parseInt(a.nodeId) < Integer.parseInt(b.nodeId) ? -1 : Integer.parseInt(a.nodeId) == 
        		Integer.parseInt(b.nodeId) ? 0 : 1; 
    }
}
/*
	public static ArrayList<String> idComparator (ArrayList<String> nodeid){
		ArrayList<String> sortNodeIds = nodeid;
		int temp = 0;
		int a,b = 0;
		int c = Integer.parseInt(sortNodeIds.get(b));
		int d = Integer.parseInt(sortNodeIds.get(b+1));
		for (a = 0; a < sortNodeIds.size(); ++a) {
		    for (b = 0; b < sortNodeIds.size()-1; ++b) {
		        if (Integer.parseInt(sortNodeIds.get(b)) < Integer.parseInt(sortNodeIds.get(b + 1))) {
		            temp = Integer.parseInt(sortNodeIds.get(b));
		            c = d;
		            d = temp;
		        }
		    }
		}
		System.out.println(sortNodeIds);
		return sortNodeIds;
	}
*/