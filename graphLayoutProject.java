
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
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.util.io.gml.GMLReader;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.CompositeAction;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.layout.Layout;
import prefuse.action.layout.RandomLayout;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.ControlAdapter;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.render.PolygonRenderer;
import prefuse.render.Renderer;
import prefuse.util.ColorLib;
import prefuse.util.GraphicsLib;
import prefuse.visual.AggregateItem;
import prefuse.visual.AggregateTable;
import prefuse.visual.VisualGraph;
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

public class graphLayoutProject extends GMLread {

	
	private static final String AGGR = "aggregates";
	private static final String NODES = "graph.nodes";
	private static final String EDGES = "graph.edges";
	private static final String GRAPH = "graph";
	
	private static Graph graph; //data
	private static Visualization vis; //the visualize
	private static VisualGraph vg;
	private static Display d; // display
	static Multimap<String, String> aggrValues = HashMultimap.create(); //multimap for aggregate keys and values

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


public static void GMLread(){
	TinkerGraph g = new TinkerGraph();
	
	ArrayList<String> edgeStart = new ArrayList<String>(); 
	ArrayList<String> edgeEnd = new ArrayList<String>();
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
		
		//v trying to make hashmap v
		if (vertex.getProperty("isGroup") != null){
			//nodeInfo.put((String)vertex.getId(), 0);
			aggrValues.putAll((String)vertex.getId(), new ArrayList<String>());
		}
		
		else if (vertex.getProperty("isGroup") == null){
			aggrValues.put(Integer.toString(vertex.getProperty("gid")),(String) vertex.getId());
		}
		
	}
	System.out.println(aggrValues);
	System.out.println(aggrValues.get("0"));

	
	//iterates through graph and adds the starting edge values and the ending
	//values to their respective arraylists
	System.out.println("Edges of: " + g);
	for (Edge edge:g.getEdges()){
		System.out.println(edge);
		edgeEnd.add((String) edge.getVertex(Direction.IN).getId());
		edgeStart.add((String) (edge.getVertex(Direction.OUT).getId()));
	} 
	
	
	/* failed attempt to make hashmap 
	List<getGid> gid = new ArrayList<getGid>(nodeProperties.size());
	for (Vertex vertex:g.getVertices()){
		gid.add((getGid)vertex.getProperty("gid"));
	}
	System.out.println(gid);
	Collections.sort(gid, new gidComparator());
	System.out.println(gid);
	
	for (int i = 0; i<nodeProperties.size(); i++){
		//v trying to create a HashTable v
		for (Vertex vertex:g.getVertices()){
			if(gid.get(i) == (vertex.getId())){
				nodeInfo.put(vertex.getProperty("label"), (Integer) vertex.getId());
			}
		}
	}
	System.out.println(nodeInfo);
	*/
	
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
	
	//------------------------------------------------------
	/*
	vis.addGraph(GRAPH, graph);
    //vis.setInteractive(EDGES, null, false);
    //vis.setValue(NODES, null, VisualItem.SHAPE,
    //        new Integer(Constants.SHAPE_ELLIPSE));
    
    AggregateTable at = vis.addAggregates(AGGR);
    at.addColumn(VisualItem.POLYGON, float[].class);
    at.addColumn("id", int.class);
    // add nodes to aggregates
    // create an aggregate for each 3-clique of nodes
    //Iterator<?> nodes = vg.nodes();
    for ( int i=0; i<aggrValues.size(); ++i ) {
        AggregateItem aitem = (AggregateItem)at.addItem();
        aitem.setInt("id", i);
        for ( int j=0; j<aggrValues.size(); ++j ) {
            aitem.addItem((VisualItem)((Iterator<?>) aggrValues.get(Integer.toString(j))).next());
        }
    }
    */
    //------------------------------------------------------
	
}


