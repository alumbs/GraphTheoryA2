//Graph Theory A2
import java.io.*;
import java.util.*;

public class A2
{
	static final int EXIT_FAILURE = -1;
		
	public static void main(String [] args)
	{
		int maxLength = 0;
		String[] files = {"Thomassen.txt", "NonHam24.txt","Mid7.txt", "Mid9.txt"};
		
		for(String filename : files)
		{
			Graph G = readInGraph(filename);
			maxLength = 0;
			
			for(int j = 0; j < 10; j++)
			{
				int tempMax = performCrossOverAlg(G);
				if(tempMax > maxLength){
					maxLength = tempMax;
					print("This is Max= " + maxLength+"\n");
				}
			}
			
			System.out.println("Graph Name: " + G.graphName);
			System.out.println("Max Graph Path: " + maxLength);
		}
	}
	
	//Returns the longest path found
	private static int performCrossOverAlg(Graph G)
	{
		A2 a2 = new A2();
		MyIntegerHashMap path = a2.new MyIntegerHashMap();
		int pathIndex = G.numVertices;
		int uIndex = pathIndex, vIndex = pathIndex;
		boolean crossoverFound = true;
		
		 //u and v vertices adjacent to startVertex
		Vertex adjacentU = null, adjacentV = null, startVertex = null;
		Vertex tempVertex, uVertex, vVertex;
		
		if(G == null)
			return 0;
			
		//System.out.println("Crossover alg started");
		
		//Pick a random starting vertex
		int randVertex = (int)(Math.random() * G.getNumVertices()) + 1;
		//int randVertex = 1;
		System.out.println("Starting vertex = " + randVertex);
		
		startVertex = G.vertices.get(randVertex);
		
		if(startVertex == null){
			System.out.println("ERROR: Vertex " + randVertex + " not found");
			return 0;
		}
		
		//Add the startVertex to the path
		//Add it to position pathIndex incase it was the last vertex
		// in the list
		path.put(pathIndex, startVertex.vertexIndex);
		
		//Now find two adjacent vertices to the startVertex
		//and add them to the path if they exist
		if(startVertex.adjacentVertices.size() > 0){
			adjacentU = startVertex.adjacentVertices.get(0);
			
			//Decerement the uIndex for the next vertex to be 
			//added to the left of vertex U
			uIndex--;
			
			//Vertex U should be to the left 
			path.put(uIndex, adjacentU.vertexIndex);
		}
		
		if(startVertex.adjacentVertices.size() > 1){
			adjacentV = startVertex.adjacentVertices.get(1);
			
			//Increment the vIndex for the next vertex to be 
			//added to the right of vertex V
			vIndex++;
			
			//Vertex V should be to the right 
			path.put(vIndex, adjacentV.vertexIndex);
		}
		
		//Extend both adjacent vertices 
		//until we can't extend them any further
		int count = 0;
		while(adjacentU != null &&
				adjacentU.adjacentVertices.size() > 0
				&& count < adjacentU.adjacentVertices.size())
		{
			tempVertex = adjacentU.adjacentVertices.get(count);
			
			if(!path.containsValue(tempVertex.vertexIndex))
			{
				//print("Vertex="+tempVertex.vertexIndex + " count=" + count);
				uIndex--;
				path.put(uIndex, tempVertex.vertexIndex);
				adjacentU = tempVertex;
				count = 0;
			}
			else
			{
				count++;
			}
		}
		
		count = 0;
		while(adjacentV != null &&
				adjacentV.adjacentVertices.size() > 0 &&
				count < adjacentV.adjacentVertices.size())
		{
			tempVertex = adjacentV.adjacentVertices.get(count);
			
			if(!path.containsValue(tempVertex.vertexIndex))
			{
				//print("Vertex V added to path= "+ tempVertex.vertexIndex +
					//" At index: " + (vIndex+1) + " for vertex " + adjacentV.vertexIndex);
				vIndex++;
				path.put(vIndex, tempVertex.vertexIndex);
				adjacentV = tempVertex;
				count = 0;
			}
			else
			{
				count++;
			}
		}
		
		//Print out u and v
		//System.out.println("adjacentU = " + adjacentU.vertexIndex);
		//System.out.println("adjacentV = " + adjacentV.vertexIndex);
		//System.out.println("Total num vertices in path = " + path.size());
		//System.out.println("Path contains " + path.values());
		
		//Main loop
		while(crossoverFound)
		{
			int vertexXIndex=0, vertexWIndex=0, vertexYIndex=0;
			boolean pathExtended = false;
			
			//adjacentU
			for(Vertex tempW : adjacentU.adjacentVertices.items)
			{
				//print("For vertices adjacent to " + adjacentU.vertexIndex +
					//" AdjVertex = " + tempW.vertexIndex);
					
				//Get the prev vertex to w on the path
				Vertex tempX = null;
				for(Integer key : path.keySet())
				{
					if(path.get(key) == tempW.vertexIndex){
						vertexWIndex = key;
						vertexXIndex = key-1;
						tempX = G.vertices.get(path.get(vertexXIndex));
						break; //stop this inner for-loop
					}
				}
				
				//print("Found prev vertex " + tempX.vertexIndex);
				
				//check all vertices adjacent to X not in the path
				if(tempX != null)
				for(Vertex tempZ : tempX.adjacentVertices.items)
				{
					//print("Adj vert = " +tempZ.vertexIndex+" for vert "+tempX.vertexIndex);
					if(!path.containsValue(tempZ.vertexIndex))
					{
						//System.out.println("Path Extension found: Vertex"+
							//"= " + vertexXIndex +   + adjacentU.vertexIndex);
						//extend the path
						MyIntegerHashMap newPath = 
							a2.new MyIntegerHashMap();
						int newUIndex = uIndex;
						
						//Add Z-X-U-W-V to the new path
						newPath.put(newUIndex, tempZ.vertexIndex);
						newUIndex++;
						newPath.put(newUIndex, tempX.vertexIndex);
						newUIndex++;
						
						//Add all vertices from vertex X to vertex U
						vertexXIndex--;						
						while(vertexXIndex >= uIndex)
						{
							newPath.put(newUIndex, path.get(vertexXIndex));
							newUIndex++;
							vertexXIndex--;
						}
						
						//Add all vertices from vertex W to vertex V
						while(vertexWIndex <= vIndex)
						{
							newPath.put(newUIndex, path.get(vertexWIndex));
							newUIndex++;
							vertexWIndex++;
						}
						
						//vIndex has changed due to the extension
						vIndex = newUIndex - 1;
						
						pathExtended = true;
						path = newPath;
						break;
					}
				}
				
				if(pathExtended)
					break;
			}
			
			//if the path was extended, break out and start again
			if(pathExtended)
				continue;
				
			//print("Path not extended, try AdjV");
			
			//adjacentV
			for(Vertex tempW : adjacentV.adjacentVertices.items)
			{
				//print("For vertices adjacent to " + adjacentV.vertexIndex +
					//" AdjVertex = " + tempW.vertexIndex);
					
				//Get the next vertex to w on the path
				Vertex tempY = null;
				for(Integer key : path.keySet())
				{
					if(path.get(key) == tempW.vertexIndex){
						vertexWIndex = key;
						vertexYIndex = key+1;
						tempY = G.vertices.get(path.get(vertexYIndex));
						break; //stop this inner for-loop
					}
				}
				
				//print("Found next vertex " + tempY.vertexIndex);
				
				//check all vertices adjacent to Y not in the path
				if(tempY != null)
				for(Vertex tempZ : tempY.adjacentVertices.items)
				{
					//print("Adj vert = " +tempZ.vertexIndex+" for vert "+tempY.vertexIndex);
					if(!path.containsValue(tempZ.vertexIndex))
					{
						//print("Path extension found");
						//print("Vertex added is: " + tempZ.vertexIndex);
						
						//extend the path
						MyIntegerHashMap newPath = 
							a2.new MyIntegerHashMap();
						int newUIndex = uIndex;
						
						//Add Z-Y-V-W-U to the new path
						newPath.put(newUIndex, tempZ.vertexIndex);
						newUIndex++;
						newPath.put(newUIndex, tempY.vertexIndex);
						newUIndex++;
						
						//Add all vertices from vertex Y to vertex V
						vertexYIndex++;						
						while(vertexYIndex <= vIndex)
						{
							newPath.put(newUIndex, path.get(vertexYIndex));
							newUIndex++;
							vertexYIndex++;
						}
						
						//Add all vertices from vertex W to vertex U
						while(vertexWIndex >= uIndex)
						{
							newPath.put(newUIndex, path.get(vertexWIndex));
							newUIndex++;
							vertexWIndex--;
						}
						
						//vIndex has changed due to the extension
						vIndex = newUIndex - 1;
						
						pathExtended = true;
						path = newPath;
						break;
					}
				}
				
				//if the path was extended, break out and start again
				if(pathExtended)
					break;
			}
			
			//if the path was extended, break out and start again
			if(pathExtended)
				continue;
				
			//print("Path not extended, try looking for crossover");
			
			//Else look for a crossover of order 1
			//if found, extend P
			uVertex = G.vertices.get(path.get(uIndex));
			for(Vertex yVertex : uVertex.adjacentVertices.items)
			{
				//print("For vertices adjacent to " + uVertex.vertexIndex +
					//" AdjVertex = " + yVertex.vertexIndex);
					
				//Get the prev vertex to adjVertices on the path
				Vertex prevX = null;
				int yIndex = 0, prevXIndex =0;
				for(Integer key : path.keySet())
				{
					if(path.get(key) == yVertex.vertexIndex){
						yIndex = key;
						prevXIndex = key-1;
						prevX = G.vertices.get(path.get(prevXIndex));
						break; //stop this inner for-loop
					}
				}
				
				//Check if the prev vertex is adjacent to vertex V
				int tempVVertex = path.get(vIndex);
				
				//If yes then we have a crossover
				if(prevX != null && prevX.containsAdjacentVertex(tempVVertex))
				{
					//print("One vertex adj to U is also adj to V= "+prevX.vertexIndex);
					//Go through all vertices on the path
					//and see if it is adjacent to any vertices 
					//not on the path - if yes, extend the Path
					for(int pIndex = uIndex; pIndex <= vIndex; pIndex++)
					{
						Vertex pVertex = G.vertices.get(path.get(pIndex));
						
						for(Vertex tempZ : pVertex.adjacentVertices.items)
						{
							//If no, then that means the current vertex
							//is adjacent to something not on the path
							//So extend the path
							if(!path.containsValue(tempZ.vertexIndex))
							{
								//extend the path - BUT we have 2 cases
								//1: pt p is to the left of crossover
								//2: pt p is to the right of crossover
								if(pIndex <= prevXIndex)
								{
									//Do Z-P-X-V-Y-U-(P-1)
									MyIntegerHashMap newPath = 
										a2.new MyIntegerHashMap();
									int newUIndex = uIndex;
									
									//Add Z to the new path
									newPath.put(newUIndex, tempZ.vertexIndex);
									newUIndex++;
									
									//Add all vertices from vertex P
									// to vertex X
									int tempPIndex = pIndex;						
									while(tempPIndex <= prevXIndex)
									{
										newPath.put(newUIndex, path.get(tempPIndex));
										newUIndex++;
										tempPIndex++;
									}
									
									//Add all vertices from vertex V 
									//to vertex Y
									int tempVIndex = vIndex;
									while(tempVIndex >= yIndex)
									{
										newPath.put(newUIndex, path.get(tempVIndex));
										newUIndex++;
										tempVIndex--;
									}
									
									//Add U - (P-1) vertices
									int tempUIndex = uIndex;
									while(tempUIndex <= pIndex-1)
									{
										newPath.put(newUIndex, path.get(tempUIndex));
										newUIndex++;
										tempUIndex++;
									}
									
									//vIndex has changed due to the extension
									vIndex = newUIndex - 1;
									
									pathExtended = true;
									path = newPath;
									break;
								}
								else //pIndex is > prevXIndex
								{
									//Do Z-P-Y-U-X-V-(P+1)
									MyIntegerHashMap newPath = 
										a2.new MyIntegerHashMap();
									int newUIndex = uIndex;
									
									//Add Z and P to the new path
									newPath.put(newUIndex, tempZ.vertexIndex);
									newUIndex++;
																		
									//Add P to Y 
									int tempPIndex = pIndex;						
									while(tempPIndex >= yIndex)
									{
										newPath.put(newUIndex, path.get(tempPIndex));
										newUIndex++;
										tempPIndex--;
									}
									
									//Add U to X
									int tempUIndex = uIndex;
									while(tempUIndex <= prevXIndex)
									{
										newPath.put(newUIndex, path.get(tempUIndex));
										newUIndex++;
										tempUIndex++;
									}
									
									//Add V to (P+1)
									int tempVIndex = vIndex;
									while(tempVIndex >= pIndex + 1)
									{
										newPath.put(newUIndex, path.get(tempVIndex));
										newUIndex++;
										tempVIndex--;
									}
									
									//vIndex has changed due to the extension
									vIndex = newUIndex - 1;
									
									pathExtended = true;
									path = newPath;
									break;
								}
							}
							
							//if the path was extended, break out and start again
							if(pathExtended)
								break;
						}
						
						//if the path was extended, break out and start again
						if(pathExtended)
							break;
					}
				}
				
				//if the path was extended, break out and start again
				if(pathExtended)
					break;
			}
			
			//Finally if no extension was performed,
			//quit the loop
			if(!pathExtended)
				crossoverFound = false;
		}		
		
		printMap(path, uIndex, vIndex);
		return path.size();
	}
	
