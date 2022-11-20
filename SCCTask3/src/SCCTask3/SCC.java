package SCCTask3;

import java.io.*;
import java.util.*;

public class SCC {

	static int[][] adjacencyMatrix;
	static int[][] invertedAdjacencyMatrix;
	static VerticeColor[] coloredVertices;
	/**
	 * true: SCC hat Knoten bereits gefunden
	 * flase: nicht gefunden
	 */
	static boolean[] foundSCC;
	/**
	 * Speichert für jeden Knoten die finishing Time ab
	 */
	static int[] finishingTime;
	/**
	 * Zähler, der die finishing Time per Rekursion mitzählt
	 */
	static int finCounter;

	/**
	 *
	 * !!!!!!!!!!!!!!!Hinweis:!!!!!!!!!!!!!!!!!!!!!!!
	 * Dateinamen können nur verwendet werden, wenn die Dateien im Projektverzeichnis sind
	 * 
	 * Anzahl der Knoten des Graphen
	 */
	static int numVertices = 1000; 
	 
	 * Dateiname der .csv Datei der Adjazenzmatrix
	 */
	static String filePath = "big_graph.csv"; // big_graph.csv / small_graph.csv

	/**
	 * Enum zur Darstellung der Einfärbung
	 */
	enum VerticeColor {
		WHITE, GREY, BLACK
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// Importhilfe
		String line;
		String[] splitLine;
		int counter = 0;
		BufferedReader br;

		// Speicher der Ergebnisse
		ArrayList<ArrayList<Integer>> resultList = new ArrayList<ArrayList<Integer>>();

		// Initialisierung der benötigten Arrays
		initializeArrays();

		// Import via BufferedReader
		// Parsen zu Int
		try {
			br = new BufferedReader(new FileReader(filePath));
			while (br.ready()) {
				line = br.readLine();
				splitLine = line.split(",");
				for (int i = 0; i < numVertices; i++) {
					adjacencyMatrix[counter][i] = Integer.parseInt(splitLine[i]);
				}
				counter++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// invertierten Adjazenzmatrix
		initializeInvAdjMatrix();

		// foundSCC Array
		initializeFoundSCC();

		// Mainloop des Algorithmus (Vorlesung)
		while (SCCnotFound()) {
			DFS(adjacencyMatrix);
			DFS(invertedAdjacencyMatrix, lookForMaxFinishTime());
			resultList.add(compileSCC());
		}

		// Ausgabe des Ergebnis
		printOutResults(resultList);
	}
	
	/**
	 * Die folgende Funktion setzt eine gefundene SCC aus dem coloredVertices Array zusammen und stellt
	 * die entsprechenden Knoten in foundSCC auf true um, damit markiert wird, dass die
	 * Knoten bereits zu einer SCC gehören
	 * 
	 * @return Liste von Indices
	 */
	static public ArrayList<Integer> compileSCC() {
		ArrayList<Integer> resultArrayList = new ArrayList<Integer>();
		for (int i = 0; i < coloredVertices.length; i++) {
			if (coloredVertices[i] == VerticeColor.BLACK) {
				resultArrayList.add(i);
				foundSCC[i] = true;
			}
		}

		return resultArrayList;
	}

	/**
	 * Überprüft ob das foundSCC Array noch Knoten hat
	 * 
	 * @return true: Knoten existieren
	 * @return false: wenn alle Knoten zu einer SCC zugeordnet sind
	 */
	static public boolean SCCnotFound() {
		for (int i = 0; i < foundSCC.length; i++) {
			if (!foundSCC[i]) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Index mit der größten finishTime
	 * 
	 * @return Index, mit der maximalen finishTime
	 */
	static public int lookForMaxFinishTime() {
		int max = -1;
		int max_index = -1; // Sonderzeichen, um Fehler auszuschließen
		for (int i = 0; i < finishingTime.length; i++) {
			if (finishingTime[i] > max) {
				max = finishingTime[i];
				max_index = i;
			}
		}
		
		finishingTime[max_index] = -1;
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
		finCounter = 0;
		for (int i = 0; i < numVertices; i++) {
			if (!foundSCC[i] && coloredVertices[i] == VerticeColor.WHITE) {
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
		finCounter = 0;
		DFSVisit(matrix, src);
		finishingTime[src] = -1;
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
				if (!foundSCC[i] && coloredVertices[i] == VerticeColor.WHITE) {
					DFSVisit(matrix, i);
				}
			}
		}
		coloredVertices[src] = VerticeColor.BLACK;
		finishingTime[src] = finCounter;
		finCounter++;
	}
	
	/**
	 * Initialisierung der Arrays
	 */
	static public void initializeArrays() {
		adjacencyMatrix = new int[numVertices][numVertices];
		invertedAdjacencyMatrix = new int[numVertices][numVertices];
		coloredVertices = new VerticeColor[numVertices];
		finishingTime = new int[numVertices];
		foundSCC = new boolean[numVertices];
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
	 * Initialisierung des FoundSCC arrays
	 */
	static public void initializeFoundSCC() {
		for (int i = 0; i < foundSCC.length; i++) {
			foundSCC[i] = false;
		}
	}
	
	/**
	 * Ergebnisse
	 * @param resultList eine Liste der SCCs, jedes SCC hat eine eigene Liste mit allen im SCC enthaltenen Knoten
	 */
	static public void printOutResults(ArrayList<ArrayList<Integer>> resultList) {
		int counter = 0;
		for (ArrayList<Integer> al : resultList) {
			counter = 0;
			for (Integer i : al) {
				if(counter % 30 == 0)
				{
					System.out.println();
				}
				System.out.print(i + " ,");
				counter++;
			}
			System.out.println();
		}
	}
}