//the visualization
public static void setUpVisualization() //simple.. create the obj then add graph obj we just made
{
	//visualization obj
	vis = new Visualization();
	
	//add graph obj to this method and graph gets label so we can reference it later on
	//vis.addGraph(GRAPH, graph);
//	vis.setInteractive("graph.edges", null, false);
	
	//------------------------------------------------------
	vg = vis.addGraph(GRAPH, graph);
	vis.setInteractive(EDGES, null, false);
	vis.setValue(NODES, null, VisualItem.SHAPE, new Integer(Constants.SHAPE_ELLIPSE));
	AggregateTable at = vis.addAggregates(AGGR);
	at.addColumn(VisualItem.POLYGON, float[].class);
	at.addColumn("id", int.class);
    // add nodes to aggregates
    // create an aggregate for each 3-clique of nodes
	/*
	for ( int i=0; i<aggrValues.size(); ++i ) {
		AggregateItem aitem = (AggregateItem)at.addItem();
		aitem.setInt("id", i);
		for ( int j=0; j<aggrValues.get(Integer.toString(i)).size(); j++) {
			Node node = graph.getNode(aggrValues.get(Integer.toString(i)).get(Integer.toString(j)));
			aitem.addItem((VisualItem) ((VisualItem) aggrValues.get(Integer.toString(i))).get(Integer.toString(j)));
		}ljkhl
	}
	*/
	
	for(String groupId: aggrValues.keySet()){
		System.out.println("----"+groupId);
		AggregateItem aitem = (AggregateItem)at.addItem();
		aitem.set("id", groupId);
		for(String nodeId: aggrValues.get(groupId)){
			aitem.addItem( (VisualItem) (vg.getNode(Integer.parseInt(nodeId))) );
		}
	}
}