	public static void print(String s)
	{
		System.out.println(s);
	}
	
	public static void printMap(MyIntegerHashMap map, int start, int end)
	{		
		print("Printing Path");
		for(int i = start; i <= end; i++)
		{
			System.out.print(map.get(i) +" - ");
		}
		print("\n");
	}
	
	private static Graph readInGraph(String fileName)
	{
		int numVerticesOfGraph = 0;
		//Scanner sc;
		String line, graphName = null;
		String [] splitTemp;
		A2 a2 = new A2();
		A2.Graph G = null;
		BufferedReader br;
		
		if(fileName == null){
			System.exit(EXIT_FAILURE);
		}
					
		System.out.println("\nGraph Name = " + fileName);
					
		try{
			br = new BufferedReader(new FileReader(fileName));
			
			if((line = br.readLine()) != null){
				//First read the name of the Graph
				graphName = line;
			}
			
			if((line = br.readLine()) != null){
				//Next read the amount of vertices of the Graph
				numVerticesOfGraph = Integer.parseInt(line);
			}
			
			G = a2.new Graph(graphName, numVerticesOfGraph);
			
			while((line = br.readLine()) != null && !line.isEmpty()){
				//System.out.println("line = " + line);
				splitTemp = line.split(" ");
				String vertex = splitTemp[0].substring(1);//Remove the first char
				int vertexIndex = 0;
				Vertex mainVertex;
				
				if(!vertex.isEmpty()){
					//The vertex we are at
					vertexIndex = Integer.parseInt(vertex); 
				}
								
				//Check if the Vertex exists in the Graph already
				if(vertexIndex != 0 && 
					G.vertices.containsKey(vertexIndex))
				{
					mainVertex = G.vertices.get(vertexIndex);
				}
				else{
					mainVertex = a2.new Vertex(vertexIndex, splitTemp.length - 1);
				}
				
				//Add the vertex to the graph
				G.vertices.put(vertexIndex, mainVertex);
				
				//Read the list of adjacent vertices
				for(int i = 1; i < splitTemp.length; i++)
				{
					Vertex tempVertex = null;
					int tempIndex = 0;
					
					if(!splitTemp[i].isEmpty()){
						//System.out.println("SPlitVertex = " + splitTemp[i]);
						tempIndex = Integer.parseInt(splitTemp[i]);
					}
					
					//0 signifies the end of the list of vertices
					if(tempIndex != 0){
						//Check if the Vertex exists in the Graph already
						if(G.vertices.containsKey(tempIndex)){
							//System.out.println("G has vertex = " + tempIndex);
							tempVertex = G.vertices.get(tempIndex);
						}
						else{
							tempVertex = a2.new Vertex(tempIndex, 1);
							
							//Add the vertex to the graph
							G.vertices.put(tempIndex, tempVertex);
						}
						
						//Add this vertex as an adjacent vertex to the
						//main vertex and vice versa
						mainVertex.adjacentVertices.add(tempVertex);
						tempVertex.adjacentVertices.add(mainVertex);
					}
				}
				
				//System.out.println("line = " + line);
				//System.out.println("Splitline = " + vertex);
				//mainVertex.printAdjacentVertices();
			}			
		}
		catch(Exception e)
		{
			//Opening file failed or other stuff
			e.printStackTrace();
			System.out.println("Exception caught: " + e);
			System.exit(EXIT_FAILURE);
		}
		
		return G;
	}
	
