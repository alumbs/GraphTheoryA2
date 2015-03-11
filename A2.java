//Graph Theory A2
import java.io.*;
import java.util.*;

public class A2
{
	static final int EXIT_FAILURE = -1;
	static int DFCount;
	static int[] DFNum;//Used to mark visited vertices
	static int[] Parent;
	static int[] LowPt;
	static MyArrayList cutV;
	//static MyEdgeStack stack;
		
	public static void main(String [] args)
	{
		int maxLength = 0;
		String[] files = {"Cutpt1.txt", "Cutpt2.txt","Cutpt3.txt"};
		
		for(String filename : files)
		{
			Graph G = readInGraph(filename);
			G.printGraph();
			HopcroftTarjan(G);
		}
	}
	
	private static void HopcroftTarjan(Graph G)
	{
		A2 a2 = new A2();
		cutV = a2.new MyArrayList(1);
		//stack = a2.new MyEdgeStack(1);
		
		int numV = G.numVertices;
		for(int i = 1; i <= numV; i++)
		{
			DFNum[i] = 0;
			Parent[i] = 0;
		}
		
		for(int i = 1; i <= numV; i++)
		{
			if(DFNum[i] == 0)
				DFS(i, G.adj);				
		}
		
		System.out.println("\nPrinting Cut Vertices:");
		cutV.printList();
		
		//print blocks
		for(int i = 1; i < DFNum.length; i++)
		{
			DFNum[i] = 0;
		}
		
		int count = 1;
		for(int i : cutV.items)
		{
			if(DFNum[i] == 0)
			{	
				System.out.println("\nPrinting block " + count+":");		
				printBlock(i, G.adj, cutV);	
				count++;			
			}
		}
	}
	
	private static void printBlock(int cutV, int[][]adjM, MyArrayList list)
	{
		DFNum[cutV] = 1;//Visited
		for(int adjV = 1; adjV < adjM.length; adjV++)
		{
			if(adjM[cutV][adjV] == 1 && 
				DFNum[adjV] == 0 && 
				!list.contains(adjV))
			{
				System.out.print(cutV+"-"+adjV + " ");
				printBlock(adjV, adjM, list);
			}
		}
	}
	
	private static void DFS(int u, int[][] adj)
	{
		A2 a2 = new A2();
		DFCount++;
		DFNum[u] = DFCount;
		LowPt[u] = DFCount;//Initial value
		
		for(int v = 1; v < adj.length; v++)
		{
			//if vertex u is adjacent to
			//any vertex i
			if(adj[u][v] == 1)
			{
				if(DFNum[v] == 0)
				{
					//System.out.println("Visited first time " + v);
					// v has not been visited yet
					Parent[v] = u;
					//stack.add(a2.new Edge(u, v));
					LowPt[v] = DFNum[u];//Initial value
					DFS(v, adj);
					
					//LowPt[v] is now known
					if(LowPt[v] >= DFNum[u])
					{
						//Check if any other vertex is unvisited
						if(hasUnvisitedAdjV(u, adj) && !cutV.contains(u))
						{
							cutV.add(u);
							//System.out.println("CUT-V v= " +v+" u="+u);					
						}
					}
					else //LowPt[v] < DFNum[u]
					{
						if(LowPt[v] < LowPt[u])
							LowPt[u] = LowPt[v];
					}						
				}
				else //v has been visited
				{
					//System.out.println("V already visited " + v);
					if(v != Parent[u])
					{
						if(DFNum[v] < DFNum[u])
						{
							//stack.add(a2.new Edge(u, v));
							if(DFNum[v] < LowPt[u])
								LowPt[u] = DFNum[v];
						}
					}
				}
			}
		}
	}
	
	private static boolean hasUnvisitedAdjV(int u, int[][]adj)
	{
		for(int i = 1; i < adj.length; i++)
		{
			if(adj[u][i] == 1 && DFNum[i] == 0)
			{
				return true;
			}
		}
		return false;
	}
	
	private static Graph readInGraph(String fileName)
	{
		int numVerticesOfGraph = 0;
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
			
			//Re-initialize all the global vars
			numVerticesOfGraph++;
			DFNum = new int[numVerticesOfGraph];
			Parent = new int[numVerticesOfGraph];
			DFCount = 0;
			LowPt = new int[numVerticesOfGraph];
			
			//Keep going as long as we have valid input
			while((line = br.readLine()) != null && !line.isEmpty()){
				//System.out.println("line = " + line);
				splitTemp = line.split(" ");
				String vertex = splitTemp[0].substring(1);//Remove the first char
				int vertexIndex = 0;
				
				if(!vertex.isEmpty()){
					//The vertex we are at
					vertexIndex = Integer.parseInt(vertex); 
				}
				
				//Read the list of adjacent vertices
				for(int i = 1; i < splitTemp.length; i++)
				{
					G.addAdjacent(vertexIndex, 
						Integer.parseInt(splitTemp[i]));
				}
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
		String graphName;
		int numVertices; //No. of vertices
		int[][] adj; //Adjacency list
		
		public Graph(String name, int numV) throws Exception
		{
			graphName = name;
			
			if(numV <= 0)
				throw new Exception("Invalid value");
				
			numVertices = numV;
			numV++;
			adj = new int[numV][numV];
		}
		
		public void addAdjacent(int i, int j)
		{			
			//Vertices start from 1
			if(i <= 0 || j <= 0)
				return; 
				
			if(i > numVertices || j > numVertices)
				return;
				
			adj[i][j] = 1;
			adj[j][i] = 1;
		}
		
		public void printGraph()
		{
			for(int i= 1; i <= numVertices; i++)
			{
				System.out.println("");
				for(int j=1; j <= numVertices; j++)
				{
					System.out.print(adj[i][j] + " ");
				}
			}
		}
	}
	
	private class MyArrayList
	{
		public int[] items;
		private int count;
		
		public MyArrayList(int size)
		{
			if(size > 0)
				items = new int[size];
			else
				MyArrayList();
				
			count = 0;
		}
		
		public void MyArrayList()
		{
			items = new int[1];
			count = 0;
		}
		
		public boolean contains(int item)
		{
			for(int i : items)
			{
				if(i == item)
					return true;
			}
			return false;
		}
		
		public int size()
		{
			return count;
		}
		
		public void printList()
		{
			for(int i : items)
				System.out.print(i + " ");
		}
		
		public boolean add(int v)
		{
			if(v < 0){
				System.out.println("Null vertex passed");
				return false;
			}
				
			if(count == 0){
				items[count] = v;
			}
			else{
				if(count <= items.length)
				{
					int[] newItems = new int[count+1];
					for(int i = 0; i < count; i++)
					{
						newItems[i] = items[i];
					}
					newItems[count] = v;
					
					items = newItems;
				}
				else
				{
					System.out.println("COUNT > ITEMS LENGTH");
				}
			}
				
			count++;
			
			return true;
		}
	}
}