public static void setUpRenderers() //creates ShapeRenderer and tells visualization to use it by default
{
	//LabelRenderer r = new LabelRenderer("nodeId");
	LabelRenderer re = new LabelRenderer("nodeLabel");
	//r.setRoundedCorner(10,10); 
	
	
	Renderer polyR = new PolygonRenderer(Constants.POLY_TYPE_CURVE);
    ((PolygonRenderer)polyR).setCurveSlack(0.15f);
    
    DefaultRendererFactory drf = new DefaultRendererFactory();
    drf.setDefaultRenderer(re);
    drf.add("ingroup('aggregates')", polyR);
    vis.setRendererFactory(drf);
 
    //vis.setRendererFactory(new DefaultRendererFactory(re));
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
	
	ColorAction aStroke = new ColorAction(AGGR, VisualItem.STROKECOLOR);
    aStroke.setDefaultColor(ColorLib.gray(200));
  
    int[] palette = new int[] {ColorLib.rgba(255,200,200,150), ColorLib.rgba(200,255,200,150), ColorLib.rgba(200,200,255,150)};
    ColorAction aFill = new DataColorAction(AGGR, "id", Constants.NOMINAL, VisualItem.FILLCOLOR, palette);
    
	//create action list containing color assignments... these are used for actions that will be executed at the same time
	ActionList color = new ActionList();
	color.add(fill);
	color.add(edges);
	color.add(text);
	color.add(aStroke);
	color.add(aFill);
	
	ActionList layout = new ActionList();
	//ActionList layout = new ActionList(Activity.INFINITY);
	layout.add(color);
	layout.add(new RandomLayout(GRAPH));
	//layout.add(new ForceDirectedLayout("graph", true));
	layout.add(new AggregateLayout(AGGR, vis));
	layout.add(new RepaintAction());
	//vis.putAction("color",color);
	vis.putAction("layout",layout);
	//vis.run("layout");
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
	
	d.addControlListener(new AggregateDragControl());
	
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

class AggregateLayout extends Layout {
    
    private int m_margin = 5; // convex hull pixel margin
    private double[] m_pts;   // buffer for computing convex hulls
    
    public AggregateLayout(String aggrGroup, Visualization vis) {
        super(aggrGroup);
        m_vis = vis;
       // System.out.println("test1");
    }
    
    /**
     * @see edu.berkeley.guir.prefuse.action.Action#run(edu.berkeley.guir.prefuse.ItemRegistry, double)
     */
    public void run(double frac) {
        
        AggregateTable aggr = (AggregateTable)m_vis.getGroup(m_group);
        // do we have any  to process?
        int num = aggr.getTupleCount();
        if ( num == 0 ) return;
        
       // System.out.println("test12");
        
        // update buffers
        int maxsz = 0;
        for ( Iterator aggrs = aggr.tuples(); aggrs.hasNext();  )
            maxsz = Math.max(maxsz, 4*2*
                    ((AggregateItem)aggrs.next()).getAggregateSize());
        if ( m_pts == null || maxsz > m_pts.length ) {
            m_pts = new double[maxsz];
        }
        
        // compute and assign convex hull for each aggregate
        Iterator aggrs = m_vis.visibleItems(m_group);
        while ( aggrs.hasNext() ) {
        	//System.out.println("1");
            AggregateItem aitem = (AggregateItem)aggrs.next();

            int idx = 0;
            if ( aitem.getAggregateSize() == 0 ) continue;
            VisualItem item = null;
            Iterator iter = aitem.items();
            while ( iter.hasNext() ) {
                item = (VisualItem)iter.next();
                if ( item.isVisible() ) {
                    addPoint(m_pts, idx, item, m_margin);
                    idx += 2*4;
                }
            }
            // if no aggregates are visible, do nothing
            if ( idx == 0 ) {
            	//System.out.println("test123");
            	continue;
            }

            // compute convex hull
            double[] nhull = GraphicsLib.convexHull(m_pts, idx);
            
            // prepare viz attribute array
            float[]  fhull = (float[])aitem.get(VisualItem.POLYGON);
            if ( fhull == null || fhull.length < nhull.length )
                fhull = new float[nhull.length];
            else if ( fhull.length > nhull.length )
                fhull[nhull.length] = Float.NaN;
            
            // copy hull values
            for ( int j=0; j<nhull.length; j++ )
                fhull[j] = (float)nhull[j];
            aitem.set(VisualItem.POLYGON, fhull);
            aitem.setValidated(false); // force invalidation
        }
    }
    
    private static void addPoint(double[] pts, int idx, 
                                 VisualItem item, int growth)
    {
    	//System.out.println("test1234");
    	
        Rectangle2D b = item.getBounds();
        double minX = (b.getMinX())-growth, minY = (b.getMinY())-growth;
        double maxX = (b.getMaxX())+growth, maxY = (b.getMaxY())+growth;
        pts[idx]   = minX; pts[idx+1] = minY;
        pts[idx+2] = minX; pts[idx+3] = maxY;
        pts[idx+4] = maxX; pts[idx+5] = minY;
        pts[idx+6] = maxX; pts[idx+7] = maxY;
        
       // System.out.println("test1234");
    }
    
} // end of class AggregateLayout


/**
 * Interactive drag control that is "aggregate-aware"
 */
class AggregateDragControl extends ControlAdapter {

    private VisualItem activeItem;
    protected Point2D down = new Point2D.Double();
    protected Point2D temp = new Point2D.Double();
    protected boolean dragged;
    
    /**
     * Creates a new drag control that issues repaint requests as an item
     * is dragged.
     */
    public AggregateDragControl() {
    }
        
    /**
     * @see prefuse.controls.Control#itemEntered(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
     */
    public void itemEntered(VisualItem item, MouseEvent e) {
        Display d = (Display)e.getSource();
        d.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        activeItem = item;
        if ( !(item instanceof AggregateItem) )
            setFixed(item, true);
    }
    
    /**
     * @see prefuse.controls.Control#itemExited(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
     */
    public void itemExited(VisualItem item, MouseEvent e) {
        if ( activeItem == item ) {
            activeItem = null;
            setFixed(item, false);
        }
        Display d = (Display)e.getSource();
        d.setCursor(Cursor.getDefaultCursor());
    }
    
    /**
     * @see prefuse.controls.Control#itemPressed(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
     */
    public void itemPressed(VisualItem item, MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e)) return;
        dragged = false;
        Display d = (Display)e.getComponent();
        d.getAbsoluteCoordinate(e.getPoint(), down);
        if ( item instanceof AggregateItem )
            setFixed(item, true);
    }
    
    /**
     * @see prefuse.controls.Control#itemReleased(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
     */
    public void itemReleased(VisualItem item, MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e)) return;
        if ( dragged ) {
            activeItem = null;
            setFixed(item, false);
            dragged = false;
        }            
    }
    
    /**
     * @see prefuse.controls.Control#itemDragged(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
     */
    public void itemDragged(VisualItem item, MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e)) return;
        dragged = true;
        Display d = (Display)e.getComponent();
        d.getAbsoluteCoordinate(e.getPoint(), temp);
        double dx = temp.getX()-down.getX();
        double dy = temp.getY()-down.getY();
        
        move(item, dx, dy);
        
        down.setLocation(temp);
    }

    protected static void setFixed(VisualItem item, boolean fixed) {
        if ( item instanceof AggregateItem ) {
            Iterator items = ((AggregateItem)item).items();
            while ( items.hasNext() ) {
                setFixed((VisualItem)items.next(), fixed);
            }
        } else {
            item.setFixed(fixed);
        }
    }
    
    protected static void move(VisualItem item, double dx, double dy) {
        if ( item instanceof AggregateItem ) {
            Iterator items = ((AggregateItem)item).items();
            while ( items.hasNext() ) {
                move((VisualItem)items.next(), dx, dy);
            }
        } else {
            double x = item.getX();
            double y = item.getY();
            item.setStartX(x);  item.setStartY(y);
            item.setX(x+dx);    item.setY(y+dy);
            item.setEndX(x+dx); item.setEndY(y+dy);
        }
    }
    
} // end of class AggregateDragControl

/*
class gidComparator implements Comparator<getGid> {
	@Override
	public int compare(getGid a, getGid b) {
		return a.gid < b.gid ? -1 : a.gid == b.gid ? 0 : 1;
	}
}

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