	private class Graph
	{
		private String graphName;
		private int numVertices;
		public MyHashMap vertices;
		
		public Graph(String name, int numVert) throws Exception
		{
			if(name == null || numVert < 0)
			{
				System.out.println("Bad parameters passed in for Graph"
					+ " declaration");
				throw new Exception("Invalid parameters for Graph");
			}
			
			graphName = name;
			numVertices = numVert;
			
			//Create the array of vertices for this graph
			vertices = new MyHashMap();
		}	
		
		public int getNumVertices()
		{
			return this.numVertices;
		}	
		
		private class MyHashMap
		{
			public int[] keys;
			public Vertex[] values;
			private int count;
			
			public MyHashMap()
			{
				keys = new int[1];
				values = new Vertex[1];
				count = 0;
			}
			
			public boolean containsKey(int key)
			{
				boolean result = false;
				for(int k : keys)
				{
					if(k == key)
					{
						result = true;
						break;
					}
				}
				return result;
			}
			
			public void put(int key, Vertex value)
			{
				if(count == 0)
				{
					keys[count] = key;
					values[count] = value;
				}
				else if(count == keys.length)
				{
					int[] newKeys = new int[count+1];
					Vertex[] newValues = new Vertex[count+1];
					
					for(int i = 0; i < count; i++)
					{
						newKeys[i] = keys[i];
						newValues[i] = values[i];
					}
					newKeys[count] = key;
					newValues[count] = value;
					
					keys = newKeys;
					values = newValues;
				}
				else
				{
					print("MyHashMap Put Function Crashed -RECTIFY");
				}
				
				count++;
			}
			
