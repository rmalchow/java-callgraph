package gr.gousiosg.javacg.output;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import gr.gousiosg.javacg.info.ClassInfo;
import gr.gousiosg.javacg.info.MethodCall;
import gr.gousiosg.javacg.info.MethodInfo;

public class GraphmlRenderer implements Renderer {

	private Node root;
	private Node rootGraph;
	private Map<String,Node> nodes = new HashMap<String, Node>();
	private Map<String,Node> edges = new HashMap<String, Node>();
	
	
	
	private Node appendElementNode(Node parent, String tag, String... attributes) {
		Node n = parent.getOwnerDocument().createElement(tag);
		parent.appendChild(n);
		
		for(int i=0; i<attributes.length;i=i+2) {
			Node a = n.getOwnerDocument().createAttribute(attributes[i]);
			a.setNodeValue(attributes[i+1]);
			n.getAttributes().setNamedItem(a);
		}
		return n;
	}

	private void appendLabel(Node parent, String label, String color) {
		Node d = parent.getOwnerDocument().createElement("data");
		parent.appendChild(d);
		
		Node dk = parent.getOwnerDocument().createAttribute("key");
		dk.setNodeValue("d1");
		d.getAttributes().setNamedItem(dk);
		
		Node ysn = parent.getOwnerDocument().createElementNS("http://www.yworks.com/xml/graphml", "y:ShapeNode");
		d.appendChild(ysn);

		//<y:Fill color="#FFCC00" transparent="false"/>
		
		Node yf = parent.getOwnerDocument().createElementNS("http://www.yworks.com/xml/graphml", "y:Fill");
		ysn.appendChild(yf);
		
		Node yfc = parent.getOwnerDocument().createAttribute("color");
		yfc.setNodeValue(color);
		yf.getAttributes().setNamedItem(yfc);
		
		Node yft = parent.getOwnerDocument().createAttribute("transparent");
		yft.setNodeValue("false");
		yf.getAttributes().setNamedItem(yft);
		
		
		Node yl = parent.getOwnerDocument().createElementNS("http://www.yworks.com/xml/graphml", "y:NodeLabel");
		ysn.appendChild(yl);
		
		Node yt = parent.getOwnerDocument().createTextNode(label);
		yl.appendChild(yt);
	}
	
	private void appendEdgeStyle(Node parent, String color) {
		/**
	    <y:PolyLineEdge>
	    <y:Path sx="0.0" sy="0.0" tx="0.0" ty="0.0"/>
	    <y:LineStyle color="#00000033" type="line" width="1.0"/>
	    <y:Arrows source="none" target="none"/>
	    <y:BendStyle smoothed="false"/>
	  	</y:PolyLineEdge>
		**/

		Node d = parent.getOwnerDocument().createElement("data");
		parent.appendChild(d);
		
		Node dk = parent.getOwnerDocument().createAttribute("key");
		dk.setNodeValue("d10");
		d.getAttributes().setNamedItem(dk);
		
		Node ple = parent.getOwnerDocument().createElementNS("http://www.yworks.com/xml/graphml", "y:PolyLineEdge");
		d.appendChild(ple);

		//<y:Fill color="#FFCC00" transparent="false"/>
		
		Node yl = parent.getOwnerDocument().createElementNS("http://www.yworks.com/xml/graphml", "y:LineStyle");
		ple.appendChild(yl);
		
		{
			Node ylc = parent.getOwnerDocument().createAttribute("color");
			ylc.setNodeValue(color);
			yl.getAttributes().setNamedItem(ylc);
		}
		{
			Node ylw = parent.getOwnerDocument().createAttribute("width");
			ylw.setNodeValue("3.0");
			yl.getAttributes().setNamedItem(ylw);
		}
		{
			Node ylt = parent.getOwnerDocument().createAttribute("type");
			ylt.setNodeValue("line");
			yl.getAttributes().setNamedItem(ylt);
		}
		
		Node ya = parent.getOwnerDocument().createElementNS("http://www.yworks.com/xml/graphml", "y:Arrows");
		ple.appendChild(ya);
		
		{
			Node yas = parent.getOwnerDocument().createAttribute("source");
			yas.setNodeValue("delta");
			ya.getAttributes().setNamedItem(yas);
		}
		{
			Node yat = parent.getOwnerDocument().createAttribute("target");
			yat.setNodeValue("delta");
			ya.getAttributes().setNamedItem(yat);
		}
		
		
	}

