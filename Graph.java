package graphs;

import java.util.*;

/*
 * Implements a graph. We use two maps: one map for adjacency properties 
 * (adjancencyMap) and one map (dataMap) to keep track of the data associated 
 * with a vertex. 
 */

public class Graph<E> {
	private HashMap<String, HashMap<String, Integer>> adjacencyMap;
	private HashMap<String, E> dataMap;


	public Graph() {
		adjacencyMap = new HashMap<String, HashMap<String, Integer>>();
		dataMap = new HashMap<String, E>();
	}

	/* adds a vertex to the graph with the specified data */
	public void addVertex(String vertexName, E data) {
		/* if the data already exists within the graph, a new vertex is not created */
		if (dataMap.containsKey(vertexName)) {
			throw new IllegalArgumentException("Error: Vertex already exists.");

		} else {
			dataMap.put(vertexName, data);
			adjacencyMap.put(vertexName, new HashMap<String, Integer>());
		}
	}
	/* sets the cost it takes to travel from the startVertex to the edgeVertex */
	public void addDirectedEdge(String startVertexName, String edgeVertexName, 
			int cost) {
		if (adjacencyMap.containsKey(startVertexName)
				&& adjacencyMap.containsKey(edgeVertexName)) {
			adjacencyMap.get(startVertexName).put(edgeVertexName, cost);

		} else {
			throw new IllegalArgumentException
			("Error: One or more vertices does not exist.");
		}

	}

	/* prints an array containing all of the vertices, followed by what vertices 
	 * are connected to one another and the cost to use the edge
	 */
	public String toString() {
		Set<String> keySet = dataMap.keySet();
		String[] keys = keySet.toArray(new String[keySet.size()]);

		/* puts the array of keys in alphabetical order */
		for (int i = 1; i < keys.length; i++) {
			int j;
			String temp = keys[i];
			for (j = i - 1; j >= 0 && temp.compareTo(keys[j]) < 0; j--) {
				keys[j + 1] = keys[j];
			}

			keys[j + 1] = temp;
		}

		StringBuffer answer = new StringBuffer("");
		for(int i = 0; i < keys.length; i++) {
			answer.append("Vertex(" 
					+ keys[i] + ")--->" 
					+ adjacencyMap.get(keys[i]).toString()
					+ "\n");
		}

		return "Vertices: " + Arrays.toString(keys) + "\nEdges:\n" + answer;

	}

	public Map<String, Integer> getAdjacentVertices(String vertexName) {
		return adjacencyMap.get(vertexName);
	}

	public int getCost(String vertexName, String endVertexName) {
		if(adjacencyMap.containsKey(vertexName) 
				|| adjacencyMap.get(vertexName).containsKey(endVertexName)) {
			return adjacencyMap.get(vertexName).get(endVertexName);

		} else {
			throw new IllegalArgumentException
			("Error: One or more vertex does not exist.");
		}
	}

	public Set<String> getVertices() {
		return dataMap.keySet();
	}

	public E getData(String vertex) {
		return dataMap.get(vertex);
	}

	/* performs a depth first search */
	public void doDepthFirstSearch(String startVertexName,
			CallBack<E> callback) {
		if(!dataMap.containsKey(startVertexName)) {
			throw new IllegalArgumentException("Error: Vertex does not exist.");
		}

		Set<String> visited = new HashSet<String>();
		Stack<String> discovered = new Stack<String>();


		discovered.add(startVertexName);
		while (!discovered.isEmpty()) {

			String curr = discovered.pop();

			if (!visited.contains(curr)) {
				visited.add(curr);
				callback.processVertex(curr, dataMap.get(curr));
			}

			for (String vertex: adjacencyMap.get(curr).keySet()) {
				if (!visited.contains(vertex)) {
					discovered.add(vertex);
				}
			}
		}
	}

	/* performs a breadth first search */
	public void doBreadthFirstSearch(String startVertexName,
			CallBack<E> callback) {
		if(!dataMap.containsKey(startVertexName)) {
			throw new IllegalArgumentException("Error: Vertex does not exist.");
		}

		Set<String> visited = new HashSet<String>();
		Queue<String> discovered = new LinkedList<String>();

		discovered.add(startVertexName);
		while (!discovered.isEmpty()) {
			String curr = discovered.poll();
			if (!visited.contains(curr)) {
				visited.add(curr);
				callback.processVertex(curr, dataMap.get(curr));
			}

			for (String vertex: adjacencyMap.get(curr).keySet()) {
				if (!visited.contains(vertex)) {
					discovered.add(vertex);
				}
			}
		}
	}

	/* performs Dijkstras algorithm(finds the path of least cost from specified
	 * starting vertex to specified end vertex
	 */
	public int doDijkstras(String startVertexName, String endVertexName,
			ArrayList<String> shortestPath) {

		if(!dataMap.containsKey(startVertexName) 
				|| !dataMap.containsKey(endVertexName)) {
			throw new  IllegalArgumentException
			("Error: One or more vertex does not exist.");
		}

		Set<String> vertices = new HashSet<String>();
		Map<String, Integer> cost = new HashMap<String, Integer>();
		Map<String, String> predecessor = new HashMap<String, String>();

		for(String vertex: adjacencyMap.keySet()) {
			cost.put(vertex, 999999999);
			predecessor.put(vertex, "");
		}

		cost.put(startVertexName, 0);
		Set<String> costKeys = new HashSet<String>();
		for(String dupicateKey: cost.keySet()) {
			costKeys.add(dupicateKey);
		}

		while (vertices.size() != dataMap.size()) {
			int min = 0;
			int i = 0;
			String addVertex = "";

			for(String key: costKeys) {

				if (i == 0) {
					min = cost.get(key);
					addVertex = key;
				}

				if (min > cost.get(key)) {
					min = cost.get(key);
					addVertex = key;
				}

				i++;
			}

			vertices.add(addVertex);
			for(String adjacentVert: adjacencyMap.get(addVertex).keySet()) {
				if (!vertices.contains(adjacentVert)) {

					if(cost.get(addVertex) 
							+ adjacencyMap.get(addVertex).get(adjacentVert) 
							< cost.get(adjacentVert)) {

						cost.put(adjacentVert, cost.get(addVertex) 
								+ adjacencyMap.get(addVertex).get(adjacentVert));

						predecessor.put(adjacentVert, addVertex);
					}
				}
			}

			costKeys.remove(addVertex);

		}

		if(cost.get(endVertexName) == 999999999) {
			cost.put(endVertexName, -1);
		}

		int index = 0;
		String currVertex = endVertexName;
		while(!predecessor.get(currVertex).equals(startVertexName)) {

			if(predecessor.get(endVertexName).equals("") && startVertexName.equals(endVertexName)) {
				shortestPath.add(startVertexName);
				break;

			} else if (predecessor.get(endVertexName).equals("")) {
				shortestPath.add("None");
				break;
			} 

			if (index == 0) {
				shortestPath.add(startVertexName);
			}

			shortestPath.add(1 , predecessor.get(currVertex));
			currVertex = predecessor.get(currVertex);

			index++;
		}

		if (predecessor.get(endVertexName).equals(startVertexName)) {
			shortestPath.add(startVertexName);
			shortestPath.add(endVertexName);
		}

		if (index > 0) {
			shortestPath.add(endVertexName);
		}

		return cost.get(endVertexName);
	}

}