			public Vertex get(int index)
			{
				Vertex result = null;
				for(int i = 0; i < keys.length; i++)
				{
					if(keys[i] == index){
						result = values[i];
						break;
					}
				}
				
				return result;
			}
		}
	}
	
	private class Vertex
	{
		public int vertexIndex;
		public MyArrayList adjacentVertices;
		
		public Vertex(int index, int numAdjacentVertices)
		{
			vertexIndex = index;
			adjacentVertices = new MyArrayList(numAdjacentVertices);
		}
		
		public boolean addAdjacentVertex(Vertex vertex)
		{
			return adjacentVertices.add(vertex);
		}
		
		public boolean containsAdjacentVertex(int wantedVertexIndex)
		{
			boolean hasVertex = false;
			if(adjacentVertices != null)
			{
				for(Vertex v : adjacentVertices.items)
				{
					if(v.vertexIndex == wantedVertexIndex)
					{
						hasVertex = true;
						break;
					}
				}
			}
			
			return hasVertex;
		}
		
		public void printAdjacentVertices()
		{
			System.out.print("Vertex Index = " + vertexIndex);
			System.out.print(" Adjacent vertices =");
			if(adjacentVertices != null){
				for(int i = 0; i < adjacentVertices.size(); i++)
					System.out.print(" " + adjacentVertices.get(i).vertexIndex);
					
				System.out.println();
			}
			else
			{
				System.out.println("No adjacent vertices");
			}
		}
		
