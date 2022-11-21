Aufgabe 3: 
Implementieren Sie einen Algorithmus der, gegeben einen gerichteten Graphen, die starken Zusammenhangskomponenten findet. 

Programmsprache: Java

Idee des Codes:

Als erstes ruft man DFS auf jedem Knoten im Diagramm auf, der Knoten mit der größten saw (Finishing time) muss die Quelle des Diagramms sein.


Daraus folgt, dass derselbe Knoten im transponierten Graphen eine Senke sein muss. 

Wenn dann auf diesen Knoten in der transponierten Matrix eine weitere DFS startet, müsste das Ergebnis stark zusammenhängende Komponenten sein. 

Das Ergebnis schreibt man in eine Liste, dann wird DFS wieder auf dem Knoten mit der höchsten saw (Finishing time) unter den verbleibenden Knoten aufgerufen usw. 

Umdas zu „vereinfachen“ haben wir eine Importfunktion, die mit einem BufferedReader die .csv-Datei auswertet und die darin enthaltene Datei den Graphen in eine Adjazenzmatrix parst.

In unserer Hauptfunktion ist eine Funktion zum Transponieren der gegebenen Matrix, eine Schleife zum Ausführen des oben beschriebenen Konzepts und eine Funktion zum Schreiben und Zurückgeben der gefundenen stark verbundenen Komponenten in einer ArrayList. 

DFS wird auf zwei Arten implementiert. Die erste Version nimmt nur eine AdjacencyMatrix als Parameter, die zweite Version ist eine überladene Funktion, die einen zusätzlichen Parameter nimmt, der bestimmt, auf welchem Knoten das DFS starten soll. 

Wenn DFS aufgerufen wird, wird die Farbe des Knotens auf Weiß zurückgesetzt und timeCounter (wird benötigt, um saw (finishing time) zu verfolgen) wird auf 0 gesetzt. 
Anschließend prüft es, ob der aktuelle Knoten weiß ist und noch keine stark verbundene Komponente gefunden hat, zu der dieser Knoten gehört, falls dies der Fall ist, rufe DFS-Visit auf diesem Knoten auf.

In DFS-Visit wird dieser Knoten dann grau markiert, darauf folgt, dass die Kanten anderer Knoten in AdjacencyMatrix gesucht werden. 
Wird eine Kante gefunden, wird geprüft, ob der zugehörige Knoten weiß ist und nicht zu einer stark zusammenhängenden Komponente gehört, ist dies der Fall, wird DFS-Visit auf dem Nachbarknoten aufgerufen usw. Dies wird fortgesetzt, bis der Pfad vollständig abgelaufen ist. 

Der Code sollte dann den letzten Knoten des Pfads schwarz färben und timeCounter in das dazugehörige Array reinschreiben, das saw (finishing time) speichert.

Wendet in der Hauptschleife des Programms zuerst DFS auf den Graphen an, wendet dann den überladenen DFS auf den transponierten Graphen an und ruft eine Funktion auf, um die maximale saw zu finden, die vom ersten DFS übergeben wurde, und übergibt die relevanten Knoten an den überladenen DFS als Quellverzeichnis. 
Auf diese Weise wird der gesamte Graph durchlaufen und die stark zusammenhängenden Komponenten als ArrayList in eine weitere ArrayList geschrieben und dann ausgegeben.
Hinweis: PNG-Dateien zeigen die Ergebnisse von big_graph. Jede Zeile beschreibt eine stark verbundene Komponente. Wie Sie sehen können, sind die letzten drei zu groß, um sie alle auf einmal anzuzeigen. (Component Graph in einer weiteren PNG-Datei)
