package SCCTask3;

import java.io.*;
import java.util.*;

public class SCC {

	static int[][] adjacencyMatrix;
	static int[][] invertedAdjacencyMatrix;
	static VerticeColor[] coloredVertices;
	/**
	 * Beschreibt ob die SCC eines Knotens bereits gefunden wurde.
	 */
	static boolean[] foundSCC;
	/**
	 * Speichert die finishing Time für die verschiedenen Knoten
	 */
	static int[] finishingTime;
	/**
	 * Counter der die finishing Time über die Rekursion trackt.
	 */
	static int finCounter;

	/**
	 * Diese Variablen werden manipuliert um eine Datei in das Programm zu laden,
	 * aus Zeitgründen musste auf einen richtigen Import leider verzichtet werden.
	 * Damit der Dateinamen verwendet werden kann, muss sich die Datei im
	 * Projektverzeichnis liegen.
	 * 
	 * Anzahl der Knoten des Graphen
	 */
	static int numVertices = 1000; // 1000 / 7
	/**
	 * Diese Variablen werden manipuliert um eine Datei in das Programm zu laden,
	 * aus Zeitgründen musste auf einen richtigen Import leider verzichtet werden.
	 * Damit der Dateinamen verwendet werden kann, muss sich die Datei im
	 * Projektverzeichnis liegen.
	 * 
	 * Dateiname der .csv Datei der Adjazenzmatrix
	 */
	static String filePath = "big_graph.csv"; // big_graph.csv / small_graph.csv

	/**
	 * Enum zur Darstellung der Einfärbung
	 * 
	 * @author jh
	 *
	 */
	enum VerticeColor {
		WHITE, GREY, BLACK
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// Hilfsvariablen für den Import
		String line;
		String[] splitLine;
		int counter = 0;
		BufferedReader br;

		// Variable zum Speichern der Ergebnisse
		ArrayList<ArrayList<Integer>> resultList = new ArrayList<ArrayList<Integer>>();

		// Initialisierung der benötigten Arrays
		initializeArrays();

		// Import via BufferedReader und Parsen zu Int
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

		// Erstellen der invertierten Adjazenzmatrix
		initializeInvAdjMatrix();

		// Initialisieren des foundSCC Arrays
		initializeFoundSCC();

		// Mainloop des Algorithmus wie in der Vorlesung beschrieben
		while (SCCnotFound()) {
			DFS(adjacencyMatrix);
			DFS(invertedAdjacencyMatrix, lookForMaxFinishTime());
			resultList.add(compileSCC());
		}

		// Ausgabe des Ergebnis
		printOutResults(resultList);
	}
	
	/**
	 * stellt eine gefundene SCC aus dem coloredVertices Array zusammen und setzt
	 * die entsprechenden Knoten in foundSCC auf true um zu markieren das dieser
	 * Knoten bereits zu einer SCC gehört
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
	 * Überprüft ob das foundSCC Array noch Knoten
	 * 
	 * @return wahr wenn noch Knoten existieren, falsch wenn alle Knoten zu einer
	 *         SCC zugeordnet sind
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
	 * Sucht den Index mit der größten finishTime
	 * 
	 * @return der Index mit der maximalen finishTime
	 */
	static public int lookForMaxFinishTime() {
		int max = -1;
		int max_index = -1;
		for (int i = 0; i < finishingTime.length; i++) {
			if (finishingTime[i] > max) {
				max = finishingTime[i];
				max_index = i;
			}
		}
		// The max_index is set to -1 to make sure that it doesn't interfere with future
		// values since 0 is a valid value
		finishingTime[max_index] = -1;
		return max_index;
	}

	/**
	 * resets the coloredVertices Array to reuse it in the DFS
	 */
	static public void resetColoredVertices() {
		for (int i = 0; i < coloredVertices.length; i++) {
			coloredVertices[i] = VerticeColor.WHITE;
		}
	}

	/**
	 * Startet DFS ohne bestimmte Quelle
	 * 
	 * @param Adjazenzmatrix bzw. Invertierte Adjazenzmatrix
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
	 * @param Adjazenzmatrix bzw. Invertierte Adjazenzmatrix
	 * @param Index
	 */
	static public void DFS(int[][] matrix, int src) {
		resetColoredVertices();
		finCounter = 0;
		DFSVisit(matrix, src);
		// Zusätzliche Zuweisung um zu verhindern das der zweite DFS Algorithmus den -1
		// Wert im
		// finishingTime Array überschreibt
		finishingTime[src] = -1;
	}

	/**
	 * Rekursion des DFS algorithmus, mit Counter und Zuweisung um die FinishTime zu
	 * tracken
	 * 
	 * @param Adjazenzmatrix bzw. Invertierte Adjazenzmatrix
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
	 * Ausgabefunktion der Resultate des Algorithmus
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