	public void createClasses(ClassInfo ci) {
		Node previous = rootGraph;  
		
		List<String> full = Arrays.asList(ci.getClassname().split("\\."));
		for(int i=3;i<=full.size();i++) {
			List<String> sub = full.subList(0, i);
			String subPath = StringUtils.join(sub,"."); 
			Node n = nodes.get(subPath);
			if(n==null) {
				n = appendElementNode(previous, "node", "id", subPath);
				nodes.put(subPath, n);
				
				String color = "#CCCCCC11";
				/**
				if(subPath.endsWith(".api")) {
					color = "#A9BCF5";
				} else if(subPath.endsWith(".impl")) {
					color = "#A9F5A9";
				} else 
				**/
				if (subPath.equals(ci.getClassname())) {
					color = "#FE9A2E";
				}
				appendLabel(n, subPath, color);
			}
			previous = n;
			
			Node g = nodes.get(subPath+"_graph");
			if(g==null) {
				g = appendElementNode(previous, "graph", "id", subPath+"_graph", "edgedefault", "directed" );
				nodes.put(subPath+"_graph", g);
			}
			previous = g;
			 
			
		}
		/**
		for(MethodInfo mi : ci.getMethodInfos()) {
			String nodeId = ci.getClassname()+"-"+mi.getName()+"("+mi.getSignature()+")";
			Node nm = appendElementNode(previous, "node", "id", nodeId);
			nodes.put(nodeId, nm);
			appendLabel(nm, mi.getName()+"("+mi.getSignature()+")");
		}
		**/
	}

	public void createEdges(ClassInfo ci) {
		
		for(String s : ci.getSuperclasses()) {
			Node source = nodes.get(s);
			Node target = nodes.get(ci.getClassname());
			
			if(source==target) continue;
			
			if(source==null) continue;
			if(target==null) continue;

			String key = s+"---"+ci.getClassname();
			/**
			Node n = edges.get(key);
			if(n == null) {
				n = appendElementNode(rootGraph, "edge", "source", source.getAttributes().getNamedItem("id").getNodeValue(), "target", target.getAttributes().getNamedItem("id").getNodeValue());
				edges.put(key, n);
				appendEdgeStyle(n, "#99999911");
			}
			**/
			
		}
		
		
		for(MethodInfo mi : ci.getMethodInfos()) {
			for(MethodCall mc : mi.getMethodcalls()) {
				
				Node source = nodes.get(mc.getCaller());
				Node target = nodes.get(mc.getCallee());
				
				if(source==target) continue;
				
				if(source==null) continue;
				if(target==null) continue;
				
				String key = mc.getCaller()+"---"+mc.getCallee();
				Node n = edges.get(key);
				if(n == null) {
					n = appendElementNode(rootGraph, "edge", "source", source.getAttributes().getNamedItem("id").getNodeValue(), "target", target.getAttributes().getNamedItem("id").getNodeValue());
					edges.put(key, n);
					appendEdgeStyle(n, "BBBBBB11");
				}
			}
		}
	}
	

	@Override
	public void write(List<ClassInfo> cis, OutputStream os) {
		try {
			
			Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			
			root = d.createElement("graphml");
			d.appendChild(root);
			
			// <key for="node" id="d1" yfiles.type="nodegraphics"/>
			appendElementNode(root, "key", "for","node","id","d1","yfiles.type","nodegraphics");

			//   <key for="edge" id="d10" yfiles.type="edgegraphics"/>
			appendElementNode(root, "key", "for","node","id","d10","yfiles.type","edgegraphics");

			
			rootGraph = appendElementNode(root,"graph","id","top_node","edgedefault","directed");
			
			for(ClassInfo ci : cis) {
				createClasses(ci);
			}
			for(ClassInfo ci : cis) {
				createEdges(ci);
			}
			
			
	        Source xmlInput = new DOMSource(d);

	        StreamResult xmlOutput = new StreamResult(os);
	        TransformerFactory transformerFactory = SAXTransformerFactory.newInstance();
	        Transformer transformer = transformerFactory.newTransformer(); 
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
	        transformer.transform(xmlInput, xmlOutput);
	        os.flush();
 			
		} catch (Exception e) {
			throw new RuntimeException("error rendering to graphml",e);
		}
	}
	
}
