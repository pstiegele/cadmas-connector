# Cadmas-Connector

An example connector for Ardupilot based planes to connect to CADMAS - the cloudbased drone management suite.

Der UAV-Connector ist das Bindeglied zwischen Autopilot und APIServer,
welches auf einem Kleinstcomputer verbaut innerhalb des UAVs läuft. Er empfängt
die Telemetrie des Autopiloten und sendet sie im passenden Format an den Server. Außerdem
empfängt er Telekommandos, wandelt diese in das passende Format des Autopiloten
um und gibt sie weiter.

Written by Maximilian Bührer & Paul Stiegele

## Installation von Ardupilot-Connector
1. Zum klonen des Connector-Repositorys ist Git notwendig.
git-scm.com
2. Das Connector-Verzeichnis klonen
git clone https://github.com/pstiegele/cadmas-connector/
3. Zum Bauen wird „Apache Ant“ benötigt. Es kann über sudo apt-get install ant
installiert werden.
4. Anschließend in dem Connector-Verzeichnis ant ausführen
5. Zum Starten des Connectors java -jar "dist/ardupilot connector" ausführen.
Die Kommandozeilenparameter können angehangen werden. (siehe Kapitel A.1.3:
Connector Kommandozeilenparameter). Für den Autostart ist das run.sh-Skript sinnvoll:
Es aktualisiert das Repository, falls Änderungen vorliegen wird der Connector neu
gebaut und anschließend ausgeführt.