		private class MyArrayList
		{
			public Vertex[] items;
			private int count;
			
			public MyArrayList(int size)
			{
				if(size > 0)
					items = new Vertex[size];
				else
					MyArrayList();
					
				count = 0;
			}
			
			public void MyArrayList()
			{
				items = new Vertex[1];
				count = 0;
			}
			
			public int size()
			{
				return count;
			}
			
			public boolean add(Vertex v)
			{
				if(v == null){
					System.out.println("Null vertex passed");
					return false;
				}
					
				if(count == 0){
					items[count] = v;
				}
				else{
					if(count <= items.length)
					{
						Vertex[] newItems = new Vertex[count+1];
						for(int i = 0; i < count; i++)
						{
							newItems[i] = items[i];
						}
						newItems[count] = v;
						
						items = newItems;
					}
					else
					{
						print("COUNT > ITEMS LENGTH");
					}
				}
					
				count++;
				
				return true;
			}
			
			public Vertex get(int index)
			{
				//print("Index = " + index + " Count=" + count);
				Vertex result = null;
				if(index >= 0 && index < count)
				{
					result = items[index];
				}
				
				return result;
			}
		}
	}
	
	private class MyIntegerHashMap
	{
		public int[] keys;
		public int[] values;
		private int count;
		
		public MyIntegerHashMap()
		{
			keys = new int[1];
			values = new int[1];
			count = 0;
		}
		
		public int[]keySet()
		{
			return keys;
		}
		
		public int[] values()
		{
			return values;
		}
		
		public int size()
		{
			return count;
		}
		
		public boolean containsValue(int value)
		{
			boolean result = false;
			for(int v : values)
			{
				if(v == value)
				{
					result = true;
					break;
				}
			}
			return result;
		}
		
		public boolean containsKey(int key)
		{
			boolean result = false;
			for(int k : keys)
			{
				if(k == key)
				{
					result = true;
					break;
				}
			}
			return result;
		}
		
		public void put(int key, int value)
		{
			if(count == 0)
			{
				keys[count] = key;
				values[count] = value;
			}
			else if(count == keys.length)
			{
				int[] newKeys = new int[count+1];
				int[] newValues = new int[count+1];
				
				for(int i = 0; i < count; i++)
				{
					newKeys[i] = keys[i];
					newValues[i] = values[i];
				}
				newKeys[count] = key;
				newValues[count] = value;
				
				keys = newKeys;
				values = newValues;
			}
			else
			{
				print("MyHashMap Put Function Crashed -RECTIFY");
			}
			
			count++;
		}
		
		public int get(int index)
		{
			int result = -1;
			for(int i = 0; i < keys.length; i++)
			{
				if(keys[i] == index){
					result = values[i];
					break;
				}
			}
			
			return result;
		}
	}
}


