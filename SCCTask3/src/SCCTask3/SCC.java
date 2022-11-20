package SCCTask3;

import java.io.*;
import java.util.*;

public class SCC {

	// Variablen definieren

	/**
	 * true: SCC hat Knoten bereits gefunden
	 * false: nicht gefunden
	 */
	static boolean[] saw;
	/**
	 * Speichert für jeden Knoten die Zeit (finishing time) ab
	 */
	static int[] timeStorage;
	/**
	 * Zähler, der die finishing time per Rekursion mitzählt
	 */
	static int timeCounter;

	/**
	 * Anzahl der Knoten des Graphen
	 */
	static int numVertices = 1000; 

	static int[][] adjacencyMatrix;
	static int[][] invertedAdjacencyMatrix;
	static VerticeColor[] coloredVertices;

	/**
	 * Enum zur Darstellung der Einfärbung
	 */
	enum VerticeColor {
		WHITE, GREY, BLACK
	}

	/**
	 *
	 * !!!!!!!!!!!!!!!Hinweis:!!!!!!!!!!!!!!!!!!!!!!!
	 * Dateinamen können nur verwendet werden, wenn die Dateien im Projektverzeichnis sind
	 * 
	 */
	
	/** 
	 * Dateiname der .csv Datei der Adjazenzmatrix
	 */
	static String filePath = "big_graph.csv"; // big_graph.csv / small_graph.csv

	public static void main(String[] args) {

		// Importhilfe (Hilfe von Anderen)
		String line;
		String[] splitLine;
		int counter = 0;
		BufferedReader buffered;

		// Speicher der Ergebnisse
		ArrayList<ArrayList<Integer>> resultList = new ArrayList<ArrayList<Integer>>();

		// Initialisierung der benötigten Arrays
		initializeArrays();

		// Import mittles BufferedReader
		// Parsen -> Int
		try {
			buffered = new BufferedReader(new FileReader(filePath));
			while (buffered.ready()) {
				line = buffered.readLine();
				splitLine = line.split(",");
				for (int i = 0; i < numVertices; i++) {
					adjacencyMatrix[counter][i] = Integer.parseInt(splitLine[i]);
				}
				counter++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// invertierten Adjazenzmatrix
		initializeInvAdjMatrix();

		// saw Array
		initializeSaw();

		// Mainloop des Algorithmus (Vorlesung)
		while (notSaw()) {
			DFS(adjacencyMatrix);
			DFS(invertedAdjacencyMatrix, maxFinishTime());
			resultList.add(compileSCC());
		}

		// Ausgabe des Ergebnis
		printOutResults(resultList);
	}
	
	/**
	 * Die folgende Funktion setzt eine gefundene SCC aus dem coloredVertices Array zusammen und stellt
	 * die entsprechenden Knoten in saw auf true um, damit markiert wird, dass die
	 * Knoten bereits zu einer SCC gehören
	 * 
	 * @return Liste von Indices
	 */
	static public ArrayList<Integer> compileSCC() {
		ArrayList<Integer> resultArrayList = new ArrayList<Integer>();
		for (int i = 0; i < coloredVertices.length; i++) {
			if (coloredVertices[i] == VerticeColor.BLACK) {
				resultArrayList.add(i);
				saw[i] = true;
			}
		}

		return resultArrayList;
	}

	/**
	 * Überprüft ob das saw Array noch Knoten hat
	 * 
	 * @return true: Knoten existieren
	 * @return false: wenn alle Knoten zu einer SCC zugeordnet sind
	 */
	static public boolean notSaw() {
		for (int i = 0; i < saw.length; i++) {
			if (!saw[i]) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Index mit der größten finishing time
	 * 
	 * @return Index, mit der maximalen finishing time
	 */
	static public int maxFinishTime() {
		int max = -1;
		int max_index = -1; // Sonderzeichen, um Fehler auszuschließen
		for (int i = 0; i < timeStorage.length; i++) {
			if (timeStorage[i] > max) {
				max = timeStorage[i];
				max_index = i;
			}
		}
		
		timeStorage[max_index] = -1;
		return max_index;
	}

	/**
	 * coloredVertictes Array wird gelöscht, um die DFS neu starten zu können
	 */
	static public void resetColoredVertices() {
		for (int i = 0; i < coloredVertices.length; i++) {
			coloredVertices[i] = VerticeColor.WHITE;
		}
	}

	/**
	 * Startet DFS ohne bestimmte Quelle
	 * 
	 * @param Invertierte Adjazenzmatrix
	 */
	static public void DFS(int[][] matrix) {
		resetColoredVertices();
		timeCounter = 0;
		for (int i = 0; i < numVertices; i++) {
			if (!saw[i] && coloredVertices[i] == VerticeColor.WHITE) {
				DFSVisit(matrix, i);
			}
		}
	}

	/**
	 * Startet DFS mit bestimmter Quelle
	 * 
	 * @param Invertierte Adjazenzmatrix
	 * @param Index
	 */
	static public void DFS(int[][] matrix, int src) {
		resetColoredVertices();
		timeCounter = 0;
		DFSVisit(matrix, src);
		timeStorage[src] = -1;
	}

	/**
	 * Rekursion, für in README beschriebener Zähler
	 * 
	 * @param Invertierte Adjazenzmatrix
	 * @param Index
	 */
	static public void DFSVisit(int[][] matrix, int src) {
		coloredVertices[src] = VerticeColor.GREY;

		for (int i = 0; i < matrix[src].length; i++) {
			if (matrix[src][i] == 1) {
				if (!saw[i] && coloredVertices[i] == VerticeColor.WHITE) {
					DFSVisit(matrix, i);
				}
			}
		}
		coloredVertices[src] = VerticeColor.BLACK;
		timeStorage[src] = timeCounter;
		timeCounter++;
	}
	
	/**
	 * Initialisierung der Arrays
	 */
	static public void initializeArrays() {
		adjacencyMatrix = new int[numVertices][numVertices];
		invertedAdjacencyMatrix = new int[numVertices][numVertices];
		coloredVertices = new VerticeColor[numVertices];
		timeStorage = new int[numVertices];
		saw = new boolean[numVertices];
	}
	
	/**
	 * Initialisierung der invertierten AdjazenzMatrix
	 */
	static public void initializeInvAdjMatrix() {
		for (int i = 0; i < adjacencyMatrix.length; i++) {
			for (int j = 0; j < adjacencyMatrix[i].length; j++) {
				invertedAdjacencyMatrix[j][i] = adjacencyMatrix[i][j];
			}
		}
	}
	
	/**
	 * Initialisierung des saw arrays
	 */
	static public void initializeSaw() {
		for (int i = 0; i < saw.length; i++) {
			saw[i] = false;
		}
	}
	
	/**
	 * Ergebnisse
	 * @param resultList eine Liste der SCCs, jedes SCC hat eine eigene Liste mit allen im SCC enthaltenen Knoten
	 */
	static public void printOutResults(ArrayList<ArrayList<Integer>> resultList) {
		int counter = 0;
		for (ArrayList<Integer> list : resultList) {
			counter = 0;
			for (Integer i : list) {
				if(counter % 30 == 0)
				{
					System.out.println();
				}
				System.out.print(i + ", ");
				counter++;
			}
			System.out.println();
		}
	}
}
