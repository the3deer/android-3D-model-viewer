package org.andresoviedo.util.xml;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a node in an XML file. This contains the name of the node, a map
 * of the attributes and their values, any text data between the start and end
 * tag, and a list of all its children nodes.
 * 
 * @author Karl
 */
public final class XmlNode {

	private String name;
	private Map<String, String> attributes;
	private String data;
	private Map<String, List<XmlNode>> childNodes;

    XmlNode(String name) {
		this.name = name;
	}

	/**
	 * @return The name of the XML node.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Any text data contained between the start and end tag of the
	 *         node.
	 */
	public String getData() {
		return data;
	}

	/**
	 * Gets the value of a certain attribute of the node. Returns {@code null}
	 * if the attribute doesn't exist.
	 * 
     * @param attr - the name of the attribute.
	 * @return The value of the attribute.
	 */
	public String getAttribute(String attr) {
		if (attributes != null) {
			return attributes.get(attr);
		} else {
			return null;
		}
	}

	/**
	 * Gets a certain child node of this node.
	 * 
     * @param childName - the name of the child node.
	 * @return The child XML node with the given name.
	 */
	public XmlNode getChild(String childName) {
		if (childNodes != null) {
			List<XmlNode> nodes = childNodes.get(childName);
			if (nodes != null && !nodes.isEmpty()) {
				return nodes.get(0);
			}
		}
		return null;

	}

	/**
	 * Gets a child node with a certain name, and with a given value of a given
	 * attribute. Used to get a specific child when there are multiple child
	 * nodes with the same node name.
	 * 
     * @param childName - the name of the child node.
     * @param attr      - the attribute whose value is to be checked.
     * @param value     - the value that the attribute must have.
	 * @return The child node which has the correct name and the correct value
	 *         for the chosen attribute.
	 */
	public XmlNode getChildWithAttribute(String childName, String attr, String value) {
		List<XmlNode> children = getChildren(childName);
		if (children == null || children.isEmpty()) {
			return null;
		}
		for (XmlNode child : children) {
			String val = child.getAttribute(attr);
			if (value.equals(val)) {
				return child;
			}
		}
		return null;
	}

	/**
	 * Get the child nodes of this node that have a given name.
	 * 
     * @param name - the name of the child nodes.
	 * @return A list of the child nodes with the given name. If none exist then
	 *         an empty list is returned.
	 */
	@NonNull
	public List<XmlNode> getChildren(String name) {
		if (childNodes != null) {
			List<XmlNode> children = childNodes.get(name);
			if (children != null) {
				return children;
			}
		}
        return Collections.emptyList();
	}

	/**
	 * Adds a new attribute to this node. An attribute has a name and a value.
	 * Attributes are stored in a HashMap which is initialized in here if it was
	 * previously null.
	 * 
     * @param attr  - the name of the attribute.
     * @param value - the value of the attribute.
	 */
	protected void addAttribute(String attr, String value) {
		if (attributes == null) {
			attributes = new HashMap<>();
		}
		attributes.put(attr, value);
	}

	/**
	 * Adds a child node to this node.
	 * 
     * @param child - the child node to add.
	 */
	protected void addChild(XmlNode child) {
		if (childNodes == null) {
            childNodes = new HashMap<>();
		}
		List<XmlNode> list = childNodes.get(child.name);
		if (list == null) {
            list = new ArrayList<>();
			childNodes.put(child.name, list);
		}
		list.add(child);
	}

	/**
	 * Sets some data for this node.
	 * 
     * @param data - the data for this node (text that is found between the start
	 *            and end tags of this node).
	 */
	protected void setData(String data) {
		this.data = data;
	}

}
